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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
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

    /**
     * Get the URI from which the configuration is being read.
     *
     * @return the URI from which the configuration is being read
     */
    public URI getConfigurationUri() {
        return configurationUri;
    }

    /**
     * Get a stream reader over a configuration.  The configuration returned will be the first element within the root
     * {@code configuration} element which has a namespace corresponding to one of the given namespaces.
     *
     * @param recognizedNamespaces the recognized namespaces
     * @return a reader which returns the first matching element
     * @throws ConfigXMLParseException if a read error occurs
     */
    public ConfigurationXMLStreamReader readConfiguration(Set<String> recognizedNamespaces) throws ConfigXMLParseException {
        final ConfigurationXMLStreamReader reader = new XIncludeXMLStreamReader(ConfigurationXMLStreamReader.openUri(configurationUri, xmlInputFactory));
        try {
            if (reader.hasNext()) {
                switch (reader.nextTag()) {
                    case START_ELEMENT: {
                        final String namespaceURI = reader.getNamespaceURI();
                        final String localName = reader.getLocalName();
                        if ((namespaceURI != null && namespaceURI.length() > 0) || ! "configuration".equals(localName)) {
                            if (namespaceURI == null) {
                                throw msg.unexpectedElement(localName, reader.getLocation());
                            } else {
                                throw msg.unexpectedElement(localName, namespaceURI, reader.getLocation());
                            }
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

    /**
     * Get a client configuration instance for a certain URI.
     *
     * @param configurationUri the configuration URI
     * @return the client configuration instance
     */
    public static ClientConfiguration getInstance(URI configurationUri) {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, FALSE);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, FALSE);
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, FALSE);
        return new ClientConfiguration(xmlInputFactory, configurationUri);
    }

    /**
     * Get a client configuration instance from the current environment.  First, the system property
     * {@code wildfly.config.url} is checked.  If present, the configuration file is taken from that URL (which is resolved
     * against the current working directory if it is a relative URL or a bare path).  If the property is not given,
     * the current thread's context class loader is consulted for a file called {@code wildfly-config.xml}, either in the
     * root of the class loader or within the {@code META-INF} folder.  If no such resource is found, the same search
     * is done against the class loader of this library.  Finally, if no configurations are found or are loadable, {@code null}
     * is returned.
     *
     * @return the client configuration instance, or {@code null} if no configuration is found
     */
    public static ClientConfiguration getInstance() {
        // specified URL overrides all
        final String wildFlyConfig = System.getProperty("wildfly.config.url");
        if (wildFlyConfig != null) {
            URI uri;
            try {
                uri = new URI(wildFlyConfig);
                if (! uri.isAbsolute()) {
                    uri = new URI("file", "", System.getProperty("user.dir"), null).resolve(uri);
                }
            } catch (URISyntaxException e) {
                // no config file there
                return null;
            }
            return getInstance(uri);
        }

        ClassLoader classLoader;
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>) ClientConfiguration::getContextClassLoader);
        } else {
            classLoader = getContextClassLoader();
        }
        if (classLoader == null) {
            // no priv block needed since it's our class loader
            classLoader = ClientConfiguration.class.getClassLoader();
        }
        URL resource = classLoader.getResource("wildfly-config.xml");
        if (resource == null) {
            resource = classLoader.getResource("META-INF/wildfly-config.xml");
            if (resource == null) {
                return null;
            }
        } try {
            return new ClientConfiguration(XMLInputFactory.newFactory(), resource.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }


    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
