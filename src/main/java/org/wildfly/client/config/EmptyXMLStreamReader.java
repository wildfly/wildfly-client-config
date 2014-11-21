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
import java.util.NoSuchElementException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class EmptyXMLStreamReader implements ConfigurationXMLStreamReader {
    private final URI uri;
    private final XMLLocation includedFrom;

    EmptyXMLStreamReader(final URI uri, final XMLLocation includedFrom) {
        this.uri = uri;
        this.includedFrom = includedFrom;
    }

    public URI getUri() {
        return uri;
    }

    public XMLInputFactory getXmlInputFactory() {
        throw new UnsupportedOperationException();
    }

    public XMLLocation getIncludedFrom() {
        return includedFrom;
    }

    public boolean hasNext() throws ConfigXMLParseException {
        return false;
    }

    public int next() throws ConfigXMLParseException {
        throw new NoSuchElementException();
    }

    public XMLLocation getLocation() {
        return XMLLocation.UNKNOWN;
    }

    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws ConfigXMLParseException {
        throw new UnsupportedOperationException();
    }

    public void close() {
    }

    public String getNamespaceURI(final String prefix) {
        return null;
    }

    public String getAttributeValue(final String namespaceURI, final String localName) {
        throw new IllegalStateException();
    }

    public int getAttributeCount() {
        throw new IllegalStateException();
    }

    public QName getAttributeName(final int index) {
        throw new IllegalStateException();
    }

    public String getAttributeNamespace(final int index) {
        throw new IllegalStateException();
    }

    public String getAttributeLocalName(final int index) {
        throw new IllegalStateException();
    }

    public String getAttributePrefix(final int index) {
        throw new IllegalStateException();
    }

    public String getAttributeType(final int index) {
        throw new IllegalStateException();
    }

    public String getAttributeValue(final int index) {
        throw new IllegalStateException();
    }

    public boolean isAttributeSpecified(final int index) {
        throw new IllegalStateException();
    }

    public int getNamespaceCount() {
        return 0;
    }

    public String getNamespacePrefix(final int index) {
        return null;
    }

    public String getNamespaceURI(final int index) {
        return null;
    }

    public NamespaceContext getNamespaceContext() {
        throw new UnsupportedOperationException();
    }

    public int getEventType() {
        return END_DOCUMENT;
    }

    public String getText() {
        throw new IllegalStateException();
    }

    public char[] getTextCharacters() {
        throw new IllegalStateException();
    }

    public int getTextStart() {
        throw new IllegalStateException();
    }

    public int getTextLength() {
        throw new IllegalStateException();
    }

    public String getEncoding() {
        return null;
    }

    public QName getName() {
        throw new IllegalStateException();
    }

    public String getLocalName() {
        throw new IllegalStateException();
    }

    public String getNamespaceURI() {
        throw new IllegalStateException();
    }

    public String getPrefix() {
        throw new IllegalStateException();
    }

    public String getVersion() {
        return null;
    }

    public String getCharacterEncodingScheme() {
        return null;
    }

    public String getPITarget() {
        return null;
    }

    public String getPIData() {
        return null;
    }
}
