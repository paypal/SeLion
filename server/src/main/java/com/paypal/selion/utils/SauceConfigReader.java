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

package com.paypal.selion.utils;

import java.util.logging.Level;

import org.apache.commons.codec.binary.Base64;
import org.openqa.grid.common.JSONConfigurationUtils;
import org.openqa.grid.common.exception.GridConfigurationException;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * A configuration utility that is internally used by SeLion to parse sauce configuration json file.
 */
public class SauceConfigReader {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SauceConfigReader.class);
    private static SauceConfigReader reader = new SauceConfigReader();

    private static final String SAUCE_TIMEOUT = "sauceTimeout";
    private static final String SAUCE_RETRY = "sauceRetries";

    // Default of 10 seconds for connection & read
    private static final int DEFAULT_TIMEOUT = 10 * 1000;
    // Number of retries before giving up on sauce
    private static final int DEFAULT_RETRY_COUNT = 2;

    private String authKey;
    private String sauceURL;
    private String url;
    private String userName;

    // The connection & read timeout for sauce REST calls in milliseconds.
    private int sauceTimeout = DEFAULT_TIMEOUT;
    private int sauceRetry = DEFAULT_RETRY_COUNT;

    /**
     * @return - A {@link SauceConfigReader} object that can be used to retrieve values from the Configuration object as
     *         represented by the JSON file
     */
    public static SauceConfigReader getInstance() {
        return reader;
    }

    private SauceConfigReader() {
        loadConfig();
    }

    /**
     * Load the all the properties from JSON file(sauceConfig.json)
     */
    public void loadConfig() {
        try {
            JsonObject jsonObject = JSONConfigurationUtils.loadJSON(SeLionGridConstants.SAUCE_CONFIG_FILE);

            authKey = getAttributeValue(jsonObject, "authenticationKey");

            sauceURL = getAttributeValue(jsonObject, "sauceURL");

            String decodedKey = new String(Base64.decodeBase64(authKey));
            userName = decodedKey.substring(0, decodedKey.indexOf(":"));

            url = sauceURL + "/" + userName;

            if (jsonObject.has(SAUCE_RETRY) && !jsonObject.get(SAUCE_RETRY).isJsonNull()) {
                sauceRetry = jsonObject.get(SAUCE_RETRY).getAsInt();
            }

            if (jsonObject.has(SAUCE_TIMEOUT) && !jsonObject.get(SAUCE_TIMEOUT).isJsonNull()) {
                sauceTimeout = jsonObject.get(SAUCE_TIMEOUT).getAsInt();
            }

            LOGGER.info("Sauce Config loaded successfully");

        } catch (JsonSyntaxException e) {
            String error = "Error with the JSON of the Sauce Config : " + e.getMessage();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GridConfigurationException(error, e);
        }
    }

    private String getAttributeValue(JsonObject jsonObject, String key) {
        String value = null;
        if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
            value = jsonObject.get(key).getAsString();
        }
        return value;
    }

    /**
     * @return the access key associated with the saucelabs account
     */
    public String getAuthenticationKey() {
        LOGGER.info("authKey: " + authKey);
        return authKey;
    }

    /**
     * @return the sauceURL specified in the configuration file
     */
    public String getSauceURL() {
        LOGGER.info("sauceURL: " + sauceURL);
        return sauceURL;
    }

    /**
     * @return the sauce labs user name
     */
    public String getUserName() {
        LOGGER.info("userName: " + userName);
        return userName;
    }

    /**
     * @return the fully qualified sauce url
     */
    public String getURL() {
        LOGGER.info("url: " + url);
        return url;
    }

    /**
     * @return the timeout in milleseconds for sauce
     */
    public int getSauceTimeout() {
        return sauceTimeout;
    }

    /**
     * @return the number of retries with sauce
     */
    public int getSauceRetry() {
        return sauceRetry;
    }
}
