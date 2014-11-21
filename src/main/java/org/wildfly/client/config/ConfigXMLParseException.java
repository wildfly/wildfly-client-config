/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.client.config;

import static org.wildfly.client.config._private.ConfigMessages.msg;

import java.net.URI;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class ConfigXMLParseException extends XMLStreamException {

    private static final long serialVersionUID = -1880381457871462141L;

    /**
     * Constructs a new {@code ConfigXMLParseException} instance.  The message is left blank ({@code null}), and no
     * cause is specified.
     */
    public ConfigXMLParseException() {
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial message.  No cause is specified.
     *
     * @param msg the message
     */
    public ConfigXMLParseException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial cause.  If a non-{@code null} cause is
     * specified, its message is used to initialize the message of this {@code ConfigXMLParseException}; otherwise the
     * message is left blank ({@code null}).
     *
     * @param cause the cause
     */
    public ConfigXMLParseException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial message and cause.
     *
     * @param msg the message
     * @param cause the cause
     */
    public ConfigXMLParseException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance.  The message is left blank ({@code null}), and no
     * cause is specified.
     *
     * @param location the location of the exception
     */
    public ConfigXMLParseException(final Location location) {
        this(msg.parseError(), XMLLocation.toXMLLocation(location), 0);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial message.  No cause is specified.
     *
     * @param msg the message
     * @param location the location of the exception
     */
    public ConfigXMLParseException(final String msg, final Location location) {
        this(msg, XMLLocation.toXMLLocation(location), 0);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial cause.  If a non-{@code null} cause is
     * specified, its message is used to initialize the message of this {@code ConfigXMLParseException}; otherwise the
     * message is left blank ({@code null}).
     *
     * @param cause the cause
     * @param location the location of the exception
     */
    public ConfigXMLParseException(final Throwable cause, final Location location) {
        this(msg.parseError(), XMLLocation.toXMLLocation(location), cause, 0);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial message and cause.
     *  @param msg the message
     * @param location the location of the exception
     * @param cause the cause
     */
    public ConfigXMLParseException(final String msg, final Location location, final Throwable cause) {
        this(msg, XMLLocation.toXMLLocation(location), cause, 0);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance.  The message is left blank ({@code null}), and no
     * cause is specified.
     *
     * @param reader an XML reader at the position of the problem
     */
    public ConfigXMLParseException(final XMLStreamReader reader) {
        this(msg.parseError(), XMLLocation.toXMLLocation(reader.getLocation()), 0);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial message.  No cause is specified.
     *
     * @param msg the message
     * @param reader an XML reader at the position of the problem
     */
    public ConfigXMLParseException(final String msg, final XMLStreamReader reader) {
        this(msg, XMLLocation.toXMLLocation(reader.getLocation()), 0);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial cause.  If a non-{@code null} cause is
     * specified, its message is used to initialize the message of this {@code ConfigXMLParseException}; otherwise the
     * message is left blank ({@code null}).
     *
     * @param cause the cause
     * @param reader an XML reader at the position of the problem
     */
    public ConfigXMLParseException(final Throwable cause, final XMLStreamReader reader) {
        this(msg.parseError(), XMLLocation.toXMLLocation(reader.getLocation()), cause, 0);
    }

    /**
     * Constructs a new {@code ConfigXMLParseException} instance with an initial message and cause.
     *
     * @param msg the message
     * @param reader an XML reader at the position of the problem
     * @param cause the cause
     */
    public ConfigXMLParseException(final String msg, final XMLStreamReader reader, final Throwable cause) {
        this(msg, XMLLocation.toXMLLocation(reader.getLocation()), cause, 0);
    }

    /**
     * Get the location of this exception.
     *
     * @return the location of this exception
     */
    public XMLLocation getLocation() {
        return XMLLocation.toXMLLocation(super.getLocation());
    }

    /**
     * Set the location of this exception.
     *
     * @param location the location of this exception
     */
    protected void setLocation(XMLLocation location) {
        this.location = location;
    }

    static ConfigXMLParseException from(final XMLStreamException exception) {
        if (exception instanceof ConfigXMLParseException) return (ConfigXMLParseException) exception;
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        final Throwable cause = exception.getCause();
        final ConfigXMLParseException parseException;
        if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), exception.getLocation(), cause);
        } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), exception.getLocation());
        }
        parseException.setStackTrace(stackTrace);
        return parseException;
    }

    static ConfigXMLParseException from(final XMLStreamException exception, final URI uri) {
        if (exception instanceof ConfigXMLParseException) return (ConfigXMLParseException) exception;
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        final Throwable cause = exception.getCause();
        final ConfigXMLParseException parseException;
        if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, exception.getLocation()), cause);
        } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, exception.getLocation()));
        }
        parseException.setStackTrace(stackTrace);
        return parseException;
    }

    static ConfigXMLParseException from(final XMLStreamException exception, final URI uri, final XMLLocation includedFrom) {
        if (exception instanceof ConfigXMLParseException) return (ConfigXMLParseException) exception;
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        final Throwable cause = exception.getCause();
        final ConfigXMLParseException parseException;
        if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, exception.getLocation()), cause);
        } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, exception.getLocation()));
        }
        parseException.setStackTrace(stackTrace);
        return parseException;
    }

    static ConfigXMLParseException from(final Exception exception) {
        if (exception instanceof XMLStreamException) {
            return from((XMLStreamException) exception);
        }
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        final Throwable cause = exception.getCause();
        final ConfigXMLParseException parseException;
        if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.UNKNOWN, cause);
        } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.UNKNOWN);
        }
        parseException.setStackTrace(stackTrace);
        return parseException;
    }

    static ConfigXMLParseException from(final Exception exception, final URI uri) {
        if (exception instanceof XMLStreamException) {
            return from((XMLStreamException) exception, uri);
        }
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        final Throwable cause = exception.getCause();
        final ConfigXMLParseException parseException;
        if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, new XMLLocation(uri)), cause);
        } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, new XMLLocation(uri)));
        }
        parseException.setStackTrace(stackTrace);
        return parseException;
    }

    static ConfigXMLParseException from(final Exception exception, final URI uri, final XMLLocation includedFrom) {
        if (exception instanceof XMLStreamException) {
            return from((XMLStreamException) exception, uri, includedFrom);
        }
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        final Throwable cause = exception.getCause();
        final ConfigXMLParseException parseException;
        if (cause != null) {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, new XMLLocation(uri)), cause);
        } else {
            parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, new XMLLocation(uri)));
        }
        parseException.setStackTrace(stackTrace);
        return parseException;
    }

    private static String clean(String original) {
        if (original.startsWith("ParseError at [row,col]:[")) {
            final int idx = original.indexOf("Message: ");
            return idx == -1 ? original : original.substring(idx + 9);
        } else {
            return original;
        }
    }

    private ConfigXMLParseException(final String msg, final XMLLocation location, @SuppressWarnings("unused") int ignored) {
        super(location + msg);
        this.location = location;
    }

    private ConfigXMLParseException(final String msg, final XMLLocation location, final Throwable cause, @SuppressWarnings("unused") int ignored) {
        super(location + msg, cause);
        this.location = location;
    }
}
