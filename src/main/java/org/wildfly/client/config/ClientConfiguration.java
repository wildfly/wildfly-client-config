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

import static java.lang.Boolean.FALSE;
import static javax.xml.stream.XMLStreamConstants.*;
import static org.wildfly.client.config.ConfigurationXMLStreamReader.eventToString;
import static org.wildfly.client.config._private.ConfigMessages.msg;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;

/**
 * The entry point for generic client configuration.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class ClientConfiguration {

    private final XMLInputFactory xmlInputFactory;
    private final URI configurationUri;

    ClientConfiguration(final XMLInputFactory xmlInputFactory, final URI configurationUri) {
        this.xmlInputFactory = xmlInputFactory;
        this.configurationUri = configurationUri;
    }

    XMLInputFactory getXmlInputFactory() {
        return xmlInputFactory;
    }

    public URI getConfigurationUri() {
        return configurationUri;
    }

    public ConfigurationXMLStreamReader readConfiguration(Set<String> recognizedNamespaces) throws ConfigXMLParseException {
        final ConfigurationXMLStreamReader reader = new XIncludeXMLStreamReader(ConfigurationXMLStreamReader.openUri(configurationUri, xmlInputFactory));
        try {
            if (reader.hasNext()) {
                switch (reader.nextTag()) {
                    case START_ELEMENT: {
                        if (reader.getNamespaceURI() != null || ! "configuration".equals(reader.getLocalName())) {
                            throw msg.unexpectedElement(reader.getName(), reader.getLocation());
                        }
                        return new SelectingXMLStreamReader(true, reader, recognizedNamespaces);
                    }
                    default: {
                        throw msg.unexpectedContent(eventToString(reader.getEventType()), reader.getLocation());
                    }
                }
            }
            // no config found
            reader.close();
            return null;
        } catch (Throwable t) {
            try {
                reader.close();
            } catch (Throwable t2) {
                t.addSuppressed(t2);
            }
            throw t;
        }
    }

    public static ClientConfiguration getInstance(URI configurationUri) throws MalformedURLException {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, FALSE);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, FALSE);
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, FALSE);
        configurationUri.toURL();
        return new ClientConfiguration(xmlInputFactory, configurationUri);
    }
}
