/*
 * Copyright 2010 JBoss, a divison Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.workspaces.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwt.mosaic.ui.client.DeckLayoutPanel;
import org.gwt.mosaic.ui.client.DecoratedTabLayoutPanel;
import org.gwt.mosaic.ui.client.layout.LayoutPanel;
import org.gwt.mosaic.ui.client.util.WidgetHelper;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.security.AuthenticationContext;
import org.jboss.errai.bus.client.security.SecurityService;
import org.jboss.errai.workspaces.client.api.Extension;
import org.jboss.errai.workspaces.client.api.ProvisioningCallback;
import org.jboss.errai.workspaces.client.api.ResourceFactory;
import org.jboss.errai.workspaces.client.api.Tool;
import org.jboss.errai.workspaces.client.api.ToolSet;
import org.jboss.errai.workspaces.client.framework.Registry;
import org.jboss.errai.workspaces.client.icons.ErraiImageBundle;
import org.jboss.errai.workspaces.client.protocols.LayoutCommands;
import org.jboss.errai.workspaces.client.protocols.LayoutParts;
import org.jboss.errai.workspaces.client.util.LayoutUtil;
import org.jboss.errai.workspaces.client.widgets.WSToolSetLauncher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jboss.errai.workspaces.client.api.Extension.*;
import static org.jboss.errai.workspaces.client.api.Extension.Location.*;

/**
 * Maintains {@link Tool}'s
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 * @author Tobias Sarnowski
 */
public class Workspace extends DeckLayoutPanel implements RequiresResize {
    public static final String SUBJECT = "Workspace";
    private Menu menu;

    private static List<ToolSet> toolSets = new ArrayList<ToolSet>();

    private static List<Extension> bottomExtensions = new ArrayList<Extension>();
    private static List<Extension> leftExtensions = new ArrayList<Extension>();
    private static List<Extension> rightExtensions = new ArrayList<Extension>();
    private static List<Extension> topExtensions = new ArrayList<Extension>();

    private static Workspace instance;

    public static Workspace createInstance(Menu menu) {
        if (null == instance)
            instance = new Workspace(menu);

        return instance;
    }

    public static Workspace getInstance() {
        return instance;
    }

    private Workspace(Menu menu) {
        super();

        this.menu = menu;
        this.setPadding(5);

        ErraiBus.get().subscribe(
            Workspace.SUBJECT,
            new MessageCallback() {
                public void callback(final Message message) {
                    switch (LayoutCommands.valueOf(message.getCommandType())) {
                        case ActivateTool:
                            String toolsetId = message.get(String.class, LayoutParts.TOOLSET);
                            String toolId = message.get(String.class, LayoutParts.TOOL);

                            showToolSet(toolsetId, toolId);

                            // create browser history
                            recordHistory(toolsetId, toolId);

                            break;
                    }
                }
            }
        );

        // handle browser history
        History.addValueChangeHandler(
            new ValueChangeHandler<String>() {
                public void onValueChange(ValueChangeEvent<String> event) {
                    // avoid interference with other history tokens
                    // example token: errai_Administration;Users.5

                    String tokenString = event.getValue();
                    if (tokenString.startsWith("errai_")) {
                        String[] tokens = splitHistoryToken(tokenString);

                        String toolsetId = tokens[0];
                        String toolId = tokens[1].equals("none") ? null : tokens[1];

                        showToolSet(toolsetId, toolId);

                        // correlation id
                        if (tokens.length > 2) {
                            String corrId = tokens[3];
                            // not used at the moment
                        }
                    }
                }
            }
        );
    }

    private void recordHistory(String toolsetId, String toolId) {
        String toolToken = toolId != null ? toolId : "none";
        String historyToken = "errai_" + toolsetId + ";" + toolToken;
        History.newItem(historyToken, false);
    }

    public static String[] splitHistoryToken(String tokenString) {
        String s = tokenString.substring(6, tokenString.length());
        String[] token = s.split(";");
        return token;
    }

