/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.paypal.selion.logging.SeLionGridLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

/**
 * A simple helper class for querying some of the sauce labs account details via their rest api.
 */
public final class SauceLabsRestApi {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SauceLabsRestApi.class);
    private final String sauceAuthenticationKey;
    private final String sauceUrl;
    private final int sauceTimeout;
    private final int sauceRetryCount;
    private int maxTestCase = -1;

    /**
     * Creates a new instance. Uses the current (@link SauceConfigReader#getInstance()} to initialize sauce connection
     * details (url, authKey, etc) that will be used.
     */
    public SauceLabsRestApi() {
        SauceConfigReader reader = SauceConfigReader.getInstance();
        sauceAuthenticationKey = reader.getAuthenticationKey();
        sauceUrl = reader.getURL();
        sauceTimeout = reader.getSauceTimeout();
        sauceRetryCount = reader.getSauceRetry();
    }

    private String getSauceLabsRestApi(String urlString) {
        LOGGER.entering(urlString);

        HttpURLConnection conn = null;
        StringBuilder result = new StringBuilder();
        int numRetriesOnFailure = 0;
        do {
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(sauceTimeout);
                conn.setReadTimeout(sauceTimeout);

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Basic " + sauceAuthenticationKey);
                if (conn.getResponseCode() != HttpStatus.SC_OK) {
                    throw new IOException("Failed: HTTP error code: " + conn.getResponseCode()); // caught below
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                String input;
                while ((input = br.readLine()) != null) {
                    result.append(input);
                }
                br.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } while (StringUtils.isEmpty(result.toString()) && numRetriesOnFailure++ < sauceRetryCount);

        LOGGER.exiting(result.toString());
        return result.toString();
    }

    /**
     * Get the total number of test cases running in sauce labs for the primary account.
     *
     * @return number of test cases running or <code>0</code> on failure calling sauce labs
     */
    public int getNumberOfTCRunning() {
        LOGGER.entering();
        int tcRunning = getMaxConcurrency() + 1;
        try {
            String result = getSauceLabsRestApi(sauceUrl + "/activity");
            JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
            tcRunning = obj.getAsJsonObject("totals").get("all").getAsInt();
        } catch (JsonSyntaxException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        LOGGER.exiting(tcRunning);
        return tcRunning;
    }

    /**
     * Get the number of test cases running in sauce labs for the sauce labs subaccount/user
     *
     * @param user
     *            id of the sub-account
     * @return number of test case running or <code>-1</code> on failure
     */
    public int getNumberOfTCRunningForSubAccount(String user) {
        LOGGER.entering(user);
        int tcRunning = -1;
        try {
            String result = getSauceLabsRestApi(sauceUrl + "/activity");
            JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
            tcRunning = obj.getAsJsonObject("subaccounts").getAsJsonObject(user).get("all").getAsInt();
        } catch (JsonSyntaxException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        LOGGER.exiting(tcRunning);
        return tcRunning;
    }

    /**
     * Get the maximum number of test case that can run in parallel for the primary account.
     *
     * @return maximum number of test case or <code>-1</code> on failure calling sauce labs
     */
    public int getMaxConcurrency() {
        LOGGER.entering();
        if (maxTestCase == -1) {
            try {
                String result = getSauceLabsRestApi(sauceUrl + "/limits");
                JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
                maxTestCase = obj.get("concurrency").getAsInt();
            } catch (JsonSyntaxException | IllegalStateException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        LOGGER.exiting(maxTestCase);
        return maxTestCase;
    }
}
