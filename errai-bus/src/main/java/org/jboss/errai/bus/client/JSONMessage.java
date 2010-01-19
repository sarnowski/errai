/*
 * Copyright 2009 JBoss, a divison Red Hat, Inc
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

package org.jboss.errai.bus.client;

import org.jboss.errai.bus.client.protocols.MessageParts;
import org.jboss.errai.common.client.json.JSONEncoderCli;
import org.jboss.errai.common.client.types.TypeHandlerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * CommandMessage represents a message payload.  It implements a builder (or fluent) API which is used for constructing
 * sendable messages.  It is the core messageing API for ErraiBus, and will be the foremost used class within ErraiBus
 * by most users.
 * <p/>
 * <strong>Example Message:</strong>
 * <tt><pre>
 * CommandMessage msg = CommandMessage.create()
 *                          .toSubject("Foo")
 *                          .set("Text", "I like chocolate cake.");
 * </pre></tt>
 * You can transmit a message using the the <tt>sendNowWith()</tt> method by providing an instance of
 * {@link MessageBus}.
 * <p/>
 * Messages can be contructed using user-defined standard protocols through the use of enumerations. Both
 * <tt>commandType</tt> and message parts can be defined through the use of enumerations.  This helps create
 * strongly-defined protocols for communicating with services.  For instance:
 * <tt><pre>
 * public enum LoginParts {
 *    Username, Password
 * }
 * </pre></tt>
 * .. and ..
 * <tt><pre>
 * public enum LoginCommands {
 *    Login, Logout
 * }
 * </pre></tt>
 * These enumerations can than be directly used to build messages and decode incoming messages by a service. For example:
 * <tt><pre>
 *  CommandMessage.create()
 *      .command(LoginCommands.Login)
 *      .set(LoginParts.Username, "foo")
 *      .set(LoginParts.Password, "bar )
 *      .sendNowWith(busInstance);
 * </pre></tt>
 * Messages may contain serialized objects provided they meet the following criteria:
 * <ol>
 * <li>The class is annotated with {@link org.jboss.errai.bus.server.annotations.ExposeEntity}</li>
 * <li>The class implements {@link java.io.Serializable}.
 * <li>The class contains a default, no-argument constructor.
 * </ol>
 *
 * @see ConversationMessage
 */
public class JSONMessage extends CommandMessage implements HasEncoded {
    protected StringBuffer buf = new StringBuffer();
    protected boolean first = true;
    protected boolean committed;

    /**
     * Create a new CommandMessage.
     *
     * @return a new instance of CommandMessage
     */

    static JSONMessage create() {
        return new JSONMessage();
    }

    protected JSONMessage() {
        _start();
    }
    
    /**
     * Return the specified command type.  Returns <tt>null</tt> if not specified.
     *
     * @return - String representing the command type.
     */
    public String getCommandType() {
        return (String) parts.get(MessageParts.CommandType.name());
    }

    /**
     * Return the specified message subject.
     *
     * @return
     */
    public String getSubject() {
        return String.valueOf(parts.get(MessageParts.ToSubject.name()));
    }

    /**
     * Set the subject which is the intended recipient of the message.
     *
     * @param subject - subject name.
     * @return -
     */
    public Message toSubject(String subject) {
        _addStringPart(MessageParts.ToSubject.name(), subject);
        parts.put(MessageParts.ToSubject.name(), subject);
        return this;
    }

    /**
     * Set the optional command type.
     *
     * @param type
     * @return
     */
    public Message command(Enum type) {
        _addStringPart(MessageParts.CommandType.name(), type.name());
        parts.put(MessageParts.CommandType.name(), type.name());
        return this;
    }

    /**
     * Set the optional command type.
     *
     * @param type
     * @return
     */
    public Message command(String type) {
        _addStringPart(MessageParts.CommandType.name(), type);
        parts.put(MessageParts.CommandType.name(), type);
        return this;
    }

    /**
     * Set a message part to the specified value.
     *
     * @param part  - Mesage part
     * @param value - Value instance
     * @return -
     */
    public Message set(Enum part, Object value) {
        _addObjectPart(part.name(), value);
        return set(part.name(), value);
    }

    /**
     * Set a message part to the specified value.
     *
     * @param part  - Mesage part
     * @param value - Value instance
     * @return -
     */
    public Message set(String part, Object value) {
        _addObjectPart(part, value);
        parts.put(part, value);
        return this;
    }

    public void remove(String part) {
        throw new UnsupportedOperationException();
        //  parts.remove(part);
    }

    public void remove(Enum part) {
        throw new UnsupportedOperationException();
    }

    /**
     * Copy the same value from the specified message into this message.
     *
     * @param part    - Part to copy
     * @param message - CommandMessage to copy from
     * @return -
     */
    public Message copy(Enum part, Message message) {
        set(part, message.get(Object.class, part));
        return this;
    }

    /**
     * Copy the same value from the specified message into this message.
     *
     * @param part    - Part to copy
     * @param message - CommandMessage to copy from
     * @return -
     */
    public Message copy(String part, Message message) {
        set(part, message.get(Object.class, part));
        return this;
    }

    public void setFlag(RoutingFlags flag) {
        routingFlags |= flag.flag();
    }

    public void unsetFlag(RoutingFlags flag) {
        if ((routingFlags & flag.flag()) != 0) {
            routingFlags ^= flag.flag();
        }
    }

