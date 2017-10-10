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

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.COMMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.SPACE;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class TestSimpleConfiguration {

    public TestSimpleConfiguration() {
    }

    @Test
    public void testEmptyFile() throws Exception {
        URL resource = TestSimpleConfiguration.class.getResource("/empty-config.xml");
        assertNotNull(resource);
        ClientConfiguration configuration = ClientConfiguration.getInstance(resource.toURI());
        try (ConfigurationXMLStreamReader reader = configuration.readConfiguration(Collections.singleton("urn:not-found"))) {
            assertNotNull(reader);
            assertFalse(reader.hasNext());
        }
    }

    @Test
    public void testFirstElement() throws Exception {
        URL resource = TestSimpleConfiguration.class.getResource("/first-element-config.xml");
        assertNotNull(resource);
        ClientConfiguration configuration = ClientConfiguration.getInstance(resource.toURI());
        try (ConfigurationXMLStreamReader reader = configuration.readConfiguration(Collections.singleton("urn:config-urn"))) {
            validateContent(reader);
        }
    }

    @Test
    public void testSecondElement() throws Exception {
        URL resource = TestSimpleConfiguration.class.getResource("/second-element-config.xml");
        assertNotNull(resource);
        ClientConfiguration configuration = ClientConfiguration.getInstance(resource.toURI());
        try (ConfigurationXMLStreamReader reader = configuration.readConfiguration(Collections.singleton("urn:config-urn"))) {
            validateContent(reader);
        }
    }

    @Test
    public void testSimpleXIncludeElement() throws Exception {
        URL resource = TestSimpleConfiguration.class.getResource("/xinclude-config.xml");
        assertNotNull(resource);
        ClientConfiguration configuration = ClientConfiguration.getInstance(resource.toURI());
        try (ConfigurationXMLStreamReader reader = configuration.readConfiguration(Collections.singleton("urn:config-urn"))) {
            validateContent(reader);
        }
    }

    @Test
    public void testSimpleXIncludeXmlElement() throws Exception {
        URL resource = TestSimpleConfiguration.class.getResource("/xinclude-xml-config.xml");
        assertNotNull(resource);
        ClientConfiguration configuration = ClientConfiguration.getInstance(resource.toURI());
        try (ConfigurationXMLStreamReader reader = configuration.readConfiguration(Collections.singleton("urn:config-urn"))) {
            validateXIncludeContent(reader);
        }
    }

    public void validateContent(final ConfigurationXMLStreamReader reader) throws ConfigXMLParseException {
        assertNotNull(reader);
        assertTrue(reader.hasNext());
        L0: while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case START_ELEMENT: {
                    assertEquals("urn:config-urn", reader.getNamespaceURI());
                    assertEquals("the-element", reader.getLocalName());
                    assertEquals(0, reader.getAttributeCount());
                    break L0;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
        L1: while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) break;
                    assertEquals("Hello!", reader.getText().trim());
                    break L1;
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
        L2: while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case END_ELEMENT: {
                    assertEquals("urn:config-urn", reader.getNamespaceURI());
                    assertEquals("the-element", reader.getLocalName());
                    break L2;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
        while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
    }

    public void validateXIncludeContent(final ConfigurationXMLStreamReader reader) throws ConfigXMLParseException {
        assertNotNull(reader);
        assertTrue(reader.hasNext());
        L: while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case START_ELEMENT: {
                    assertEquals("urn:config-urn", reader.getNamespaceURI());
                    assertEquals("the-element", reader.getLocalName());
                    assertEquals(0, reader.getAttributeCount());
                    break L;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
        L: while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case START_ELEMENT: {
                    assertEquals("urn:config-urn", reader.getNamespaceURI());
                    assertEquals("hello", reader.getLocalName());
                    assertEquals(0, reader.getAttributeCount());
                    break L;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
        L: while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) break;
                    assertEquals("Hello!", reader.getText().trim());
                    break L;
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
        L: while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case END_ELEMENT: {
                    assertEquals("urn:config-urn", reader.getNamespaceURI());
                    assertEquals("hello", reader.getLocalName());
                    break L;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
        L: while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case END_ELEMENT: {
                    assertEquals("urn:config-urn", reader.getNamespaceURI());
                    assertEquals("the-element", reader.getLocalName());
                    break L;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
        while (reader.hasNext()) {
            switch (reader.next()) {
                case SPACE:
                case COMMENT: {
                    // skip
                    break;
                }
                case CHARACTERS: {
                    if (reader.getText().trim().isEmpty()) {
                        break;
                    }
                    // fall thru
                }
                default: {
                    fail("Unexpected event type: " + ConfigurationXMLStreamReader.eventToString(reader.getEventType()));
                }
            }
        }
    }

    @Test
    public void testPropertyUrl() {
        assertEquals("file:///absolute.xml", ClientConfiguration.propertyUrlToUri("file:///absolute.xml").toString());
        assertTrue(Pattern.matches("file:///.*/relative", ClientConfiguration.propertyUrlToUri("relative").toString()));
        assertTrue(
                Pattern.matches("file:///absolute", ClientConfiguration.propertyUrlToUri("/absolute").toString()) ||
                        Pattern.matches("file:///[A-Za-z]:/absolute", ClientConfiguration.propertyUrlToUri("/absolute").toString())
        );

        if (File.separator.equals("\\")) {
            // we are on the windows and we want to check windows specific behavior
            assertTrue(Pattern.matches("file:///.*/relative", ClientConfiguration.propertyUrlToUri("..\\relative").toString()));
            assertTrue(Pattern.matches("file:///C:/absolute.xml",
                    ClientConfiguration.propertyUrlToUri("C:\\absolute.xml").toString()));
            assertEquals("file:///C:/absolute.xml", ClientConfiguration.propertyUrlToUri("file:///C:/absolute.xml").toString());
        }
    }
}