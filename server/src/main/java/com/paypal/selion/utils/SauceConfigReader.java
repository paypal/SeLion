/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                          |
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

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.openqa.grid.common.JSONConfigurationUtils;
import org.openqa.grid.common.exception.GridConfigurationException;

import com.google.gson.JsonObject;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * A configuration utility that is internally used by SeLion to parse sauce configuration json file.
 */
@ThreadSafe
public final class SauceConfigReader {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SauceConfigReader.class);

    public static final String AUTHENTICATION_KEY = "authenticationKey";
    public static final String SAUCE_URL = "sauceURL";
    public static final String SAUCE_TIMEOUT = "sauceTimeout";
    public static final String SAUCE_RETRY = "sauceRetries";

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

    private static final class SauceConfigReaderHolder {
        private static final SauceConfigReader INSTANCE = new SauceConfigReader();
        private static volatile boolean dirty = true;

        private static synchronized void invalidate() {
            dirty = true;
        }

        private static synchronized boolean isDirty() {
            return dirty;
        }

        private static synchronized void reload() {
            if (!dirty) {
                return;
            }
            INSTANCE.loadConfig();
            dirty = false;
        }

        private static SauceConfigReader getSauceConfigReader() {
            return INSTANCE;
        }
    }

    /**
     * @return a {@link SauceConfigReader} object that can be used to retrieve values from the Configuration object as
     *         represented by the JSON file. Throws a {@link GridConfigurationException} on instance load error.
     */
    public static SauceConfigReader getInstance() {
        if (SauceConfigReaderHolder.isDirty()) {
            SauceConfigReaderHolder.reload();
        }
        return SauceConfigReaderHolder.getSauceConfigReader();
    }

    private SauceConfigReader() {
        // intentionally left blank and hidden
    }

    /**
     * Invalidates the current Sauce config and causes it to reload from disk at next {@link #getInstance()} call
     */
    public void invalidate() {
        SauceConfigReaderHolder.invalidate();
    }

    /**
     * Load the all the properties from JSON file(sauceConfig.json)
     */
    private void loadConfig() {
        try {
            JsonObject jsonObject = JSONConfigurationUtils.loadJSON(SeLionGridConstants.SAUCE_CONFIG_FILE);

            authKey = getAttributeValue(jsonObject, AUTHENTICATION_KEY);
            if (StringUtils.isBlank(authKey)) {
                final String error = "Invalid authenticationKey specified";
                LOGGER.log(Level.SEVERE, error);
                throw new GridConfigurationException(error); // caught below
            }

            sauceURL = getAttributeValue(jsonObject, SAUCE_URL);
            if (StringUtils.isBlank(sauceURL)) {
                final String error = "Invalid sauceURL specified";
                LOGGER.log(Level.SEVERE, error);
                throw new GridConfigurationException(error); // caught below
            }

            final String decodedKey = new String(Base64.decodeBase64(authKey));
            if (!StringUtils.contains(decodedKey, ":")) {
                final String error = "Decoded key error. Invalid authenticationKey specified";
                LOGGER.log(Level.SEVERE, error);
                throw new GridConfigurationException(error); // caught below
            }
            userName = decodedKey.substring(0, decodedKey.indexOf(":"));

            url = sauceURL + "/" + userName;

            if (jsonObject.has(SAUCE_RETRY) && !jsonObject.get(SAUCE_RETRY).isJsonNull()) {
                sauceRetry = jsonObject.get(SAUCE_RETRY).getAsInt();
            }

            if (jsonObject.has(SAUCE_TIMEOUT) && !jsonObject.get(SAUCE_TIMEOUT).isJsonNull()) {
                sauceTimeout = jsonObject.get(SAUCE_TIMEOUT).getAsInt();
            }

            LOGGER.info("Sauce Config loaded successfully");

        } catch (RuntimeException e) { // NOSONAR
            final String error = "Error parsing sauceConfig.json: " + e.getMessage();
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
        LOGGER.fine("authKey: " + authKey);
        return authKey;
    }

    /**
     * @return the sauceURL specified in the configuration file
     */
    public String getSauceURL() {
        LOGGER.fine("sauceURL: " + sauceURL);
        return sauceURL;
    }

    /**
     * @return the sauce labs user name
     */
    public String getUserName() {
        LOGGER.fine("userName: " + userName);
        return userName;
    }

    /**
     * @return the fully qualified sauce url
     */
    public String getURL() {
        LOGGER.fine("url: " + url);
        return url;
    }

    /**
     * @return the timeout in milleseconds for sauce
     */
    public int getSauceTimeout() {
        LOGGER.fine("sauceTimeout: " + sauceTimeout);
        return sauceTimeout;
    }

    /**
     * @return the number of retries with sauce
     */
    public int getSauceRetry() {
        LOGGER.fine("sauceRetry: " + sauceRetry);
        return sauceRetry;
    }
}
