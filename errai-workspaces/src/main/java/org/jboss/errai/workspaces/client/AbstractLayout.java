/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.errai.workspaces.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import org.jboss.errai.bus.client.security.SecurityService;
import org.jboss.errai.common.client.framework.WSComponent;
import org.jboss.errai.workspaces.client.framework.Tool;
import org.jboss.errai.workspaces.client.framework.ToolImpl;
import org.jboss.errai.workspaces.client.framework.ToolProvider;
import org.jboss.errai.workspaces.client.framework.ToolSet;
import org.jboss.errai.workspaces.client.icons.ErraiImageBundle;

import java.util.*;

/**
 * Basic workspace abstraction. Merely the ToolContainer impl.
 */
public class AbstractLayout implements ToolContainer
{
  private static ErraiImageBundle erraiImageBundle = GWT.create(ErraiImageBundle.class);
  protected static List<ToolSet> toBeLoaded = new ArrayList<ToolSet>();
  protected static Map<String, List<ToolProvider>> toBeLoadedGroups = new HashMap<String, List<ToolProvider>>();
  protected static List<String> preferredGroupOrdering = new ArrayList<String>();
  protected static Set<String> sessionRoles = new HashSet<String>();
  protected static int toolCounter = 0;
  private static SecurityService securityService = new SecurityService();

  @Override
  public void setLoginComponent(WSComponent loginComponent) {
    //this.loginComponent = loginComponent;
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void setPreferredGroupOrdering(String[] groups) {
    preferredGroupOrdering.addAll(Arrays.asList(groups));
  }

  @Override
  public  void addToolSet(ToolSet toolSet) {
    toBeLoaded.add(toolSet);
  }

  @Override
  public void addTool(String group, String name, String icon,
                      boolean multipleAllowed, int priority, WSComponent component) {
    if (!toBeLoadedGroups.containsKey(group)) toBeLoadedGroups.put(group, new ArrayList<ToolProvider>());

    final String toolId = name.replaceAll(" ", "_") + "." + toolCounter++;

    Image img;
    if (icon == null || "".equals(icon)) {
      img = new Image(erraiImageBundle.application());
    } else
      img = new Image(GWT.getModuleBaseURL() + icon);

    final Tool toolImpl = new ToolImpl(name, toolId, multipleAllowed, img, component);
    ToolProvider provider = new ToolProvider() {
      public Tool getTool() {
        return toolImpl;
      }
    };

    toBeLoadedGroups.get(group).add(provider);
  }

  @Override
  public void addTool(String group, String name, String icon,
                      boolean multipleAllowed, int priority, WSComponent component, final String[] renderIfRoles) {
    if (!toBeLoadedGroups.containsKey(group)) toBeLoadedGroups.put(group, new ArrayList<ToolProvider>());

    final String toolId = name.replaceAll(" ", "_") + "." + toolCounter++;
    Image img;
    if (icon == null || "".equals(icon)) {
      img = new Image(erraiImageBundle.application());
    } else
      img = new Image(GWT.getModuleBaseURL() + icon);

    final Set<String> roles = new HashSet<String>();

    for (String role : renderIfRoles) {
      roles.add(role.trim());
    }


    final Tool toolImpl = new ToolImpl(name, toolId, multipleAllowed,
        img, component);
    ToolProvider provider = new ToolProvider() {
      public Tool getTool() {
        if (sessionRoles.containsAll(roles)) {
          return toolImpl;
        } else {
          return null;
        }
      }
    };

    toBeLoadedGroups.get(group).add(provider);
  }

  public static SecurityService getSecurityService() {
    return securityService;
  }

  public static void forceReload()
  {
    reload();  
  }

  private native static void reload() /*-{
       $wnd.location.reload();
     }-*/;

  private native static void _initAfterWSLoad() /*-{
         try {
             $wnd.initAfterWSLoad();
         }
         catch (e) {
         }
     }-*/;
}
