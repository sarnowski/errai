
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

package org.jboss.errai.workspaces.client.framework;

import org.jboss.errai.workspaces.client.api.ToolSet;
import org.jboss.errai.workspaces.client.api.WidgetProvider;

public interface ToolContainer
{
  void addToolSet(ToolSet toolSet);

  void addTool(String group, String name, String icon,
                             boolean multipleAllowed, int priority, WidgetProvider component);

  void addTool(String group, String name, String icon,
                                                        boolean multipleAllowed, int priority, WidgetProvider component, String[] renderIfRoles);

  void addExtension(String location,
                    int priority,
                    boolean loggedIn,
                    boolean loggedOff,
                    WidgetProvider component);

  void setLoginComponent(WidgetProvider loginComponent);

  void setPreferredGroupOrdering(String[] groups);

}
