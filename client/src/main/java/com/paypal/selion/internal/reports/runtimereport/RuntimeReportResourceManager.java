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

package com.paypal.selion.internal.reports.runtimereport;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.utilities.FileAssistant;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * RuntimeReportResourceManager will take care of moving the resources files needed for RuntimeReporter to test-output
 * folder.
 * 
 */
class RuntimeReportResourceManager {

    private static SimpleLogger logger = SeLionLogger.getLogger();

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
            resourceListToCopy.load(FileAssistant.loadFile(resourceName));
            Enumeration<Object> keys = resourceListToCopy.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String fileName = resourceListToCopy.getProperty(key);
                FileAssistant.writeStreamToFile(FileAssistant.loadFile("templates/" + fileName),
                        fileName, outputFolder);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