    public boolean isFlagSet(RoutingFlags flag) {
        return (routingFlags & flag.flag()) != 0;
    }


    /**
     * Get the specified message part in the specified type.  A <tt>ClassCastException</tt> is thrown if the value
     * cannot be coerced to the specified type.
     *
     * @param type - Type to be returned.
     * @param part - Message part.
     * @param <T>  - Type to be returned.
     * @return - Value in the specified type.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public <T> T get(Class<T> type, Enum part) {
        //noinspection unchecked
        Object value = parts.get(part.toString());
        return value == null ? null : (T) TypeHandlerFactory.convert(value.getClass(), type, value);
    }

    /**
     * Get the specified message part in the specified type.  A <tt>ClassCastException</tt> is thrown if the value
     * cannot be coerced to the specified type.
     *
     * @param type - Type to be returned.
     * @param part - Message part.
     * @param <T>  - Type to be returned.
     * @return - Value in the specified type.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public <T> T get(Class<T> type, String part) {
        //noinspection unchecked
        Object value = parts.get(part);
        return value == null ? null : (T) TypeHandlerFactory.convert(value.getClass(), type, value);
    }

    /**
     * Returns true if the specified part is defined in the message.
     *
     * @param part - Message part.
     * @return - boolean value indiciating whether or not specified part is present in the message.
     */
    public boolean hasPart(Enum part) {
        return hasPart(part.name());
    }

    /**
     * Returns true if the specified part is defined in the message.
     *
     * @param part - Message part.
     * @return - boolean value indiciating whether or not specified part is present in the message.
     */
    public boolean hasPart(String part) {
        return parts.containsKey(part);
    }

    /**
     * Return a Map of all the specified parts.
     *
     * @return - A Map of parts.
     */
    public Map<String, Object> getParts() {
        return parts;
    }

    /**
     * Set the message to contain the specified parts.  Note: This overwrites any existing message contents.
     *
     * @param parts - Parts to be used in the message.
     * @return -
     */
    public Message setParts(Map<String, Object> parts) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add the specified parts to the message.
     *
     * @param parts - Parts to be added to the message.
     * @return -
     */
    public Message addAllParts(Map<String, Object> parts) {
        throw new UnsupportedOperationException();
    }

    /**
     * Set a transient resource.  A resource is not transmitted beyond the current bus scope.  It can be used for
     * managing the lifecycle of a message within a bus.
     *
     * @param key - Name of resource
     * @param res - Instance of resouce
     * @return -
     */
    public Message setResource(String key, Object res) {
        if (this.resources == null) this.resources = new HashMap<String, Object>();
        this.resources.put(key, res);
        return this;
    }

    /**
     * Obtain a transient resource based on the specified key.
     *
     * @param key - Name of resource.
     * @return - Instancee of resource.
     */
    public <T> T getResource(Class<T> type, String key) {
        return (T) (this.resources == null ? null : this.resources.get(key));
    }

    /**
     * Copy a transient resource to this mesage from the specified message.
     *
     * @param key      - Name of resource.
     * @param copyFrom - Message to copy from.
     * @return
     */
    public Message copyResource(String key, Message copyFrom) {
        if (!copyFrom.hasResource(key)) {
            throw new RuntimeException("Cannot copy resource '" + key + "': no such resource.");
        }
        setResource(key, copyFrom.getResource(Object.class, key));
        return this;
    }


    public Message errorsCall(ErrorCallback callback) {
        if (this.errorsCall != null) {
            throw new RuntimeException("An ErrorCallback is already registered");
        }
        this.errorsCall = callback;
        return this;
    }


    public ErrorCallback getErrorCallback() {
        return errorsCall;
    }

    /**
     * Returns true if the specified transient resource is present.
     *
     * @param key - Name of resouce
     * @return - boolean value indicating if the specified resource is present in the message.
     */
    public boolean hasResource(String key) {
        return this.resources != null && this.resources.containsKey(key);
    }

    /**
     * Add the Map of resources to the message.
     *
     * @param resources - Map of resource
     */
    public void addResources(Map<String, ?> resources) {
        if (this.resources == null) this.resources = new HashMap<String, Object>(resources);
        else {
            this.resources.putAll(resources);
        }
    }

    /**
     * Transmit this message to the specified {@link MessageBus} instance.
     *
     * @param viaThis
     */
    public void sendNowWith(MessageBus viaThis) {
        viaThis.send(this);
    }

    /**
     * Transmit this message using the specified {@link RequestDispatcher}.
     *
     * @param viaThis
     */
    public void sendNowWith(RequestDispatcher viaThis) {
        viaThis.dispatch(this);
    }

    @Override
    public String getEncoded() {
        _end();
        return buf.toString();
    }

    @Override
    public String toString() {
        return "CommandMessage(toSubject=" + getSubject() + ";CommandType=" + getCommandType() + ")";
    }

    protected void _start() {
        first = true;
        buf.append("{");
    }

    protected void _end() {
        committed = true;
        buf.append("}");
    }


    protected void _sep() {
        if (first) {
            first = false;
        } else {
            buf.append(',');
        }
    }

    protected void _addStringPart(String a, String b) {
        _sep();
        buf.append(a).append(':')
                .append('\"').append(b).append("\"");
    }

    protected void _addObjectPart(String a, Object b) {
        _sep();
        buf.append(a).append(':')
                .append(new JSONEncoderCli().encode(b));
    }
}