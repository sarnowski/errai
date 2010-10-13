/*
 * Copyright 2010 Tobias Sarnowski
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
