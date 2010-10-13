package org.jboss.errai.workspaces.client.api.annotations;

import org.jboss.errai.workspaces.client.api.Extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a WidgetProvider to be responsible for an extension widget.
 *
 * @see org.jboss.errai.workspaces.client.api.Extension.Location
 * @author Tobias Sarnowski
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadExtension {

    /**
     * Describes the position of the extension widget.
     *
     * @return the location
     */
    Extension.Location location();

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
}
