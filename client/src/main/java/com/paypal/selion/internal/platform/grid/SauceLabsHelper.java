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

package com.paypal.selion.internal.platform.grid;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.testng.Reporter;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A Helper class that is internally used by SeLion to embed the current test's Sauce Labs Job URL to the Test Report.
 * 
 */
public class SauceLabsHelper {
    private static final String ALGORITHM = "HmacMD5";
    private String jobURL;
    private final String userName;
    private final String apiKey;
    private final SimpleLogger logger = SeLionLogger.getLogger();

    public SauceLabsHelper() {
        userName = Config.getConfigProperty(ConfigProperty.SAUCELAB_USER_NAME);
        apiKey = Config.getConfigProperty(ConfigProperty.SAUCELAB_API_KEY);
    }

    /**
     * A utility method that leverages the TestNG provided {@link Reporter} class to embed the current test's Sauce Labs
     * Job link to the test reports. This method will not do anything if the following conditions aren't met.
     * <ul>
     * <li>The test is not targeted at the SauceLabs cloud.
     * <li>If there was any problems in fetching the current test's browser reference [i.e., if {@link Grid#driver()}
     * returned a <code>null</code> value.]
     * <li>If there were any unexpected problems that were encountered when attempting to encode the SauceLabs
     * credentials using <code>HmacMD5</code> algorithm.
     * </ul>
     */
    public void embedSauceLabsJobUrlToTestReport() {
        logger.entering();
        if (isNonSauceLabsRun()) {
            logger.exiting();
            return;
        }
        String url = getJobUrl();
        if (url == null) {
            logger.exiting();
            return;
        }
        Reporter.log(String.format("<b>SauceLabs Job URL available <a href='%s' target='_blank'>here</a></b>", url));
        logger.exiting();
    }

    private boolean isNonSauceLabsRun() {
        logger.entering();
        boolean runLocally = isLocalRun();
        boolean isSauceRC = Config.getBoolConfigProperty(ConfigProperty.SELENIUM_USE_SAUCELAB_GRID);
        boolean returnValue = (!isSauceRC || runLocally);
        logger.exiting(returnValue);
        return returnValue;
    }

    public boolean isLocalRun() {
        return Config.getBoolConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY);
    }

    private String encodeAuthToken(String message) {
        logger.entering(message);
        try {
            String key = String.format("%s:%s", userName, apiKey);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(message.getBytes());
            String encodedString = Hex.encodeHexString(rawHmac);
            logger.exiting(encodedString);
            return encodedString;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            SeLionLogger.getLogger().log(Level.SEVERE, "Encountered errors when encoding the Sauce Credentials", e);
            logger.exiting(new Object[] {null});
            return null;
        }
    }

    private String getSessionId() {
        logger.entering();
        String sessionId = Grid.driver().getSessionId().toString();
        logger.exiting(sessionId);
        return sessionId;
    }

    private String getJobUrl() {
        logger.entering();
        String sessionid = getSessionId();
        String encodedToken = encodeAuthToken(sessionid);
        if (encodedToken == null) {
            logger.exiting();
            return null;
        }
        jobURL = String.format("https://saucelabs.com/jobs/%s?auth=%s", sessionid, encodedToken);

        logger.exiting(jobURL);
        return jobURL;
    }
}
