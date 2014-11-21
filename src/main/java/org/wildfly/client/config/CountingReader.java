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

import java.io.IOException;
import java.io.Reader;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class CountingReader extends Reader {
    private int lineNumber = 1;
    private int columnNumber = 1;
    private int characterOffset = 0;

    private final Reader reader;

    CountingReader(final Reader reader) {
        this.reader = reader;
    }

    public int read() throws IOException {
        int ch = reader.read();
        if (ch == -1) return -1;
        processChar(ch);
        return ch;
    }

    private void processChar(final int ch) {
        switch (ch) {
            case '\n': {
                characterOffset++;
                lineNumber++;
                columnNumber = 1;
                break;
            }
            default: {
                if (! Character.isLowSurrogate((char) ch)) {
                    characterOffset++;
                    columnNumber++;
                }
                break;
            }
        }
    }

    public int read(final char[] cbuf) throws IOException {
        int cnt = reader.read(cbuf);
        if (cnt > 0) {
            for (int i = 0; i < cnt; i ++) {
                processChar(cbuf[i]);
            }
        }
        return cnt;
    }

    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        int cnt = reader.read(cbuf, off, len);
        if (cnt > 0) {
            for (int i = 0; i < cnt; i ++) {
                processChar(cbuf[i + off]);
            }
        }
        return cnt;
    }

    public void close() throws IOException {
        reader.close();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public int getCharacterOffset() {
        return characterOffset;
    }
}
