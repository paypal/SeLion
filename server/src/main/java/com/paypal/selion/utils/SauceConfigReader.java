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

package com.paypal.selion.utils;

import java.util.logging.Level;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
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

    private String authKey;
    private String sauceURL;
    private String url;
    private String userName;

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

            LOGGER.info("Sauce Config loaded successfully");

        } catch (JsonSyntaxException e) {
            String error = "Error with the JSON of the Sauce Config : " + e.getMessage();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GridConfigurationException(error, e);
        }
    }

    private String getAttributeValue(JsonObject jsonObject, String key) {
        if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
            String value = jsonObject.get(key).getAsString();
            if(StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        
        throw new GridConfigurationException("Invalid property " + key + " in " + SeLionGridConstants.SAUCE_CONFIG_FILE);
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

}