    public void addToolSet(ToolSet toolSet) {

        // register for lookup, late reference
        toolSets.add(toolSet);
        String toolSetId = encode(toolSet.getToolSetName());

        // Menu
        menu.addLauncher(new WSToolSetLauncher(toolSetId, toolSet), toolSet.getToolSetName());
        menu.getStack().layout();

        // ToolSet deck
        ToolSetDeck deck = new ToolSetDeck(toolSetId, toolSet);
        deck.index = this.getWidgetCount();
        this.add(deck);
    }

    public boolean hasToolSet(String id) {
        return findToolSet(id) != null;
    }

    public void showToolSet(final String id) {
        showToolSet(id, null);  // opens the default tool
    }

    public void showToolSet(final String toolSetId, final String toolId) {
        ToolSetDeck deck = findToolSet(toolSetId);
        if (null == deck)
            throw new IllegalArgumentException("No such toolSet: " + toolSetId);

        // select tool
        ToolSet selectedToolSet = deck.toolSet;
        Tool selectedTool = null;
        if (toolId != null)  // particular tool
        {
            for (Tool t : selectedToolSet.getAllProvidedTools()) {
                if (toolId.equals(t.getId())) {
                    selectedTool = t;
                    break;
                }
            }
        } else  // default tool, the first one
        {
            Tool[] availableTools = selectedToolSet.getAllProvidedTools();
            if (availableTools == null || availableTools.length == 0)
                throw new IllegalArgumentException("Empty toolset: " + toolSetId);

            selectedTool = availableTools[0];
        }

        // is it already open?
        boolean isOpen = false;
        for (int i = 0; i < deck.tabLayout.getWidgetCount(); i++) {
            ToolTabPanel toolTab = (ToolTabPanel) deck.tabLayout.getWidget(i);
            if (toolTab.toolId.equals(selectedTool.getId())) {
                isOpen = true;
                deck.tabLayout.selectTab(i);
            }
        }

        if (!isOpen) // & selectedTool.multipleAllowed()==false
        {
            final ToolTabPanel panelTool = new ToolTabPanel(toolSetId, selectedTool);
            panelTool.invalidate();

            ResourceFactory resourceFactory = GWT.create(ResourceFactory.class);
            ErraiImageBundle erraiImageBundle = GWT.create(ErraiImageBundle.class);
            ImageResource resource = resourceFactory.createImage(selectedTool.getName()) != null ?
                resourceFactory.createImage(selectedTool.getName()) : erraiImageBundle.application();

            deck.tabLayout.add(
                panelTool,
                AbstractImagePrototype.create(resource).getHTML() + "&nbsp;" + selectedTool.getName(),
                true
            );


            deck.tabLayout.selectTab(
                deck.tabLayout.getWidgetCount() - 1
            );

            DeferredCommand.addCommand(new Command() {

                public void execute() {
                    panelTool.onResize();
                }
            });
        }

        // display toolset
        this.showWidget(deck.index);
        this.layout();

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                menu.toggle(toolSetId);
            }
        });
    }

    private ToolSetDeck findToolSet(String id) {
        ToolSetDeck match = null;
        for (int i = 0; i < this.getWidgetCount(); i++) {
            ToolSetDeck deck = (ToolSetDeck) this.getWidget(i);
            if (id.equals(deck.toolSetId)) {
                match = deck;
                break;
            }
        }

        return match;
    }

    public List<ToolSet> getToolsets() {
        /*List<ToolSetRef> result = new ArrayList<ToolSetRef>(this.getWidgetCount());
       for(int i=0; i<this.getWidgetCount(); i++)
       {
         ToolSetDeck deck = (ToolSetDeck) this.provideWidget(i);
         ToolSet toolSet = deck.toolSet;
         result.add(new ToolSetRef(toolSet.getToolSetName(), editor.getEditorId()));
       } */

        return toolSets;
    }

    public static String encode(String toolSetName) {
        return "ToolSet_" + toolSetName.replace(" ", "_");
    }

    /**
     * A group of tools that belong to the same context.
     * In this case represented as a {@link org.gwt.mosaic.ui.client.TabLayoutPanel}.
     */
    private class ToolSetDeck extends LayoutPanel implements RequiresResize, ProvidesResize {
        ToolSet toolSet;
        int index;
        String toolSetId;

        DecoratedTabLayoutPanel tabLayout;

        public ToolSetDeck(String toolSetId, ToolSet toolSet) {
            super();
            this.toolSet = toolSet;
            this.toolSetId = toolSetId;
            this.tabLayout = new DecoratedTabLayoutPanel();

            this.tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
                public void onSelection(SelectionEvent<Integer> selectionEvent) {
                    ToolTabPanel toolTab = (ToolTabPanel) tabLayout.getWidget(selectionEvent.getSelectedItem());
                    recordHistory(toolTab.toolsetId, toolTab.toolId);
                }
            });

            this.add(tabLayout);
        }

        public void onResize() {
            setPixelSize(getParent().getOffsetWidth(), getParent().getOffsetHeight());
            LayoutUtil.layoutHints(tabLayout);
        }
    }

    /**
     * A tabpanel within a {@link org.jboss.errai.workspaces.client.Workspace.ToolSetDeck}
     * that contains a single tool.
     */
    private class ToolTabPanel extends LayoutPanel implements RequiresResize, ProvidesResize {
        String toolId;
        String toolsetId;

        ToolTabPanel(final String toolsetId, final Tool tool) {
            this.toolsetId = toolsetId;
            this.toolId = tool.getId();
            tool.provideWidget(new ProvisioningCallback() {
                public void onSuccess(Widget instance) {
                    String baseRef = toolsetId + ";" + toolId;
                    instance.getElement().setAttribute("baseRef", baseRef); // used by history management & perma links
                    add(instance);
                    WidgetHelper.invalidate(instance);
                    layout();
                }

                public void onUnavailable() {
                    throw new RuntimeException("Failed to load tool: " + tool.getId());
                }
            });
        }

        public void onResize() {
            setPixelSize(getParent().getOffsetWidth(), getParent().getOffsetHeight());
            LayoutUtil.layoutHints(this);
        }
    }

    public void onResize() {
        LayoutUtil.layoutHints(this);
    }

    /*public final class ToolSetRef
   {
     String title;
     String id;

     public ToolSetRef(String title, String id)
     {
       this.title = title;
       this.id = id;
     }
   } */

    public void addExtensions(List<Extension> extensions) {
        AuthenticationContext authContext = Registry.get(SecurityService.class).getAuthenticationContext();

        // FIXME: is that correct??
        boolean loggedIn = authContext.isValid();

        for (Extension extension: extensions) {
            if (!extension.forLoggedIn() && !extension.forLoggedOff()) {
                throw new IllegalArgumentException("Extension is disabled in every case");
            }

            if (loggedIn && !extension.forLoggedIn()) {
                // user authenticated but widget is only for guests
                continue;
            } else if (!loggedIn && !extension.forLoggedOff()) {
                // user not authenticated but widget is for authenticated users only
                continue;
            }

            // add it to the lists
            switch (extension.getLocation()) {
                case BOTTOM: bottomExtensions.add(extension); break;
                case LEFT: leftExtensions.add(extension); break;
                case RIGHT: rightExtensions.add(extension); break;
                case TOP: topExtensions.add(extension); break;
            }
        }

        Collections.sort(bottomExtensions, COMPARE_PRIORITY_INVERSE);
        Collections.sort(leftExtensions, COMPARE_PRIORITY);
        Collections.sort(rightExtensions, COMPARE_PRIORITY_INVERSE);
        Collections.sort(topExtensions, COMPARE_PRIORITY);
    }
}
