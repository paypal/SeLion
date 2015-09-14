/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion.reports.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.paypal.selion.internal.reports.services.ReporterDateFormatter;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class provides mechanisms to inject metadata in the report that will be used to format the configuration data
 * when the HTML report is rendered. Supported metadata includes the display label for the configuration keys.
 */
public final class ReporterConfigMetadata {

    public static final String DISPLAY_LABEL = "displayLabel";

    private static Map<String, Map<String, String>> reporterMetadata = new HashMap<String, Map<String, String>>();
    private static SimpleLogger logger = SeLionLogger.getLogger();
    private static final List<String> supportedMetaDataProperties = new ArrayList<String>(Arrays.asList(DISPLAY_LABEL));

    private ReporterConfigMetadata() {
        // Utility class. So hide the constructor
    }

    /**
     * Initialize the report dictionary map with Current Date.
     */
    public static void initReporterMetadata() {
        addReporterMetadataItem(ReporterDateFormatter.CURRENTDATE, ReporterConfigMetadata.DISPLAY_LABEL, "Current Date");
    }

    /**
     * Adds an new item to the reporter metadata.
     * 
     * @param key
     *            - A {@link String} that represents the property name contained in JsonRuntimeReporter file.
     * @param itemType
     *            - A {@link String} that represents the (supported) type of metadata.
     * @param value
     *            - A {@link String} that represents the reader friendly value to be displayed in the SeLion HTML
     *            Reports.
     */
    public static void addReporterMetadataItem(String key, String itemType, String value) {
        logger.entering(new Object[] { key, itemType, value });
        if (StringUtils.isNotBlank(value) && supportedMetaDataProperties.contains(itemType)) {

            Map<String, String> subMap = reporterMetadata.get(key);
            if (null == subMap) {
                subMap = new HashMap<String, String>();
            }
            subMap.put(itemType, value);
            reporterMetadata.put(key, subMap);
        } else {
            String message = "Key/value pair for '" + key + "' for '" + itemType
                    + "' was not inserted into report metadata.";
            logger.fine(message);
        }
    }

    /**
     * Helps retrieve the configuration metadata for the report.
     * 
     * @return - A {@link Map} that represents the configuration summary.
     */
    public static Map<String, Map<String, String>> getReporterMetaData() {
        return reporterMetadata;
    }

    /**
     * This method will generate JSON string representation of the all items in current ReportConfigMetadata.
     */
    public static String toJsonAsString() {
        logger.entering();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject configItem = new JsonObject();
        for (Entry<String, Map<String, String>> entry : ReporterConfigMetadata.getReporterMetaData().entrySet()) {
            Map<String, String> subMap = entry.getValue();
            for (Entry<String, String> subEntry : subMap.entrySet()) {
                JsonObject configSubItem = new JsonObject();
                configSubItem.addProperty(subEntry.getKey(), subEntry.getValue());
                configItem.add(entry.getKey(), configSubItem);
            }
        }

        String json = gson.toJson(configItem);
        logger.exiting(json);
        return json;
    }

}
