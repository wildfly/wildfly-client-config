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

import static java.lang.Integer.signum;

import java.net.URI;

import javax.xml.stream.Location;

/**
 * An XML location which is readable by humans and which understands XInclude.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class XMLLocation implements Location, Comparable<XMLLocation> {

    /**
     * An unknown location.
     */
    public static final XMLLocation UNKNOWN = new XMLLocation(null, -1, -1, -1);

    private final XMLLocation includedFrom;
    private final URI uri;
    private final int lineNumber;
    private final int columnNumber;
    private final int characterOffset;
    private final String publicId;
    private final String systemId;
    private int hashCode;

    /**
     * Construct a new instance.
     *
     * @param includedFrom the source location that this location was included from
     * @param uri the source location (may be {@code null} if not known)
     * @param lineNumber the line number (may be {@code -1} if not known)
     * @param columnNumber the column number (may be {@code -1} if not known)
     * @param characterOffset the character offset (may be {@code -1} if not known)
     * @param publicId the XML public ID (may be {@code null})
     * @param systemId the XML system ID (may be {@code null})
     */
    public XMLLocation(final XMLLocation includedFrom, final URI uri, final int lineNumber, final int columnNumber, final int characterOffset, final String publicId, final String systemId) {
        this.includedFrom = includedFrom;
        this.uri = uri;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.characterOffset = characterOffset;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    /**
     * Construct a new instance.
     *
     * @param uri the source location (may be {@code null} if not known)
     * @param lineNumber the line number (may be {@code -1} if not known)
     * @param columnNumber the column number (may be {@code -1} if not known)
     * @param characterOffset the character offset (may be {@code -1} if not known)
     * @param publicId the XML public ID (may be {@code null})
     * @param systemId the XML system ID (may be {@code null})
     */
    public XMLLocation(final URI uri, final int lineNumber, final int columnNumber, final int characterOffset, final String publicId, final String systemId) {
        this(null, uri, lineNumber, columnNumber, characterOffset, null, null);
    }

    /**
     * Construct a new instance.
     *
     * @param includedFrom the source location that this location was included from
     * @param uri the source location (may be {@code null} if not known)
     * @param lineNumber the line number (may be {@code -1} if not known)
     * @param columnNumber the column number (may be {@code -1} if not known)
     * @param characterOffset the character offset (may be {@code -1} if not known)
     */
    public XMLLocation(final XMLLocation includedFrom, final URI uri, final int lineNumber, final int columnNumber, final int characterOffset) {
        this(includedFrom, uri, lineNumber, columnNumber, characterOffset, null, null);
    }

    /**
     * Construct a new instance.
     *
     * @param uri the source location (may be {@code null} if not known)
     * @param lineNumber the line number (may be {@code -1} if not known)
     * @param columnNumber the column number (may be {@code -1} if not known)
     * @param characterOffset the character offset (may be {@code -1} if not known)
     */
    public XMLLocation(final URI uri, final int lineNumber, final int columnNumber, final int characterOffset) {
        this(uri, lineNumber, columnNumber, characterOffset, null, null);
    }

    /**
     * Construct a new instance.
     *
     * @param uri the file name (may be {@code null} if this location does not correspond to a file)
     */
    public XMLLocation(final URI uri) {
        this(uri, -1, -1, -1);
    }

    /**
     * Construct a new instance.
     *
     * @param uri the file name (may be {@code null} if this location does not correspond to a file)
     * @param original the location to copy the remainder of the information from
     */
    XMLLocation(final XMLLocation includedFrom, final URI uri, final Location original) {
        this(includedFrom, uri, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
    }

    /**
     * Construct a new instance.
     *
     * @param uri the file name (may be {@code null} if this location does not correspond to a file)
     * @param original the location to copy the remainder of the information from
     */
    XMLLocation(final URI uri, final Location original) {
        this(uri, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
    }

    /**
     * Construct a new instance.
     *
     * @param original the location to copy the remainder of the information from
     */
    XMLLocation(final Location original) {
        this(original instanceof XMLLocation ? ((XMLLocation)original).getUri() : null, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
    }

    /**
     * Get the file name.  May be {@code null} if this location does not correspond to a file.
     *
     * @return the file name
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Get the line number where the corresponding event ends.  Returns -1 if not known.
     *
     * @return the line number where the corresponding event ends
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the column number where the corresponding event ends.  Returns -1 if not known.
     *
     * @return the column number where the corresponding event ends
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Get the absolute character offset of this event.  Returns -1 if not known.
     *
     * @return the absolute character offset of this event
     */
    public int getCharacterOffset() {
        return characterOffset;
    }

    /**
     * Get the public ID of the XML.  Returns {@code null} if not known.
     *
     * @return the public ID of the XML
     */
    public String getPublicId() {
        return publicId;
    }

    /**
     * Get the system ID of the XML.  Returns {@code null} if not known.
     *
     * @return the system ID of the XML
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * Get the location that this location was included from.  Returns {@code null} if this was the root document.
     *
     * @return the location that this location was included from, or {@code null}
     */
    public XMLLocation getIncludedFrom() {
        return includedFrom;
    }

    public static XMLLocation toXMLLocation(final Location location) {
        return toXMLLocation((URI) null, location);
    }

    public static XMLLocation toXMLLocation(final URI uri, final Location location) {
        if (location instanceof XMLLocation) {
            return (XMLLocation) location;
        } else if (location == null) {
            return UNKNOWN;
        } else {
            return new XMLLocation(uri, location);
        }
    }

    public static XMLLocation toXMLLocation(final XMLLocation includedFrom, final Location location) {
        return toXMLLocation(includedFrom, null, location);
    }

    public static XMLLocation toXMLLocation(final XMLLocation includedFrom, final URI uri, final Location location) {
        if (location instanceof XMLLocation) {
            return (XMLLocation) location;
        } else if (location == null) {
            return UNKNOWN;
        } else {
            return new XMLLocation(includedFrom, uri, location);
        }
    }

    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            if (includedFrom != null) result = includedFrom.hashCode;
            result = 31 * result + (uri != null ? uri.hashCode() : 0);
            result = 31 * result + lineNumber;
            result = 31 * result + columnNumber;
            result = 31 * result + characterOffset;
            result = 31 * result + (publicId != null ? publicId.hashCode() : 0);
            result = 31 * result + (systemId != null ? systemId.hashCode() : 0);
            if (result == 0) result = -1;
            hashCode = result;
        }
        return result;
    }

    /**
     * Determine whether this object is equal to another.
     *
     * @param other the other object
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    public boolean equals(Object other) {
        return other instanceof XMLLocation && equals((XMLLocation)other);
    }

    private static boolean equals(Object a, Object b) {
        return a == b || a == null ? b == null : a.equals(b);
    }

    /**
     * Determine whether this object is equal to another.
     *
     * @param other the other object
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    public boolean equals(XMLLocation other) {
        return this == other || other != null && equals(includedFrom, other.includedFrom) && equals(uri, other.uri) && lineNumber == other.lineNumber && columnNumber == other.columnNumber && characterOffset == other.characterOffset && equals(publicId, other.publicId) && equals(systemId, other.systemId);
    }

    /**
     * Get the location as a string.  The string will be suitable for immediately prefixing an error message.
     *
     * @return the location as a string
     */
    public String toString() {
        final StringBuilder b = new StringBuilder();
        toString(b);
        return b.toString();
    }

    private void toString(final StringBuilder b) {
        b.append("\n\tat ").append(uri == null ? "<input>" : uri);
        if (lineNumber > 0) {
            b.append(':').append(lineNumber);
            if (columnNumber > 0) {
                b.append(':').append(columnNumber);
            }
        }
        if (includedFrom != null) {
            includedFrom.toString(b);
        }
    }

    private int compareUri(URI a, URI b) {
        return a == null ? b == null ? 0 : 1 : b == null ? -1 : a.compareTo(b);
    }

    /**
     * Compare for sort.
     *
     * @param o the other location
     * @return the sort result (-1, 0, or 1)
     */
    public int compareTo(final XMLLocation o) {
        int c;
        c = compareUri(uri, o.uri);
        if (c == 0) {
            c = signum(lineNumber - o.lineNumber);
            if (c == 0) {
                c = signum(columnNumber - o.columnNumber);
                if (c == 0) {
                    c = signum(characterOffset - o.characterOffset);
                }
            }
        }
        return c;
    }
}
