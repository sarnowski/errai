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
package org.jboss.errai.workspaces.client.api;

import java.util.Comparator;

/**
 * Defines a configured and initialized widget extension at runtime.
 *
 * <i>For building extension widgets, see {@link org.jboss.errai.workspaces.client.api.annotations.LoadExtension}.</i>
 *
 * @author Tobias Sarnowski
 * @since 1.1
 */
public interface Extension extends WidgetProvider {

    /**
     * Compares two extensions' priorities.
     */
    public static final Comparator<Extension> COMPARE_PRIORITY = new Comparator<Extension>(){
        public int compare(Extension o1, Extension o2) {
            return o1.getPriority() - o2.getPriority();
        }
    };

    /**
     * Compares two extensions' priorities.
     */
    public static final Comparator<Extension> COMPARE_PRIORITY_INVERSE = new Comparator<Extension>(){
        public int compare(Extension o1, Extension o2) {
            return o2.getPriority() - o1.getPriority();
        }
    };

    /**
     * Where to display the widget.
     *
     * @see Location
     * @return the widget's location
     */
    Location getLocation();

    /**
     * In which order the extension should be displayed.
     *
     * @see org.jboss.errai.workspaces.client.api.annotations.LoadExtension
     * @return the widget's priority
     */
    int getPriority();

    /**
     * @see org.jboss.errai.workspaces.client.api.annotations.LoadExtension
     * @return if the widget will be displayed while logged in
     */
    boolean forLoggedIn();

    /**
     * @see org.jboss.errai.workspaces.client.api.annotations.LoadExtension
     * @return if the widget will be displayed while not logged in
     */
    boolean forLoggedOff();


    /**
     * The Location will be used to place the widget in on of the following boxes:
     * <pre>
     * ^----------------------------------------------^
     * |                  TOP                         |
     * |----------------------------------------------|
     * |  LEFT  |       workspace           |  RIGHT  |
     * |----------------------------------------------|
     * |                 BOTTOM                       |
     * ^----------------------------------------------^
     * </pre>
     */
    public static enum Location {
        BOTTOM,
        LEFT,
        RIGHT,
        TOP
    }
}
