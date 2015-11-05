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

package com.paypal.selion.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.SauceConfigReader;

/**
 * This customized version of {@link DefaultRemoteProxy} basically helps redirect all traffic to the SauceLabs cloud.
 * 
 */
public class SeLionSauceProxy extends DefaultRemoteProxy {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SeLionSauceProxy.class);

    private final int maxTestCase;

    private final Lock accessLock = new ReentrantLock();

    public SeLionSauceProxy(RegistrationRequest request, Registry registry) {
        super(request, registry);
        maxTestCase = getMaxTestcase();
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        TestSession session = null;
        try {
            accessLock.lock();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            if (getNumberOfTCRunning() <= maxTestCase) {
                String username = (String) requestedCapability.get("sauceUserName");
                String accessKey = (String) requestedCapability.get("sauceApiKey");
                String tunnelId = (String) requestedCapability.get("parent-tunnel");
                if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(accessKey)) {
                    requestedCapability.put("username", username);
                    requestedCapability.put("accessKey", accessKey);
                }
                if (StringUtils.isEmpty(tunnelId)) {
                    requestedCapability.put("parent-tunnel", SauceConfigReader.getInstance().getUserName());
                }
                session = super.getNewSession(requestedCapability);
            }
        } finally {
            accessLock.unlock();
        }
        return session;
    }

    @Override
    public void afterSession(TestSession session) {

    }

    /**
     * Get the total number of test cases running in sauce labs for the primary account.
     * 
     * @return number of test cases running
     */
    public int getNumberOfTCRunning() {
        try {
            String result = getSauceLabsRestApi(SauceConfigReader.getInstance().getURL() + "/activity");
            JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
            return obj.getAsJsonObject("totals").get("all").getAsInt();
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return maxTestCase + 1;
    }

    /**
     * Get the number of test cases running in sauce labs for the sauce labs subaccount/user
     * 
     * @param user
     * @return number of test case running
     */
    public int getNumberOfTCRunningForUser(String user) {
        try {
            String result = getSauceLabsRestApi(SauceConfigReader.getInstance().getURL() + "/activity");
            JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
            return obj.getAsJsonObject("subaccounts").getAsJsonObject(user).get("all").getAsInt();
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Get the maximum number of test case that can run in parallel for the primary account.
     * 
     * @return maximum no of testcase
     */
    public int getMaxTestcase() {
        try {
            String result = getSauceLabsRestApi(SauceConfigReader.getInstance().getURL() + "/limits");
            JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
            return obj.get("concurrency").getAsInt();
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return 0;
    }

    public URL getRemoteHost() {
        try {
            return new URL("http://ondemand.saucelabs.com:80");
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return remoteHost;
    }

    public URL getNodeHost() {
        return remoteHost;
    }

    private String getSauceLabsRestApi(String urlString) {

        URL url;
        HttpURLConnection conn = null;
        String result = "";
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + SauceConfigReader.getInstance().getAuthenticationKey());
            if (conn.getResponseCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;

            while ((output = br.readLine()) != null) {
                result = result + output;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public void teardown() {
        super.teardown();
    }

    public boolean isAlive() {
        // If the sauce server is down the grid is marking node as down. To avoid this it is overridden to always return
        // true.
        return true;
    }
}
