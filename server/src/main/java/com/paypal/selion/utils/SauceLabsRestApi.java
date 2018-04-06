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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * A simple helper class for querying some of the sauce labs account details via their rest api.
 */
public final class SauceLabsRestApi {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SauceLabsRestApi.class);
    public static final int MAX_CACHE = 16;
    private final String sauceAuthenticationKey;
    private final String sauceUrl;
    private final int sauceTimeout;
    private final int sauceRetryCount;
    private int maxTestCase = -1;
    private volatile Map<String, Boolean> accountCache;

    final class SauceLabsHttpResponse {
        private StringBuilder entity;
        private int status = HttpStatus.SC_NOT_FOUND;

        public SauceLabsHttpResponse() {
            entity = new StringBuilder();
        }

        public SauceLabsHttpResponse(HttpURLConnection connection) throws IOException {
            this();
            status = connection.getResponseCode();
            try (BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())))) {
                String input;
                while ((input = br.readLine()) != null) {
                    entity.append(input);
                }
            }
        }

        public String getEntity() {
            return entity.toString();
        }

        public JsonObject getEntityAsJsonObject() {
            return new JsonParser().parse(getEntity()).getAsJsonObject();
        }

        public int getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "[" + getStatus() + ", " + getEntity() + "]";
        }
    }

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
        accountCache = new ConcurrentHashMap<String, Boolean>();
    }

    private SauceLabsHttpResponse doSauceRequest(String path) throws IOException {
        return doSauceRequest(new URL(sauceUrl + path), sauceAuthenticationKey, sauceTimeout, sauceRetryCount);
    }

    private SauceLabsHttpResponse doSauceRequest(URL url, String authKey, int timeout, int retry) throws IOException {
        LOGGER.entering(url.toExternalForm());

        HttpURLConnection conn = null;
        SauceLabsHttpResponse result = new SauceLabsHttpResponse();
        int numRetriesOnFailure = 0;

        do {
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Basic " + authKey);
                result = new SauceLabsHttpResponse(conn);
            } catch (IOException e) {
                if (numRetriesOnFailure == retry) {
                    throw e;
                }
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } while ((result.getStatus() != HttpStatus.SC_OK) && (numRetriesOnFailure++ < retry));

        LOGGER.exiting(result);
        return result;
    }

    /**
     * Get the total number of test cases running in sauce labs for the primary account.
     *
     * @return number of test cases running, <code>-1</code> on failure calling sauce labs.
     */
    public int getNumberOfTCRunning() {
        LOGGER.entering();
        int tcRunning = -1;
        try {
            SauceLabsHttpResponse result = doSauceRequest("/activity");
            JsonObject obj = result.getEntityAsJsonObject();
            tcRunning = obj.getAsJsonObject("totals").get("all").getAsInt();
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to get number of test cases running.", e);
        }
        LOGGER.exiting(tcRunning);
        return tcRunning;
    }

    /**
     * Get the number of test cases running in sauce labs for the sauce labs sub-account/user
     *
     * @param user
     *            id of the sub-account
     * @return number of test case running or <code>-1</code> on failure
     */
    public int getNumberOfTCRunningForSubAccount(String user) {
        LOGGER.entering(user);
        int tcRunning = -1;
        try {
            SauceLabsHttpResponse result = doSauceRequest("/activity");
            JsonObject obj = result.getEntityAsJsonObject();
            tcRunning = obj.getAsJsonObject("subaccounts").getAsJsonObject(user).get("all").getAsInt();
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to get number of test cases running for sub-account.", e);
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
                SauceLabsHttpResponse result = doSauceRequest("/limits");
                JsonObject obj = result.getEntityAsJsonObject();
                maxTestCase = obj.get("concurrency").getAsInt();
            } catch (JsonSyntaxException | IllegalStateException | IOException e) {
                LOGGER.log(Level.SEVERE, "Unable to get max concurrency.", e);
            }
        }
        LOGGER.exiting(maxTestCase);
        return maxTestCase;
    }

    private void addToAccountCache(String md5, boolean valid) {
        if (accountCache.size() >= MAX_CACHE) {
            // don't let the cache grow more than MAX_CACHE
            accountCache.clear();
        }
        accountCache.put(md5, valid);
    }

    private String md5(String value) {
        return DigestUtils.md5Hex(value);
    }

    /**
     * Determine if the account credentials specified are valid by calling the sauce rest api. Uses a local account
     * cache for credentials which have already been presented. Cached credentials expire when the cache reaches a size
     * of {@link SauceLabsRestApi#MAX_CACHE}
     * 
     * @param username
     *            the user name
     * @param apiKey
     *            the sauce labs api access key
     * @return <code>true</code> on success. <code>false</code> if unauthorized or unable to call sauce.
     */
    public synchronized boolean isAuthenticated(String username, String apiKey) {

        LOGGER.entering();
        final String key = username + ":" + apiKey;
        final String authKey = new String(Base64.encodeBase64(key.getBytes()));

        if (accountCache.containsKey(md5(authKey))) {
            final boolean authenticated = accountCache.get(md5(authKey));
            LOGGER.exiting(authenticated);
            return authenticated;
        }

        SauceLabsHttpResponse response;
        try {
            final URL url = new URL(SauceConfigReader.getInstance().getSauceURL() + "/users/" + username);
            response = doSauceRequest(url, authKey, sauceTimeout, 0);
            if (response.getStatus() == HttpStatus.SC_OK) {
                addToAccountCache(md5(authKey), true);
                LOGGER.exiting(true);
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to communicate with sauce labs api.", e);
        }
        // TODO don't add to cache if sauce api is down
        addToAccountCache(md5(authKey), false);

        LOGGER.exiting(false);
        return false;
    }
}
