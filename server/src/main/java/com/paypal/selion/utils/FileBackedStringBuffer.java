/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.logging.Level;

import com.paypal.selion.logging.SeLionGridLogger;

/**
 * A string buffer that flushes its content to a temporary file whenever the internal string buffer becomes larger than
 * MAX. If the buffer never reaches that size, no file is ever created and everything happens in memory, so the overhead
 * compared to StringBuffer/StringBuilder is minimal.
 * 
 * To avoid entire string to be loaded in memory, we can use toWriter() method.
 * 
 */

public class FileBackedStringBuffer {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(FileBackedStringBuffer.class);

    private static int MAX = 100000;

    private File file;
    private StringBuilder builder = new StringBuilder();
    private final int maxCharacters;

    public FileBackedStringBuffer() {
        this(MAX);
    }

    public FileBackedStringBuffer(int maxChars) {
        maxCharacters = maxChars;
    }

    public FileBackedStringBuffer append(CharSequence charSequence) {
        if (builder.length() > maxCharacters) {
            flushToFile();
        }
        if (charSequence.length() < MAX) {
            builder.append(charSequence);
        } else {
            flushToFile();
            try {
                copy(new StringReader(charSequence.toString()), new FileWriter(file, true));
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }
        return this;
    }

    public void toWriter(Writer fw) {
        try {
            BufferedWriter bw = new BufferedWriter(fw);
            if (file == null) {
                bw.write(builder.toString());
                bw.close();
            } else {
                flushToFile();
                copy(new FileReader(file), bw);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    private static void copy(Reader input, Writer output) throws IOException {
        char[] buf = new char[MAX];
        while (true) {
            int length = input.read(buf);
            if (length < 0) {
                break;
            }
            output.write(buf, 0, length);
        }

        try {
            input.close();
        } catch (IOException ignore) {
            // NOSONAR
        }
        try {
            output.close();
        } catch (IOException ignore) {
            // NOSONAR
        }
    }

    private void flushToFile() {
        if (builder.length() == 0)
            return;

        if (file == null) {
            try {
                file = File.createTempFile("testng", "FileBackedStringBuffer");
                file.deleteOnExit();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        FileWriter fw;
        try {
            fw = new FileWriter(file, true);
            fw.append(builder);
            fw.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        builder = new StringBuilder();
    }

    @Override
    public String toString() {
        String result = null;
        if (file == null) {
            result = builder.toString();
            return result;
        }
        flushToFile();
        try {
            result = readFile(file);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        return result;
    }

    public static String readFile(File f) throws IOException {
        return readFile(new FileInputStream(f));
    }

    public static String readFile(InputStream is) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        try {
            String line = br.readLine();
            while (line != null) {
                result.append(line).append("\n");
                line = br.readLine();
            }
        } finally {
            br.close();
            isr.close();
            is.close();
        }
        return result.toString();
    }
}
