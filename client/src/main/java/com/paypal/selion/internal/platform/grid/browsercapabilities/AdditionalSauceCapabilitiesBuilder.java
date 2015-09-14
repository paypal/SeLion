/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.JsonToBeanConverter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.browsercapabilities.DefaultCapabilitiesBuilder;
import com.paypal.selion.platform.utilities.FileAssistant;

/**
 * This class is used to add the additional capabilities that can be specified while executing tests against saucelabs.
 */
public class AdditionalSauceCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

    private static final String USER_NAME = "username";
    private static final String ACCESS_KEY = "accessKey";
    private static final String PARENT_TUNNEL = "parent-tunnel";

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {
        logger.entering(capabilities);

        if (isNonSauceLabsRun()) {
            return capabilities;
        }

        capabilities = appendSauceLabsCredentials(capabilities);
        capabilities = appendSeleniumVersion(capabilities);
        capabilities = appendSauceLabsCapabilities(capabilities);

        logger.exiting(capabilities);
        return capabilities;
    }

    private DesiredCapabilities appendSauceLabsCapabilities(DesiredCapabilities capabilities) {
        logger.entering(capabilities);
        String sauceJSONFileName = Config.getConfigProperty(ConfigProperty.SELENIUM_SAUCELAB_GRID_CONFIG_FILE);
        if (StringUtils.isBlank(sauceJSONFileName)) {
            logger.exiting(capabilities);
            return capabilities;
        }
        try {
            JsonObject jsonObject = new JsonParser().parse(FileAssistant.readFile(sauceJSONFileName)).getAsJsonObject();
            Iterator<Entry<String,JsonElement>> iterator = jsonObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String,JsonElement> entry = iterator.next();
                String capabilityName = entry.getKey();
                Object capabilityValue = new JsonToBeanConverter().convert(Object.class, entry.getValue());
                if ((capabilityValue instanceof String) && (capabilityValue.toString().startsWith("__string__"))) {
                    capabilityValue = capabilityValue.toString().replace("__string__", "");
                }
                capabilities.setCapability(capabilityName, capabilityValue);
            }
            logger.exiting(capabilities);
            return capabilities;
        } catch (Exception e) { //NOSONAR
            String errorMsg = "An error occured while working with the JSON file : " + sauceJSONFileName
                    + ". Root cause: ";
            throw new WebDriverException(errorMsg, e);
        }
    }

    private DesiredCapabilities appendSeleniumVersion(DesiredCapabilities caps) {
        logger.entering(caps);
        String seleniumVersion = new BuildInfo().getReleaseLabel();
        caps.setCapability("selenium-version", seleniumVersion);
        logger.exiting(caps);
        return caps;
    }

    private DesiredCapabilities appendSauceLabsCredentials(DesiredCapabilities caps) {
        logger.entering(caps);
        String sauceUserName = Config.getConfigProperty(ConfigProperty.SAUCELAB_USER_NAME);
        String sauceApiKey = Config.getConfigProperty(ConfigProperty.SAUCELAB_API_KEY);
        String tunnelUserId = Config.getConfigProperty(ConfigProperty.SAUCELAB_TUNNEL_USER_ID);

        if (sauceUserName != null && sauceApiKey != null) {
            caps.setCapability(USER_NAME, sauceUserName);
            caps.setCapability(ACCESS_KEY, sauceApiKey);
            caps.setCapability(PARENT_TUNNEL, tunnelUserId);
        }
        logger.exiting(caps);
        return caps;
    }

    private boolean isNonSauceLabsRun() {
        logger.entering();
        boolean runLocally = isLocalRun();
        boolean isSauceRC = Config.getBoolConfigProperty(ConfigProperty.SELENIUM_USE_SAUCELAB_GRID);
        boolean returnValue = (!isSauceRC || runLocally);
        logger.exiting(returnValue);
        return returnValue;
    }
}
