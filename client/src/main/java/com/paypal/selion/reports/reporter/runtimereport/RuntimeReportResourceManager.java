/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.reports.reporter.runtimereport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * RuntimeReportResourceManager will take care of moving the resources files needed for RuntimeReporter to test-output
 * folder.
 * 
 */
public class RuntimeReportResourceManager {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private void writeStreamToFile(InputStream isr, String fileName, String outputFolder) throws IOException {
        logger.entering(new Object[] { isr, fileName, outputFolder });

        File outFile = new File(outputFolder + File.separator + fileName);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        try (FileOutputStream outStream = new FileOutputStream(outFile)) {
            byte[] bytes = new byte[1024];
            int readLength = 0;
            while ((readLength = isr.read(bytes)) != -1) {
                outStream.write(bytes, 0, readLength);
            }
            isr.close();
            outStream.flush();
        }

        logger.exiting();
    }

    /**
     * This method copies all the resources specified in RuntimeReporterResources.properties file and move it to
     * test-output/RuntimeReporter folder
     * 
     * @param outputFolder
     *            - the folder in which all the resource file will be moved.
     */
    public void copyResources(String outputFolder) {

        logger.entering(new Object[] { outputFolder });

        copyResource(outputFolder, "RuntimeReporterResources.properties");
        copyResource(outputFolder, "Resources.properties");

        logger.exiting();
    }

    private void copyResource(String outputFolder, String resourceName) {
        Properties resourceListToCopy = new Properties();
        try {
            ClassLoader localClassLoader = this.getClass().getClassLoader();

            resourceListToCopy.load(localClassLoader.getResourceAsStream(resourceName));
            Enumeration<Object> keys = resourceListToCopy.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String fileName = resourceListToCopy.getProperty(key);
                writeStreamToFile(localClassLoader.getResourceAsStream("templates/" + fileName), fileName, outputFolder);

            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
