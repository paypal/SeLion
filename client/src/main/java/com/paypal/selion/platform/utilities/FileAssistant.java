/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

package com.paypal.selion.platform.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * Utilitarian class that provides simple file I/O operations
 */
public class FileAssistant {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    private FileAssistant() {

    }

    /**
     * Load a file resource via the {@link ClassLoader}
     * 
     * @param fileName
     *            The name of the file
     * @return An object of type {@link InputStream} that represents the stream of a file that was read from the file
     *         system.
     */
    public static InputStream loadFile(String fileName) {
        logger.entering(fileName);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = loader.getResourceAsStream(fileName);
        if (iStream == null) {
            try {
                iStream = new FileInputStream(fileName);
            } catch (FileNotFoundException e) { // NOSONAR
                // Gobbling the checked exception here and doing nothing with it
                // because we are explicitly checking if the inputstream is null
                // and then throwing a runtime exception
            }
        }
        if (iStream == null) {
            throw new IllegalArgumentException("[" + fileName + "] is not a valid resource");
        }
        logger.exiting();
        return iStream;
    }

    /**
     * Load a file resource via the {@link ClassLoader}
     * 
     * @param file
     *            An object of type {@link File} which represents a file object
     * @return An object of type {@link InputStream} that represents the stream of a file that was read from the file
     *         system.
     */
    public static InputStream loadFile(File file) {
        return loadFile(file.getAbsolutePath());
    }

    /**
     * Read a file resource via the {@link ClassLoader}. Return it as a {@link String}.
     * 
     * @param fileName
     *            The file name can either be an absolute path or a relative path from the project's base directory.
     * @return content of the file
     * @throws IOException
     */
    public static String readFile(String fileName) throws IOException {
        logger.entering(fileName);
        Preconditions.checkArgument(StringUtils.isNotBlank(fileName), "File name cannot be null (or) empty.");
        StringBuilder output = new StringBuilder();
        try (BufferedReader buffreader = new BufferedReader(new InputStreamReader(loadFile(fileName),
                Charset.forName("UTF-8")))) {
            String line = null;
            while ((line = buffreader.readLine()) != null) {
                output.append(line);
            }
        }
        logger.exiting(output);
        return output.toString();
    }

    /**
     * Write an {@link InputStream} to a file
     * @param isr the {@link InputStream} 
     * @param fileName The target file name to use. Do not include the path.
     * @param outputFolder The target folder to use.
     * @throws IOException
     */
    public static void writeStreamToFile(InputStream isr, String fileName, String outputFolder) throws IOException {
        logger.entering(new Object[] { isr, fileName, outputFolder });
        FileUtils.copyInputStreamToFile(isr, new File(outputFolder + "/" + fileName));
        logger.exiting();
    }
}
