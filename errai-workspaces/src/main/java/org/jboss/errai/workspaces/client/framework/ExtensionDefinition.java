package org.jboss.errai.workspaces.client.framework;

import org.jboss.errai.workspaces.client.api.Extension;
import org.jboss.errai.workspaces.client.api.ProvisioningCallback;
import org.jboss.errai.workspaces.client.api.WidgetProvider;

/**
 * Just a dataholder.
 *
 * @author Tobias Sarnowski
 * @since 1.1
 */
class ExtensionDefinition implements Extension {

    private Extension.Location location;
    private int priority;
    private boolean loggedIn;
    private boolean loggedOff;
    private WidgetProvider widgetProvider;

    ExtensionDefinition(
        Extension.Location location,
        int priority,
        boolean loggedIn,
        boolean loggedOff,
        WidgetProvider widgetProvider) {

        this.location = location;
        this.priority = priority;
        this.loggedIn = loggedIn;
        this.loggedOff = loggedOff;
        this.widgetProvider = widgetProvider;
    }

    public Extension.Location getLocation() {
        return location;
    }

    public int getPriority() {
        return priority;
    }

    public boolean forLoggedIn() {
        return loggedIn;
    }

    public boolean forLoggedOff() {
        return loggedOff;
    }

    public void provideWidget(ProvisioningCallback callback) {
        widgetProvider.provideWidget(callback);
    }
}
