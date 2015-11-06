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

package com.paypal.selion.reports.runtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.internal.reports.model.PageContents;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * {@link DataSaver} that stores the info on the file system.
 */
class SaverFileSystem implements DataSaver {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private final String outputFolder;

    public SaverFileSystem(String testNGOutputFolder) {
        this.outputFolder = testNGOutputFolder;
    }

    @Override
    public String saveScreenshot(PageContents s) {
        logger.entering(s);
        String screenshotAbsolutePath = getScreenshotAbsolutePath(s.getId());
        try (OutputStream out = new FileOutputStream(screenshotAbsolutePath)) {
            out.write(s.getScreenImage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while trying to save screenshot " + e.getMessage(), e);
        }
        String screenshotUrl = "screenshots/" + s.getId() + ".png";
        logger.exiting(screenshotUrl);
        return screenshotUrl;

    }

    private String getScreenshotAbsolutePath(String name) {
        logger.entering(name);
        String screenshotPath = "screenshots" + File.separator + name + ".png";
        String screenshotAbsolutePath = outputFolder + screenshotPath;
        logger.exiting(screenshotAbsolutePath);
        return screenshotAbsolutePath;
    }

    public String saveSources(PageContents s) {
        /*
         * Made output to txt file not html
         */
        logger.entering(s);
        String path = outputFolder + "sources" + File.separator + s.getId() + ".source.txt";
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF8"))) {
            out.write(s.getPageSource());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while trying to save page source " + e.getMessage(), e);
        }
        logger.exiting(path);

        return path;
    }

    @Override
    public PageContents getScreenshotByName(String name) throws IOException {
        logger.entering(name);
        String path = getScreenshotAbsolutePath(name);
        File f = new File(path);
        byte[] bytes = getBytesFromFile(f);
        PageContents returnValue = new PageContents(bytes, name);
        logger.exiting();
        return returnValue;
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        logger.entering(file);
        try (InputStream is = new FileInputStream(file)) {
            // Get the size of the file
            long length = file.length();

            // Create the byte array to hold the data
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                IOException e = new IOException("Could not completely read file " + file.getName());
                logger.log(Level.SEVERE, e.getMessage(), e);
                if (is != null) {
                    is.close();
                }
                throw e;
            }

            logger.exiting();
            return bytes;
        }

    }

    /**
     * @see com.paypal.selion.reports.runtime.DataSaver#init() Creates directories sources, html, screenshots
     *      based off output folder
     */
    @Override
    public void init() {
        logger.entering();
        (new File(outputFolder, "sources")).mkdirs();
        (new File(outputFolder, "html")).mkdirs();
        (new File(outputFolder, "screenshots")).mkdirs();
        logger.exiting();
    }
}
