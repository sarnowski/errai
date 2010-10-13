package org.jboss.errai.workspaces.client.api;

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
