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

package com.paypal.selion.reports.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.paypal.selion.internal.reports.services.ReporterDateFormatter;
import com.paypal.selion.logger.SeLionLogger;

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
        addConfigSummary(ReporterDateFormatter.CURRENTDATE, ReporterDateFormatter.getISO8601String(new Date()));
    }

    public static void initLocalConfigSummary(String testName) {
        Map<String, String> testLocalMap = new HashMap<String, String>();
        testLocalMap.put(ReporterDateFormatter.CURRENTDATE, ReporterDateFormatter.getISO8601String(new Date()));
        localConfigsMap.put(testName, testLocalMap);
    }

    /**
     * @param key
     *            - A {@link String} that represents the Configuration Name to be displayed in the SeLion Reports.
     * @param value
     *            - A {@link String} that represents the configuration value to be displayed in the SeLion Reports.
     */
    public static void addConfigSummary(String key, String value) {
        addConfigSummary(key, value, null);
    }

    /**
     * @param key
     *            - A {@link String} that represents the Configuration Name to be displayed in the SeLion Reports.
     * @param value
     *            - A {@link String} that represents the configuration value to be displayed in the SeLion Reports.
     * @param testName
     *            - A {@link String} that if specified, will add the configuration value to the specified test to be
     *            displayed in the SeLion Reports. If not specified(null or empty), the configuration value will be
     *            added to the global map for the SeLion Reports.
     */
    public static void addConfigSummary(String key, String value, String testName) {
        if (StringUtils.isBlank(testName)) {
            configSummary.put(key, value);
        } else {
            if (getLocalConfigSummary(testName) != null) {
                getLocalConfigSummary(testName).put(key, value);
            } else {
                String message = "Error trying to insert key/value pair " + key + "/" + " into localConfigSummary map.  Map key "
                        + testName + " does not exist in the localConfigSummary map.";
                SeLionLogger.getLogger().fine(message);
            }
        }
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
