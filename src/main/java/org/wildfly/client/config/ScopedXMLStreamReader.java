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

import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamException;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class ScopedXMLStreamReader extends AbstractDelegatingXMLStreamReader {
    private int level;

    ScopedXMLStreamReader(final boolean closeDelegate, final ConfigurationXMLStreamReader delegate) {
        super(closeDelegate, delegate);
    }

    public boolean hasNext() throws ConfigXMLParseException {
        try {
            return level >= 0 && getDelegate().hasNext();
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e);
        }
    }

    public int next() throws ConfigXMLParseException {
        if (! hasNext()) {
            throw new NoSuchElementException();
        }
        final int next;
        try {
            next = getDelegate().next();
        } catch (XMLStreamException e) {
            throw ConfigXMLParseException.from(e);
        }
        switch (next) {
            case START_ELEMENT: {
                level ++;
                break;
            }
            case END_ELEMENT: {
                level --;
                break;
            }
        }
        return next;
    }
}
