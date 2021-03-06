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

package org.jboss.errai.bus.client.protocols;

/**
 * The parts comprising the core messaging protocol used by ErraiBus.
 * <p/>
 * As a general rule, you should avoid using the words reserved by this protocol.
 */
public enum MessageParts {
    /**
     * Specifies the specific command within the service that is being requested.  This is an optional element,
     * and is not required for signal-only services.  However it's use is encouraged for building multi-command
     * services.  It is used as the underlying protocol representation in
     * {@link org.jboss.errai.bus.client.api.base.CommandMessage#toSubject(String)}.  The <tt>CommandType</tt> is
     * represented as a <tt>String</tt>.
     */
    CommandType,

    /**
     * Specifies a subject being referenced for use in an command.  This should not be confused with {@link #ToSubject},
     * which is used for message routing.
     */
    Subject,

    /**
     * A unique identifier for identifying the session with which a message is associated.
     */
    SessionID,

    /**
     * Specifies any specific message text to be communicated as part of the command being sent.
     */
    MessageText,

    /**
     * Specifies what subject which should be replied-to in response to the message being sent.  Usually handled
     * automatically with conversations.
     */
    ReplyTo,

    /**
     * Specifies the intended recipient queue for the message.
     */
    ToSubject,

    /**
     * Specifies error message test.
     */
    ErrorMessage,

    /**
     * Specifies stack trace data in String form.
     */
    StackTrace,

    /**
     * If this attribute is present, the bus should give priority to processing it and not subject it to
     * window matching.
     */
    PriorityProcessing
}
