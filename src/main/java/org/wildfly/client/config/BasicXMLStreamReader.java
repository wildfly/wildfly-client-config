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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class BasicXMLStreamReader implements ConfigurationXMLStreamReader {
    private final XMLLocation includedFrom;
    private final XMLStreamReader xmlStreamReader;
    private final URI uri;
    private final XMLInputFactory inputFactory;

    BasicXMLStreamReader(final XMLLocation includedFrom, final XMLStreamReader xmlStreamReader, final URI uri, final XMLInputFactory inputFactory) {
        this.includedFrom = includedFrom;
        this.xmlStreamReader = xmlStreamReader;
        this.uri = uri;
        this.inputFactory = inputFactory;
    }

    public URI getUri() {
        return uri;
    }

    public XMLInputFactory getXmlInputFactory() {
        return inputFactory;
    }

    public XMLLocation getIncludedFrom() {
        return includedFrom;
    }

    public boolean hasNext() throws ConfigXMLParseException {
        try {
            return xmlStreamReader.hasNext();
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e, uri, includedFrom);
        }
    }

    public int next() throws ConfigXMLParseException {
        try {
            return xmlStreamReader.next();
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e, uri, includedFrom);
        }
    }

    public void require(final int type, final String namespaceURI, final String localName) throws ConfigXMLParseException {
        try {
            xmlStreamReader.require(type, namespaceURI, localName);
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e, uri, includedFrom);
        }
    }

    public String getElementText() throws ConfigXMLParseException {
        try {
            return xmlStreamReader.getElementText();
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e, uri, includedFrom);
        }
    }

    public int nextTag() throws ConfigXMLParseException {
        try {
            return xmlStreamReader.nextTag();
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e, uri, includedFrom);
        }
    }

    public XMLLocation getLocation() {
        return new XMLLocation(includedFrom, uri, xmlStreamReader.getLocation());
    }

    public QName getName() {
        return xmlStreamReader.getName();
    }

    public String getLocalName() {
        return xmlStreamReader.getLocalName();
    }

    public boolean hasName() {
        return xmlStreamReader.hasName();
    }

    public String getNamespaceURI() {
        return xmlStreamReader.getNamespaceURI();
    }

    public String getPrefix() {
        return xmlStreamReader.getPrefix();
    }

    public String getVersion() {
        return xmlStreamReader.getVersion();
    }

    public boolean isStandalone() {
        return xmlStreamReader.isStandalone();
    }

    public boolean standaloneSet() {
        return xmlStreamReader.standaloneSet();
    }

    public String getCharacterEncodingScheme() {
        return xmlStreamReader.getCharacterEncodingScheme();
    }

    public String getPITarget() {
        return xmlStreamReader.getPITarget();
    }

    public String getPIData() {
        return xmlStreamReader.getPIData();
    }

    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws ConfigXMLParseException {
        try {
            return xmlStreamReader.getTextCharacters(sourceStart, target, targetStart, length);
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e, uri, includedFrom);
        }
    }

    public int getTextStart() {
        return xmlStreamReader.getTextStart();
    }

    public int getTextLength() {
        return xmlStreamReader.getTextLength();
    }

    public String getEncoding() {
        return xmlStreamReader.getEncoding();
    }

    public boolean hasText() {
        return xmlStreamReader.hasText();
    }

    public void close() throws ConfigXMLParseException {
        try {
            xmlStreamReader.close();
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e, uri, includedFrom);
        }
    }

    public String getNamespaceURI(final String prefix) {
        return xmlStreamReader.getNamespaceURI(prefix);
    }

    public boolean isWhiteSpace() {
        // delegate impl may be more efficient
        return xmlStreamReader.isWhiteSpace();
    }

    public String getAttributeValue(final String namespaceURI, final String localName) {
        return xmlStreamReader.getAttributeValue(namespaceURI, localName);
    }

    public int getAttributeCount() {
        return xmlStreamReader.getAttributeCount();
    }

    public QName getAttributeName(final int index) {
        return xmlStreamReader.getAttributeName(index);
    }

    public String getAttributeNamespace(final int index) {
        return xmlStreamReader.getAttributeNamespace(index);
    }

    public String getAttributeLocalName(final int index) {
        return xmlStreamReader.getAttributeLocalName(index);
    }

    public String getAttributePrefix(final int index) {
        return xmlStreamReader.getAttributePrefix(index);
    }

    public String getAttributeType(final int index) {
        return xmlStreamReader.getAttributeType(index);
    }

    public String getAttributeValue(final int index) {
        return xmlStreamReader.getAttributeValue(index);
    }

    public boolean isAttributeSpecified(final int index) {
        return xmlStreamReader.isAttributeSpecified(index);
    }

    public int getNamespaceCount() {
        return xmlStreamReader.getNamespaceCount();
    }

    public String getNamespacePrefix(final int index) {
        return xmlStreamReader.getNamespacePrefix(index);
    }

    public String getNamespaceURI(final int index) {
        return xmlStreamReader.getNamespaceURI(index);
    }

    public NamespaceContext getNamespaceContext() {
        return xmlStreamReader.getNamespaceContext();
    }

    public int getEventType() {
        return xmlStreamReader.getEventType();
    }

    public String getText() {
        return xmlStreamReader.getText();
    }

    public char[] getTextCharacters() {
        return xmlStreamReader.getTextCharacters();
    }

    public Object getProperty(final String name) throws IllegalArgumentException {
        return xmlStreamReader.getProperty(name);
    }
}
