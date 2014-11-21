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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class Delimiterator implements Iterator<String> {
    private final String subject;
    private final char delimiter;
    private int i;

    private static final int[] NO_INTS = new int[0];
    private static final long[] NO_LONGS = new long[0];
    private static final String[] NO_STRINGS = new String[0];

    Delimiterator(final String subject, final char delimiter) {
        this.subject = subject;
        this.delimiter = delimiter;
        i = 0;
    }

    static Delimiterator over(String subject, char delimiter) {
        return new Delimiterator(subject, delimiter);
    }

    public boolean hasNext() {
        return i != -1;
    }

    public String next() {
        final int i = this.i;
        if (i == -1) {
            throw new NoSuchElementException();
        }
        int n = subject.indexOf(delimiter, i);
        try {
            return n == -1 ? subject.substring(i) : subject.substring(i, n);
        } finally {
            this.i = n == -1 ? -1 : n + 1;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public String[] toStringArray() {
        return toStringArray(0);
    }

    String[] toStringArray(int count) {
        if (hasNext()) {
            final String next = next();
            final String[] strings = toStringArray(count + 1);
            strings[count] = next;
            return strings;
        } else {
            return count == 0 ? NO_STRINGS : new String[count];
        }
    }

    public int[] toIntArray() throws NumberFormatException {
        return toIntArray(0);
    }

    int[] toIntArray(int count) {
        if (hasNext()) {
            final String next = next();
            final int[] ints = toIntArray(count + 1);
            ints[count] = Integer.parseInt(next);
            return ints;
        } else {
            return count == 0 ? NO_INTS : new int[count];
        }
    }

    public long[] toLongArray() throws NumberFormatException {
        return toLongArray(0);
    }

    long[] toLongArray(int count) {
        if (hasNext()) {
            final String next = next();
            final long[] longs = toLongArray(count + 1);
            longs[count] = Long.parseLong(next);
            return longs;
        } else {
            return count == 0 ? NO_LONGS : new long[count];
        }
    }

}