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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface ConfigurationXMLStreamReader extends XMLStreamReader, AutoCloseable {

    char[] EMPTY_CHARS = new char[0];

    static ConfigurationXMLStreamReader openUri(final URI uri, final XMLInputFactory xmlInputFactory) throws ConfigXMLParseException {
        try {
            final URL url = uri.toURL();
            final URLConnection connection = url.openConnection();
            connection.setRequestProperty("Accept", "application/xml,text/xml,application/xhtml+xml");
            final InputStream inputStream = connection.getInputStream();
            try {
                return openUri(uri, xmlInputFactory, inputStream);
            } catch (final Throwable t) {
                try {
                    inputStream.close();
                } catch (Throwable t2) {
                    t.addSuppressed(t2);
                }
                throw t;
            }
        } catch (MalformedURLException e) {
            throw msg.invalidUrl(new XMLLocation(uri), e);
        } catch (IOException e) {
            throw msg.failedToReadInput(new XMLLocation(uri), e);
        }
    }

    static ConfigurationXMLStreamReader openUri(final URI uri, final XMLInputFactory xmlInputFactory, final InputStream inputStream) throws ConfigXMLParseException {
        try {
            return new BasicXMLStreamReader(null, xmlInputFactory.createXMLStreamReader(inputStream), uri, xmlInputFactory);
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e, uri, null);
        }
    }

    URI getUri();

    XMLInputFactory getXmlInputFactory();

    XMLLocation getIncludedFrom();

    boolean hasNext() throws ConfigXMLParseException;

    int next() throws ConfigXMLParseException;

    default int nextTag() throws ConfigXMLParseException {
        int eventType;
        for (;;) {
            eventType = next();
            switch (eventType) {
                case SPACE:
                case PROCESSING_INSTRUCTION:
                case COMMENT: {
                    break;
                }
                case START_ELEMENT:
                case END_ELEMENT: {
                    return eventType;
                }
                case CHARACTERS:
                case CDATA: {
                    if (isWhiteSpace()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    throw msg.expectedStartOrEndElement(eventToString(eventType), getLocation());
                }
            }
        }
    }

    default String getElementText() throws ConfigXMLParseException {
        int eventType = getEventType();
        if (eventType != START_ELEMENT) {
            throw msg.expectedStartElement(eventToString(eventType), getLocation());
        }
        final StringBuilder sb = new StringBuilder();
        for (;;) {
            eventType = next();
            switch (eventType) {
                case END_ELEMENT: {
                    return sb.toString();
                }
                case CHARACTERS:
                case CDATA:
                case SPACE:
                case ENTITY_REFERENCE: {
                    sb.append(getText());
                    break;
                }
                case PROCESSING_INSTRUCTION:
                case COMMENT: {
                    // skip
                    break;
                }
                case END_DOCUMENT: {
                    throw msg.unexpectedDocumentEnd(getLocation());
                }
                case START_ELEMENT: {
                    throw msg.textCannotContainElements(getLocation());
                }
                default: {
                    throw msg.unexpectedContent(eventToString(eventType), getLocation());
                }
            }
        }
    }

    default void require(final int type, final String namespaceURI, final String localName) throws ConfigXMLParseException {
        if (getEventType() != type) {
            throw msg.expectedEventType(eventToString(type), eventToString(getEventType()), getLocation());
        } else if (namespaceURI != null && !namespaceURI.equals(getNamespaceURI())) {
            throw msg.expectedNamespace(namespaceURI, getNamespaceURI(), getLocation());
        } else if (localName != null && !localName.equals(getLocalName())) {
            throw msg.expectedLocalName(localName, getLocalName(), getLocation());
        }
    }

    default boolean isStartElement() {
        return getEventType() == START_ELEMENT;
    }

    default boolean isEndElement() {
        return getEventType() == END_ELEMENT;
    }

    default boolean isCharacters() {
        return getEventType() == CHARACTERS;
    }

    default boolean isWhiteSpace() {
        return getEventType() == SPACE || getEventType() == CHARACTERS && getText().trim().isEmpty();
    }

    static String eventToString(final int type) {
        switch (type) {
            case START_DOCUMENT: return "document start";
            case END_DOCUMENT: return "document end";
            case START_ELEMENT: return "start element";
            case END_ELEMENT: return "end element";
            case CDATA: return "cdata";
            case CHARACTERS: return "characters";
            case ATTRIBUTE: return "attribute";
            case DTD: return "dtd";
            case ENTITY_DECLARATION: return "entity declaration";
            case ENTITY_REFERENCE: return "entity reference";
            case NAMESPACE: return "namespace";
            case NOTATION_DECLARATION: return "notation declaration";
            case PROCESSING_INSTRUCTION: return "processing instruction";
            case SPACE: return "white space";
            default: return "unknown";
        }
    }

    default Object getProperty(final String name) throws IllegalArgumentException {
        return null;
    }

    default boolean hasText() {
        switch (getEventType()) {
            case CHARACTERS:
            case DTD:
            case ENTITY_REFERENCE:
            case COMMENT:
            case SPACE: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    default boolean isStandalone() {
        return false;
    }

    default boolean standaloneSet() {
        return false;
    }

    default boolean hasName() {
        final int eventType = getEventType();
        return eventType == START_ELEMENT || eventType == END_ELEMENT;
    }

    XMLLocation getLocation();

    int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws ConfigXMLParseException;

    void close() throws ConfigXMLParseException;

    default void skipContent() throws ConfigXMLParseException {
        while (hasNext()) {
            switch (next()) {
                case START_ELEMENT: {
                    skipContent();
                    break;
                }
                case END_ELEMENT: {
                    return;
                }
            }
        }
    }

    // ===== exceptions =====

    /**
     * Return a throwable exception explaining that the element at the current position was not expected.
     *
     * @return the exception
     */
    default ConfigXMLParseException unexpectedElement() {
        return msg.unexpectedElement(getName(), getLocation());
    }

    /**
     * Return a throwable exception explaining that the attribute at the current position with the given index was not
     * expected.
     *
     * @param i the attribute index
     * @return the exception
     */
    default ConfigXMLParseException unexpectedAttribute(int i) {
        return msg.unexpectedAttribute(getAttributeName(i), getLocation());
    }

    /**
     * Return a throwable exception explaining that a required element with the given namespace and local name was missing.
     *
     * @param namespaceUri the namespace URI
     * @param localName the local element name
     * @return the exception
     */
    default ConfigXMLParseException missingRequiredElement(String namespaceUri, String localName) {
        return msg.missingRequiredElement(namespaceUri, localName, getLocation());
    }

    /**
     * Return a throwable exception explaining that a required attribute with the given namespace and local name was missing.
     *
     * @param namespaceUri the namespace URI (or {@code null} if it is a local name)
     * @param localName the local attribute name
     * @return the exception
     */
    default ConfigXMLParseException missingRequiredAttribute(String namespaceUri, String localName) {
        return msg.missingRequiredAttribute(namespaceUri, localName, getLocation());
    }

    // ===== attribute helpers =====

    /**
     * Get the value of an attribute as an integer.
     *
     * @param index the index of the attribute
     *
     * @return the integer value
     *
     * @throws ConfigXMLParseException if an error occurs
     */
    default int getIntAttributeValue(int index) throws ConfigXMLParseException {
        try {
            return Integer.parseInt(getAttributeValue(index));
        } catch (NumberFormatException e) {
            throw msg.intParseException(e, getAttributeName(index), getLocation());
        }
    }

    /**
     * Get the value of an attribute as an integer list.
     *
     * @param index the index of the attribute
     *
     * @return the integer values
     *
     * @throws ConfigXMLParseException if an error occurs
     */
    default int[] getIntListAttributeValue(int index) throws ConfigXMLParseException {
        try {
            return new Delimiterator(getAttributeValue(index), ' ').toIntArray();
        } catch (NumberFormatException e) {
            throw msg.intParseException(e, getAttributeName(index), getLocation());
        }
    }

    /**
     * Get the value of an attribute as a space-delimited string list, as an iterator.
     *
     * @param index the index of the attribute
     * @return the values
     * @throws ConfigXMLParseException if an error occurs
     */
    default Iterator<String> getListAttributeValueAsIterator(int index) throws ConfigXMLParseException {
        return new Delimiterator(getAttributeValue(index), ' ');
    }

    /**
     * Get the value of an attribute as a space-delimited string list.
     *
     * @param index the index of the attribute
     * @return the values
     * @throws ConfigXMLParseException if an error occurs
     */
    default List<String> getListAttributeValue(int index) throws ConfigXMLParseException {
        return Arrays.asList(getListAttributeValueAsArray(index));
    }

    /**
     * Get the value of an attribute as a space-delimited string list, as an array.
     *
     * @param index the index of the attribute
     *
     * @return the values
     *
     * @throws ConfigXMLParseException if an error occurs
     */
    default String[] getListAttributeValueAsArray(int index) throws ConfigXMLParseException {
        return new Delimiterator(getAttributeValue(index), ' ').toStringArray();
    }

    /**
     * Get the value of an attribute as a long.
     *
     * @param index the index of the attribute
     *
     * @return the long value
     *
     * @throws ConfigXMLParseException if an error occurs
     */
    default long getLongAttributeValue(int index) throws ConfigXMLParseException {
        try {
            return Long.parseLong(getAttributeValue(index));
        } catch (NumberFormatException e) {
            throw msg.intParseException(e, getAttributeName(index), getLocation());
        }
    }

    /**
     * Get the value of an attribute as a long integer list.
     *
     * @param index the index of the attribute
     *
     * @return the long values
     *
     * @throws ConfigXMLParseException if an error occurs
     */
    default long[] getLongListAttributeValue(int index) throws ConfigXMLParseException {
        try {
            return new Delimiterator(getAttributeValue(index), ' ').toLongArray();
        } catch (NumberFormatException e) {
            throw msg.intParseException(e, getAttributeName(index), getLocation());
        }
    }

    /**
     * Get an attribute value as a {@code boolean}.  Only the string {@code "true"} (case-insensitive) is recognized as
     * a {@code true} value; all other strings are considered {@code false}.
     *
     * @param index the attribute index
     * @return the attribute value
     */
    default boolean getBooleanAttributeValue(int index) {
        return Boolean.parseBoolean(getAttributeValue(index));
    }
}
