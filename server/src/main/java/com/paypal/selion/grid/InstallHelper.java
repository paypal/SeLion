/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2017 PayPal                                                                                     |
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

package com.paypal.selion.grid;

import static com.paypal.selion.pojos.SeLionGridConstants.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.grid.RunnableLauncher.InstanceType;
import com.paypal.selion.logging.SeLionGridLogger;

final class InstallHelper {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(InstallHelper.class);

    private InstallHelper() {
        // Utility class. So hiding the constructor
    }

    /**
     * Create mandatory folders and files required to start the Grid / Node
     */
    static void firstTimeSetup() {
        LOGGER.entering();

        String msg = "Setting up SeLion Grid for first time use...\n";
        if (new File(SeLionConstants.SELION_HOME_DIR).exists()) {
            msg = "Verifying SeLion Grid installation...\n";
        }
        System.out.println(msg);

        try {
            FileUtils.forceMkdir(new File(DOWNLOADS_DIR));
            FileUtils.forceMkdir(new File(LOGS_DIR));
            copyFileFromResources(DOWNLOAD_JSON_FILE_RESOURCE, DOWNLOAD_JSON_FILE);
            copyFileFromResources(SELION_CONFIG_FILE_RESOURCE, SELION_CONFIG_FILE);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to install required components. Please make sure you have write "
                    + "access to " + SeLionConstants.SELION_HOME_DIR + " or specify a different home directory with -DselionHome.", e);
        }

        LOGGER.exiting();
    }

    /**
     * Copy file from source path to destination location
     *
     * @param sourcePath
     *            Resource path of the source file
     * @param destPath
     *            Path of the destination file
     * @throws IOException
     */
    static void copyFileFromResources(String sourcePath, String destPath) throws IOException {
        LOGGER.entering(new Object[] { sourcePath, destPath });

        File downloadFile = new File(destPath);

        if (!downloadFile.exists()) {
            InputStream stream = JarSpawner.class.getResourceAsStream(sourcePath);
            FileUtils.copyInputStreamToFile(stream, downloadFile);
            LOGGER.fine("File copied to " + destPath);
        }
        LOGGER.exiting();
    }

    /**
     * Create logging.properties file in the {@link SeLionConstants#SELION_HOME_DIR}
     * 
     * @param type
     *            {@link InstanceType}
     *
     * @throws IOException
     */
    static void createLoggingPropertiesFile(InstanceType type) throws IOException {
        LOGGER.entering(type);

        String installedFile = LOGGING_PROPERTIES_FILE + "." + type.getFriendlyName();
        if (!new File(installedFile).exists()) {

            // Need to change the backward slash to forward, so that logger able to locate path in windows
            String logPath = LOGS_DIR.replace("\\", "/");

            String value = IOUtils.toString(JarSpawner.class.getResourceAsStream(LOGGING_PROPERTIES_FILE_RESOURCE),
                    "UTF-8");
            value = value.concat("\njava.util.logging.FileHandler.pattern=" + logPath + "selion-grid-" + type.getFriendlyName()
                    + "-%g.log");
            FileUtils.writeStringToFile(new File(installedFile), value, "UTF-8");
            LOGGER.fine("Logger file created successfully. Path is " + installedFile);
        }

        LOGGER.exiting();
    }

}
