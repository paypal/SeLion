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

package com.paypal.selion.platform.grid.browsercapabilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.BrowserFlavors;
import com.paypal.selion.platform.utilities.FileAssistant;

/**
 * This class is used to add the additional capabilities that can be specified while executing tests against saucelabs.
 */
public class AdditionalSauceCapabilitiesBuilder extends DefaultCapabilitiesBuilder {

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

    private List<Object> retrieveValuesFromJSONArray(JSONArray jsonArray) throws JSONException {
        logger.entering(jsonArray);
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.get(i));
        }
        logger.exiting(list);
        return list;
    }

    private DesiredCapabilities appendSauceLabsCapabilities(DesiredCapabilities capabilities) {
        logger.entering(capabilities);
        String sauceJSONFileName = Config.getConfigProperty(ConfigProperty.SELENIUM_SAUCELAB_GRID_CONFIG_FILE);
        if (StringUtils.isBlank(sauceJSONFileName)) {
            logger.exiting(capabilities);
            return capabilities;
        }
        try {
            JSONObject jsonObject = new JSONObject(FileAssistant.readFile(sauceJSONFileName));
            Iterator<?> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String capabilityName = (String) iterator.next();
                Object capabilityValue = jsonObject.get(capabilityName);
                if (capabilityValue instanceof JSONArray) {
                    capabilityValue = retrieveValuesFromJSONArray((JSONArray) capabilityValue);
                }
                capabilities.setCapability(capabilityName, capabilityValue);
            }
            logger.exiting(capabilities);
            return capabilities;
        } catch (IOException e) {
            String errorMsg = "Unable to load the saucelabs additional capabilties JSON file : " + sauceJSONFileName
                    + ". Root cause: ";
            throw new WebDriverException(errorMsg, e);
        } catch (JSONException exception) {
            String errorMsg = "An error occured while working with the JSON file : " + sauceJSONFileName
                    + ". Root cause: ";
            throw new WebDriverException(errorMsg, exception);
        }
    }

    private DesiredCapabilities appendSeleniumVersion(DesiredCapabilities caps) {
        logger.entering(caps);
        // for ipad and iphone saucelabs works with only selected versions if selenium-server
        // so leaving the version selection to saucelabs
        if (isIphoneOrIpad()) {
            logger.exiting(caps);
            return caps;
        }
        String seleniumVersion = new org.openqa.selenium.internal.BuildInfo().getReleaseLabel();
        caps.setCapability("selenium-version", seleniumVersion);
        logger.exiting(caps);
        return caps;
    }

    private DesiredCapabilities appendSauceLabsCredentials(DesiredCapabilities caps) {
        logger.entering(caps);
        String sauceUserName = Config.getConfigProperty(ConfigProperty.SAUCELAB_USER_NAME);
        String sauceApiKey = Config.getConfigProperty(ConfigProperty.SAUCELAB_API_KEY);
        if (sauceUserName != null && sauceApiKey != null) {
            caps.setCapability("sauceUserName", sauceUserName);
            caps.setCapability("sauceApiKey", sauceApiKey);
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

    private boolean isIphoneOrIpad() {
        logger.entering();
        BrowserFlavors browser = BrowserFlavors.getBrowser(getLocalConfigProperty(ConfigProperty.BROWSER));
        boolean returnValue = Arrays.asList(BrowserFlavors.getIOSDeviceFlavors()).contains(browser);
        logger.exiting(returnValue);
        return returnValue;
    }
}