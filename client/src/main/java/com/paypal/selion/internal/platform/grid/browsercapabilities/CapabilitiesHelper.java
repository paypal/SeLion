/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

package com.paypal.selion.internal.platform.grid.browsercapabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A utility class which is internally used by SeLion to load capabilities via {@link DefaultCapabilitiesBuilder}s which
 * are specified through the{@link ServiceLoader} mechanism, defined
 * {@link ConfigProperty#SELENIUM_CUSTOM_CAPABILITIES_PROVIDER}s, and/or specified through the {@link MobileTest} or
 * {@link WebTest} annotations.
 */
public final class CapabilitiesHelper {
    private static SimpleLogger logger = SeLionLogger.getLogger();

    private CapabilitiesHelper() {
        // Utility class. So hide the constructor
    }

    /**
     * @return A list of {@link DesiredCapabilities} found via {@link ServiceLoader}.
     */
    public static List<DesiredCapabilities> retrieveCustomCapsViaServiceLoaders() {
        logger.entering();
        ServiceLoader<DefaultCapabilitiesBuilder> allCapsBuilderInstances = ServiceLoader
                .load(DefaultCapabilitiesBuilder.class);
        List<DesiredCapabilities> allCaps = new ArrayList<>();
        for (DefaultCapabilitiesBuilder eachCapsBuilderInstance : allCapsBuilderInstances) {
            allCaps.add(eachCapsBuilderInstance.createCapabilities());
        }
        logger.exiting(allCaps);
        return allCaps;
    }

    /**
     * @return A list of {@link DesiredCapabilities} found via the
     *         {@link ConfigProperty#SELENIUM_CUSTOM_CAPABILITIES_PROVIDER}
     */
    @SuppressWarnings("unchecked")
    public static List<DesiredCapabilities> retrieveCustomCapsObjects() {
        logger.entering();
        List<DesiredCapabilities> capsObjects = new ArrayList<>();

        List<Object> customCapsProv = ConfigManager.getConfig(Grid.getTestSession().getXmlTestName())
                .getListConfigProperty(ConfigProperty.SELENIUM_CUSTOM_CAPABILITIES_PROVIDER);

        if (customCapsProv == null || customCapsProv.isEmpty()) {
            return capsObjects;
        }
        for (Object eachProvider : customCapsProv) {
            // it is possible to get a List of { "", "   ", } depending on what the user specified.
            String providerString = (String) eachProvider;
            if (StringUtils.isBlank(providerString)) {
                continue;
            }

            try {
                Class<?> provider = Class.forName(providerString);
                if (DefaultCapabilitiesBuilder.class.isAssignableFrom(provider)) {
                    capsObjects.add(retrieveCustomCapabilities((Class<? extends DefaultCapabilitiesBuilder>) provider));
                } else {
                    logger.info("Skipping " + providerString + " because it is not a subclass of "
                            + DefaultCapabilitiesBuilder.class.getCanonicalName());
                }
            } catch (ClassNotFoundException e) {
                // Throw an Un-checked exception and let the user know that the custom capabilities that they provided
                // us with has problems.Doing this will prevent their tests from running under the assumption that the
                // capabilities they provided were fine.
                throw new IllegalStateException(e);
            }
        }
        logger.exiting(capsObjects);
        return capsObjects;
    }

    /**
     * Parse capabilities from an array of String which uses the "name:value" format
     * 
     * @param capabilitiesArray
     *            the capabilities to parse
     * @return the parsed capabilities as a {@link DesiredCapabilities} object
     */
    public static DesiredCapabilities retrieveCustomCapabilities(String[] capabilitiesArray) {
        logger.entering((Object[]) capabilitiesArray);

        DesiredCapabilities caps = new DesiredCapabilities();
        if (capabilitiesArray.length != 0) {
            Map<String, Object> capabilityMap = parseIntoCapabilities(capabilitiesArray);
            // We found some capabilities. Lets merge them.
            caps = new DesiredCapabilities(capabilityMap);
        }

        logger.exiting(caps);
        return caps;
    }

    /**
     * Acquire capabilities from the TestNG {@link InvokedMethodInformation}
     * 
     * @param methodInfo
     *            the TestNG {@link InvokedMethodInformation}
     * @return the provided {@link DesiredCapabilities} which are associated with the {@link InvokedMethodInformation}
     */
    @Deprecated
    public static DesiredCapabilities retrieveCustomCapabilities(InvokedMethodInformation methodInfo) {
        logger.entering(methodInfo);

        DesiredCapabilities caps = new DesiredCapabilities();
        Object additionalCaps =
                methodInfo.getTestAttribute(com.paypal.selion.configuration.ExtendedConfig.CAPABILITIES.getConfig());
        if (additionalCaps instanceof DesiredCapabilities) {
            caps = (DesiredCapabilities) additionalCaps;
        }

        logger.exiting(caps);
        return caps;
    }

    /**
     * Acquire capabilities from a {@link DefaultCapabilitiesBuilder} provider.
     * 
     * @param builder
     *            the {@link DefaultCapabilitiesBuilder} provider to acquire capabilities from
     * @return the {@link DesiredCapabilities} which came from the providers.
     */
    public static DesiredCapabilities retrieveCustomCapabilities(Class<? extends DefaultCapabilitiesBuilder> builder) {
        logger.entering(builder);

        DesiredCapabilities caps = new DesiredCapabilities();
        if (builder != null && !builder.getName().equals(DefaultCapabilitiesBuilder.class.getName())) {
            try {
                caps = builder.newInstance().createCapabilities();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Unable to apply desired capabilities from " + builder.getName(), e);
            }
        }

        logger.exiting(caps);
        return caps;
    }

    private static Map<String, Object> parseIntoCapabilities(String[] capabilities) {
        Map<String, Object> capabilityMap = new HashMap<>();
        for (String eachCapability : capabilities) {
            // split into key/value at the ':' character
            String[] keyValuePair = eachCapability.split(":", 2);
            if (keyValuePair.length == 2) {
                String value = keyValuePair[1];
                Object desiredCapability = value;
                // treat true/false values surrounded with ' marks as strings
                if (value.startsWith("'") && value.endsWith("'")) {
                    String trimmedValue = StringUtils.mid(value, 1, value.length() - 2);
                    if (trimmedValue.equalsIgnoreCase("true")) {
                        desiredCapability = "true";
                    } else if (trimmedValue.equalsIgnoreCase("false")) {
                        desiredCapability = "false";
                    }
                } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    desiredCapability = Boolean.parseBoolean(value);
                }
                capabilityMap.put(keyValuePair[0], desiredCapability);
            } else {
                StringBuilder errMsg = new StringBuilder();
                errMsg.append("Capabilities are to be provided as name value pair separated by colons. ");
                errMsg.append("For e.g., capabilityName:capabilityValue");
                throw new IllegalArgumentException(errMsg.toString());
            }
        }
        return capabilityMap;
    }

}
