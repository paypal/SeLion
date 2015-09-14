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

package com.paypal.selion.internal.platform.pageyaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import com.google.common.base.Preconditions;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;
import org.apache.commons.lang.StringUtils;

/**
 * This reader Factory returns the reader instance depending on the type of input data file. For now we support
 * .yaml files only).
 */
public final class GuiMapReaderFactory {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private GuiMapReaderFactory() {

    }

    /**
     * Method to get the reader instance depending on the input parameters.
     * 
     * @param pageDomain
     *            domain folder under which the input data files are present.
     * @param pageClassName
     *            Page class name. May not be <code>null</code>, empty, or whitespace.
     * @return DataProvider instance
     * @throws IOException
     */
    public static GuiMapReader getInstance(String pageDomain, String pageClassName) throws IOException {
        logger.entering(new Object[]{pageDomain, pageClassName});
        Preconditions.checkArgument(StringUtils.isNotBlank(pageClassName),
                "pageClassName can not be null, empty, or whitespace");

        String guiDataDir = Config.getConfigProperty(ConfigProperty.GUI_DATA_DIR);

        String processedPageDomain = StringUtils.defaultString(pageDomain, "");
        String rawDataFile = guiDataDir + "/" + processedPageDomain  + "/" + pageClassName;
        if (processedPageDomain.isEmpty()) {
            rawDataFile = guiDataDir + "/" + pageClassName;
        }

        String yamlFile = rawDataFile + ".yaml";
        String ymlFile = rawDataFile + ".yml";

        GuiMapReader dataProvider;
        if (getFilePath(yamlFile) != null) {
            dataProvider = YamlReaderFactory.createInstance(yamlFile);
        } else if (getFilePath(ymlFile) != null) {
            dataProvider = YamlReaderFactory.createInstance(ymlFile);
        } else {
            // Should be a FileNotFoundException?
            FileNotFoundException e = new FileNotFoundException("Data file does not exist for " + rawDataFile
                    + ". Supported file extensions: yaml, yml.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
        logger.exiting(dataProvider);
        return dataProvider;
    }

    /**
     * Method to get the complete file path.
     * 
     * @param file
     * @return String file path
     */
    private static String getFilePath(String file) {
        logger.entering(file);
        String filePath = null;
        URL fileURL = GuiMapReaderFactory.class.getClassLoader().getResource(file);
        if (fileURL != null) {
            filePath = fileURL.getPath();
        }
        logger.exiting(filePath);
        return filePath;
    }

}
