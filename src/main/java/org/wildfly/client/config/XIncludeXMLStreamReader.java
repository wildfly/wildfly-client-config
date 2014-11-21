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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamException;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class XIncludeXMLStreamReader extends AbstractDelegatingXMLStreamReader {

    static final String XINCLUDE_NS = "http://www.w3.org/2001/XInclude";
    private ConfigurationXMLStreamReader child;

    XIncludeXMLStreamReader(final ConfigurationXMLStreamReader delegate) {
        super(true, delegate);
    }

    private ConfigurationXMLStreamReader getRawDelegate() {
        return super.getDelegate();
    }

    protected ConfigurationXMLStreamReader getDelegate() {
        final ConfigurationXMLStreamReader child = this.child;
        return child != null ? child : getRawDelegate();
    }

    public void skipContent() throws ConfigXMLParseException {
        while (getDelegate().hasNext()) {
            switch (getDelegate().next()) {
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

    public int next() throws ConfigXMLParseException {
        final ConfigurationXMLStreamReader child = this.child;
        if (child != null) {
            if (child.hasNext()) {
                final int next = child.next();
                if (next != END_DOCUMENT) {
                    return next;
                } else {
                    child.close();
                }
            }
            this.child = null;
        }
        final ConfigurationXMLStreamReader delegate = this.getDelegate();
        if (! delegate.hasNext()) {
            throw new NoSuchElementException();
        }
        int res;
        for (;;) {
            res = delegate.next();
            if (res == START_ELEMENT) {
                final String namespaceURI = delegate.getNamespaceURI();
                if (XINCLUDE_NS.equals(namespaceURI)) {
                    switch (delegate.getLocalName()) {
                        case "include": {
                            ConfigurationXMLStreamReader nested = processInclude();
                            boolean ok = false;
                            try {
                                if (nested != null && nested.hasNext()) {
                                    int eventType = nested.next();
                                    if (eventType == START_DOCUMENT) {
                                        if (! nested.hasNext()) {
                                            // close nested and keep going
                                            continue;
                                        }
                                        eventType = nested.next();
                                    }
                                    this.child = new XIncludeXMLStreamReader(nested);
                                    ok = true;
                                    return eventType;
                                } else {
                                    // fallback to empty, discard nested content
                                    ok = true;
                                    continue;
                                }
                            } finally {
                                if (!ok) try {
                                    nested.close();
                                } catch (ConfigXMLParseException ignored) {
                                }
                            }
                        }
                        default: {
                            throw msg.unexpectedElement(delegate.getName(), getLocation());
                        }
                    }
                } else {
                    // some other boring element!
                    return res;
                }
            }
            return res;
        }
    }

    private ConfigurationXMLStreamReader processInclude() throws ConfigXMLParseException {
        // save this for later
        final ScopedXMLStreamReader includeElement = new ScopedXMLStreamReader(false, getRawDelegate());
        final ConfigurationXMLStreamReader delegate = this.getDelegate();
        final int attributeCount = delegate.getAttributeCount();
        URI href = null;
        Charset textCharset = StandardCharsets.UTF_8;
        boolean fallback = false;
        String accept = null;
        String acceptLanguage = null;
        boolean parseAsText = false;
        for (int i = 0; i < attributeCount; i ++) {
            if (delegate.getAttributeNamespace(i) == null) {
                switch (delegate.getAttributeLocalName(i)) {
                    case "href": {
                        try {
                            href = new URI(delegate.getAttributeValue(i));
                        } catch (URISyntaxException e) {
                            throw new ConfigXMLParseException("Invalid include URI", getLocation(), e);
                        }
                        if (href.getFragment() != null) {
                            throw new ConfigXMLParseException("Invalid include URI: must not contain fragment identifier", getLocation());
                        }
                        fallback |= href.isOpaque();
                        break;
                    }
                    case "parse": {
                        switch (delegate.getAttributeValue(i)) {
                            case "xml": parseAsText = false; break;
                            case "text": parseAsText = true; break;
                            default: throw new ConfigXMLParseException("Invalid include directive: unknown parse type (must be \"text\" or \"xml\")", getLocation());
                        }
                        break;
                    }
                    case "xpointer": {
                        // no xpointer support
                        fallback = true;
                        break;
                    }
                    case "encoding": {
                        try {
                            textCharset = Charset.forName(delegate.getAttributeValue(i));
                        } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
                            // bad charset
                            fallback = true;
                            break;
                        }
                        break;
                    }
                    case "accept": {
                        accept = delegate.getAttributeValue(i);
                        break;
                    }
                    case "accept-language": {
                        acceptLanguage = delegate.getAttributeValue(i);
                        break;
                    }
                    // ignore others
                }
            }
        }
        if (! fallback) {
            final URL url;
            final InputStream inputStream;
            ConfigurationXMLStreamReader child;
            try {
                if (! href.isAbsolute()) {
                    href = getRawDelegate().getUri().resolve(href);
                }
                url = href.toURL();
                final URLConnection connection = url.openConnection();
                connection.addRequestProperty("Accept", accept != null ? accept : parseAsText ? "text/plain,text/*" : "application/xml,text/xml,application/*+xml,text/*+xml");
                if (acceptLanguage != null) connection.addRequestProperty("Accept-Language", acceptLanguage);
                inputStream = connection.getInputStream();
                try {
                    if (parseAsText) {
                        child = new TextXMLStreamReader(textCharset, inputStream, this, href);
                    } else {
                        child = new XIncludeXMLStreamReader(new BasicXMLStreamReader(getLocation(), getXmlInputFactory().createXMLStreamReader(inputStream), href, getXmlInputFactory()));
                    }
                } catch (XMLStreamException e) {
                    try {
                        inputStream.close();
                    } catch (Throwable e1) {
                        e.addSuppressed(e1);
                    }
                    throw ConfigXMLParseException.from(e, getUri(), getIncludedFrom());
                } catch (Throwable t) {
                    try {
                        inputStream.close();
                    } catch (Throwable e1) {
                        t.addSuppressed(e1);
                    }
                    throw t;
                }
            } catch (IOException e) {
                throw ConfigXMLParseException.from(e, getUri(), getIncludedFrom());
            }
            try {
                // consume remaining content
                getRawDelegate().skipContent();
                return child;
            } catch (Throwable t) {
                try {
                    child.close();
                } catch (Throwable t2) {
                    t.addSuppressed(t2);
                }
                throw t;
            }
        }
        // fallback
        // first, seek to first fallback element
        while (super.hasNext()) {
            switch (super.next()) {
                case START_ELEMENT: {
                    if (XINCLUDE_NS.equals(super.getNamespaceURI()) && "fallback".equals(super.getLocalName())) {
                        return child = new ScopedXMLStreamReader(true, new DrainingXMLStreamReader(false, includeElement));
                    } else {
                        int level = 0;
                        out: while (super.hasNext()) {
                            switch (super.next()) {
                                case START_ELEMENT: {
                                    level ++;
                                    break;
                                }
                                case END_ELEMENT: {
                                    if (level -- == 0) {
                                        break out;
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                case END_ELEMENT: {
                    return null;
                }
            }
        }
        return null;
    }
}
