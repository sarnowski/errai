package org.jboss.errai.workspaces.client.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a WidgetProvider to be responsible for an extension widget.
 *
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
 *
 * @author Tobias Sarnowski
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExtensionWidget {

    /**
     * Describes the position of the extension widget.
     *
     * @return the location
     */
    Location location();

    /**
     * Extensions with more priority will be nearer to the workspace.
     *
     * @return the priority, defaults to 50
     */
    int priority() default 50;

    /**
     * If the widget should be displayed while logged in.
     *
     * @return display the widget?
     */
    boolean loggedIn() default true;

    /**
     * If the widget should be displayed while not logged in.
     *
     * @return display the widget?
     */
    boolean loggedOff() default false;


    /**
     * See {@link org.jboss.errai.workspaces.client.api.annotations.ExtensionWidget} for detailed explanation of the
     * values.
     */
    public static enum Location {
        BOTTOM,
        LEFT,
        RIGHT,
        TOP
    }
}
