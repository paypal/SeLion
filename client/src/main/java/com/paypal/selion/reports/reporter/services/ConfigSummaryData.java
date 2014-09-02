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

package com.paypal.selion.reports.reporter.services;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides mechanisms to inject summary information which would then be retrieved by all the SeLion
 * Reporting listeners. This class also houses the default summary that SeLion would be aware of.
 * 
 */
public final class ConfigSummaryData {

    private static Map<String, String> configSummary = new HashMap<String, String>();
    // Map for each TestNG test name -> local SeLion config associated with <test>
    private static Map<String, Map<String, String>> localConfigsMap = new HashMap<String, Map<String, String>>();

    private ConfigSummaryData() {
        // Utility class. So hide the constructor
    }

    /**
     * Initialize the config summary map with Current Date.
     */
    public static void initConfigSummary() {
        addConfigSummary("Current Date",
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));
    }

    public static void initLocalConfigSummary(String testName) {
        Map<String, String> testLocalMap = new HashMap<String, String>();
        testLocalMap.put("Current Date",
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));
        localConfigsMap.put(testName, testLocalMap);
    }

    /**
     * @param key
     *            - A {@link String} that represents the Configuration Name to be displayed in the SeLion Reports.
     * @param value
     *            - A {@link String} that represents the configuration value to be displayed in the SeLion Reports.
     */
    public static void addConfigSummary(String key, String value) {
        configSummary.put(key, value);
    }

    /**
     * @param key
     *            - A {@link String} that represents the test Name to be displayed in the SeLion Reports.
     * @param localConfigMap
     *            - A {@link Map} that represents the configuration values to be displayed in the SeLion Reports.
     */
    public static void addLocalConfigSummary(String key, Map<String, String> localConfigMap) {
        if (localConfigsMap.get(key) == null) {
            localConfigsMap.put(key, localConfigMap);
        } else {
            localConfigsMap.get(key).putAll(localConfigMap);
        }
    }

    /**
     * Helps retrieve the configuration summary to be displayed in the report.
     * 
     * @return - A {@link Map} that represents the configuration summary.
     */
    public static Map<String, String> getConfigSummary() {
        return configSummary;
    }

    /**
     * Helps retrieve the configuration summary for test to be displayed in the report.
     * 
     * @return - A {@link Map} that represents the configuration summary for test.
     */
    public static Map<String, String> getLocalConfigSummary(String testName) {
        return localConfigsMap.get(testName);
    }
}
