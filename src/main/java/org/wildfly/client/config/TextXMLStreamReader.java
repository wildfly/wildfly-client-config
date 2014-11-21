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

import static java.lang.Math.min;
import static org.wildfly.client.config._private.ConfigMessages.msg;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

class TextXMLStreamReader implements ConfigurationXMLStreamReader {

    private final String charsetName;
    private final CountingReader reader;
    private final ConfigurationXMLStreamReader parent;
    private final URI uri;
    private final XMLLocation includedFrom;

    private char[] current = new char[512];
    private int len;
    private char[] next = new char[512];
    private int nextLen;

    TextXMLStreamReader(final String charsetName, final InputStream inputStream, final ConfigurationXMLStreamReader parent, final URI uri) throws UnsupportedEncodingException {
        this(charsetName, new InputStreamReader(inputStream, charsetName), parent, uri);
    }

    TextXMLStreamReader(final Charset charset, final InputStream inputStream, final ConfigurationXMLStreamReader parent, final URI uri) {
        this(charset.name(), new InputStreamReader(inputStream, charset), parent, uri);
    }

    TextXMLStreamReader(final String charsetName, final Reader reader, final ConfigurationXMLStreamReader parent, final URI uri) {
        this(charsetName, reader instanceof CountingReader ? (CountingReader) reader : new CountingReader(reader), parent, uri);
    }

    TextXMLStreamReader(final String charsetName, final CountingReader reader, final ConfigurationXMLStreamReader parent, final URI uri) {
        this.charsetName = charsetName;
        this.reader = reader;
        this.parent = parent;
        this.uri = uri;
        includedFrom = this.parent.getLocation();
    }

    public XMLLocation getIncludedFrom() {
        return includedFrom;
    }

    public int next() throws ConfigXMLParseException {
        if (! hasNext()) {
            throw new NoSuchElementException();
        }
        // swap buffers
        char[] old = current;
        current = next;
        len = nextLen;
        next = old;
        nextLen = 0;
        return CHARACTERS;
    }

    public URI getUri() {
        return uri;
    }

    public XMLInputFactory getXmlInputFactory() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() throws ConfigXMLParseException {
        if (nextLen == 0) {
            int res;
            try {
                res = reader.read(next);
            } catch (IOException e) {
                throw msg.failedToReadInput(getLocation(), e);
            }
            if (res == -1) {
                return false;
            }
            nextLen = res;
        }
        return true;
    }

    public void close() throws ConfigXMLParseException {
        try {
            reader.close();
        } catch (IOException e) {
            throw msg.failedToCloseInput(getLocation(), e);
        }
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
        throw new IllegalStateException();
    }

    public String getNamespacePrefix(final int index) {
        throw new IllegalStateException();
    }

    public String getNamespaceURI(final int index) {
        throw new IllegalStateException();
    }

    public NamespaceContext getNamespaceContext() {
        throw new IllegalStateException();
    }

    public int getEventType() {
        return len == 0 ? START_DOCUMENT : CHARACTERS;
    }

    public String getText() {
        if (len == 0) throw new IllegalStateException();
        return new String(current, 0, len);
    }

    public char[] getTextCharacters() {
        return Arrays.copyOf(current, len);
    }

    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) {
        if (sourceStart > len || targetStart > length) return 0;
        int realLen = min(len - sourceStart, length);
        System.arraycopy(current, sourceStart, target, targetStart, realLen);
        return realLen;
    }

    public int getTextStart() {
        return 0;
    }

    public int getTextLength() {
        if (len == 0) throw new IllegalStateException();
        return len;
    }

    public String getEncoding() {
        return charsetName;
    }

    public XMLLocation getLocation() {
        return new XMLLocation(includedFrom, uri, reader.getLineNumber(), reader.getColumnNumber(), reader.getCharacterOffset());
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
