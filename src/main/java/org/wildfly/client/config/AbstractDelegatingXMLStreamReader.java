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

import java.net.URI;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
abstract class AbstractDelegatingXMLStreamReader implements ConfigurationXMLStreamReader {
    private final boolean closeDelegate;
    private final ConfigurationXMLStreamReader delegate;

    AbstractDelegatingXMLStreamReader(final boolean closeDelegate, final ConfigurationXMLStreamReader delegate) {
        this.closeDelegate = closeDelegate;
        this.delegate = delegate;
    }

    protected ConfigurationXMLStreamReader getDelegate() {
        return delegate;
    }

    public URI getUri() {
        return getDelegate().getUri();
    }

    public XMLInputFactory getXmlInputFactory() {
        return getDelegate().getXmlInputFactory();
    }

    public XMLLocation getIncludedFrom() {
        return getDelegate().getIncludedFrom();
    }

    public boolean hasNext() throws ConfigXMLParseException {
        return getDelegate().hasNext();
    }

    public int next() throws ConfigXMLParseException {
        return getDelegate().next();
    }

    public int nextTag() throws ConfigXMLParseException {
        int eventType = next();
        while (eventType == XMLStreamConstants.CHARACTERS && isWhiteSpace()
            || eventType == XMLStreamConstants.CDATA && isWhiteSpace()
            || eventType == XMLStreamConstants.SPACE
            || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
            || eventType == XMLStreamConstants.COMMENT) {
            eventType = next();
        }
        if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT) {
            throw unexpectedContent();
        }
        return eventType;
    }

    public String getElementText() throws ConfigXMLParseException {
        return getDelegate().getElementText();
    }

    public void require(final int type, final String namespaceURI, final String localName) throws ConfigXMLParseException {
        getDelegate().require(type, namespaceURI, localName);
    }

    public boolean isStartElement() {
        return getDelegate().isStartElement();
    }

    public boolean isEndElement() {
        return getDelegate().isEndElement();
    }

    public boolean isCharacters() {
        return getDelegate().isCharacters();
    }

    public boolean isWhiteSpace() {
        return getDelegate().isWhiteSpace();
    }

    public static String eventToString(final int type) {
        return ConfigurationXMLStreamReader.eventToString(type);
    }

    public Object getProperty(final String name) throws IllegalArgumentException {
        return getDelegate().getProperty(name);
    }

    public boolean hasText() {
        return getDelegate().hasText();
    }

    public boolean isStandalone() {
        return getDelegate().isStandalone();
    }

    public boolean standaloneSet() {
        return getDelegate().standaloneSet();
    }

    public boolean hasName() {
        return getDelegate().hasName();
    }

    public XMLLocation getLocation() {
        return getDelegate().getLocation();
    }

    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws ConfigXMLParseException {
        return getDelegate().getTextCharacters(sourceStart, target, targetStart, length);
    }

    public void close() throws ConfigXMLParseException {
        if (closeDelegate) getDelegate().close();
    }

    public String getNamespaceURI(final String prefix) {
        return getDelegate().getNamespaceURI(prefix);
    }

    public String getAttributeValue(final String namespaceURI, final String localName) {
        return getDelegate().getAttributeValue(namespaceURI, localName);
    }

    public int getAttributeCount() {
        return getDelegate().getAttributeCount();
    }

    public QName getAttributeName(final int index) {
        return getDelegate().getAttributeName(index);
    }

    public String getAttributeNamespace(final int index) {
        return getDelegate().getAttributeNamespace(index);
    }

    public String getAttributeLocalName(final int index) {
        return getDelegate().getAttributeLocalName(index);
    }

    public String getAttributePrefix(final int index) {
        return getDelegate().getAttributePrefix(index);
    }

    public String getAttributeType(final int index) {
        return getDelegate().getAttributeType(index);
    }

    public String getAttributeValue(final int index) {
        return getDelegate().getAttributeValue(index);
    }

    public boolean isAttributeSpecified(final int index) {
        return getDelegate().isAttributeSpecified(index);
    }

    public int getNamespaceCount() {
        return getDelegate().getNamespaceCount();
    }

    public String getNamespacePrefix(final int index) {
        return getDelegate().getNamespacePrefix(index);
    }

    public String getNamespaceURI(final int index) {
        return getDelegate().getNamespaceURI(index);
    }

    public NamespaceContext getNamespaceContext() {
        return getDelegate().getNamespaceContext();
    }

    public int getEventType() {
        return getDelegate().getEventType();
    }

    public String getText() {
        return getDelegate().getText();
    }

    public char[] getTextCharacters() {
        return getDelegate().getTextCharacters();
    }

    public int getTextStart() {
        return getDelegate().getTextStart();
    }

    public int getTextLength() {
        return getDelegate().getTextLength();
    }

    public String getEncoding() {
        return getDelegate().getEncoding();
    }

    public QName getName() {
        return getDelegate().getName();
    }

    public String getLocalName() {
        return getDelegate().getLocalName();
    }

    public String getNamespaceURI() {
        return getDelegate().getNamespaceURI();
    }

    public String getPrefix() {
        return getDelegate().getPrefix();
    }

    public String getVersion() {
        return getDelegate().getVersion();
    }

    public String getCharacterEncodingScheme() {
        return getDelegate().getCharacterEncodingScheme();
    }

    public String getPITarget() {
        return getDelegate().getPITarget();
    }

    public String getPIData() {
        return getDelegate().getPIData();
    }
}
