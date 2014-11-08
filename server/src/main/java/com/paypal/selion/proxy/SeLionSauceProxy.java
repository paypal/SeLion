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

package com.paypal.selion.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.grid.web.servlet.handler.RequestType;
import org.openqa.grid.web.servlet.handler.WebDriverRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.paypal.selion.utils.SauceConfigReader;
import com.paypal.selion.utils.SauceConnectManager;

/**
 * This customized version of {@link DefaultRemoteProxy} basically helps redirect all traffic to the SauceLabs cloud.
 * 
 */
public class SeLionSauceProxy extends DefaultRemoteProxy {

    private static final Logger log = Logger.getLogger(SeLionSauceProxy.class.getName());

    private SauceConnectManager manager = new SauceConnectManager();
    private Map<String, String> apiKeys = new HashMap<String, String>();
    private Map<String, Long> lastTime = new HashMap<String, Long>();
    private int maxTestCase = 0;
    private volatile boolean bKill = false;
    private SauceCleanUpThread sauceCleanupThread;

    private Lock accessLock = new ReentrantLock();

    public SeLionSauceProxy(RegistrationRequest request, Registry registry) {
        super(request, registry);
        maxTestCase = getMaxTestcase();
        sauceCleanupThread = new SauceCleanUpThread(this);
        new Thread(sauceCleanupThread, "Sauce CleanUpThread for " + getId()).start();
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        TestSession session = null;
        try {
            accessLock.lock();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }

            if (getNumberOfTCRunning() <= maxTestCase) {
                requestedCapability.put("parent-tunnel", SauceConfigReader.getInstance().getUserName());
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

    @Override
    public void beforeCommand(TestSession session, HttpServletRequest request, HttpServletResponse response) {

        if (request instanceof WebDriverRequest && request.getMethod().equals("POST")) {
            WebDriverRequest seleniumRequest = (WebDriverRequest) request;
            if (seleniumRequest.getRequestType().equals(RequestType.START_SESSION)) {
                String body = seleniumRequest.getBody();
                // convert from String to JSON
                JsonObject json = new JsonParser().parse(body).getAsJsonObject();
                // add username/accessKey
                JsonObject desiredCapabilities = json.getAsJsonObject("desiredCapabilities");
                desiredCapabilities.addProperty("username", (String) session.getRequestedCapabilities().get("sauceUserName"));
                desiredCapabilities.addProperty("accessKey", (String) session.getRequestedCapabilities().get("sauceApiKey"));
                // convert from JSON to String
                seleniumRequest.setBody(json.toString());
            }

        }
        super.beforeCommand(session, request, response);
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
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return maxTestCase + 1;
    }

    /**
     * Get the number of test cases running in sauce labs for the sauce labs subaccount/user
     * @param user
     * @return
     */
    public int getNumberOfTCRunningForUser(String user) {
        try {
            String result = getSauceLabsRestApi(SauceConfigReader.getInstance().getURL() + "/activity");
            JsonObject obj = new JsonParser().parse(result).getAsJsonObject();
            return obj.getAsJsonObject("subaccounts").getAsJsonObject(user).get("all").getAsInt();
        } catch (JsonSyntaxException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
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
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return 0;
    }

    public URL getRemoteHost() {
        try {
            return new URL("http://ondemand.saucelabs.com:80");
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return remoteHost;
    }

    public URL getNodeHost() {
        return remoteHost;
    }

    private String getSauceLabsRestApi(String urlString) {

        URL url = null;
        HttpURLConnection conn = null;
        String result = "";
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization",
                    "Basic " + SauceConfigReader.getInstance().getAuthenticationKey());
            if (conn.getResponseCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;

            while ((output = br.readLine()) != null) {
                result = result + output;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public void teardown() {
        bKill = true;
        super.teardown();
    }

    public boolean isAlive() {
        // If the sauce server is down the grid is marking node as down. To avoid this it is overridden to always return
        // true.
        return true;
    }

    class SauceCleanUpThread implements Runnable {

        private SeLionSauceProxy proxy;

        public SauceCleanUpThread(SeLionSauceProxy proxy) {
            this.proxy = proxy;
        }

        public void run() {
            int i = 0;
            log.fine("cleanup thread starting...");
            while (!proxy.bKill) {
                try {
                    Thread.sleep(30 * 1000); // will cleanup every 5 min
                } catch (InterruptedException e) {
                    log.severe("clean up thread died. " + e.getMessage());
                }

                try {
                    accessLock.lock();
                    if (i > 10) {
                        cleanUpSauceConnect(proxy.manager.getUsers());
                        i = 0;
                    }
                    i++;
                    // need to uncomment this when needed
                    // restartSauceConnect(proxy.manager.getTunnelMap());
                } finally {
                    accessLock.unlock();
                }
            }
        }

        public void cleanUpSauceConnect(List<String> set) {
            for (String temp : set) {
                if (getNumberOfTCRunningForUser(temp) == 0) {

                    log.fine("Going to close the Tunnel For :" + temp);
                    if (System.currentTimeMillis() - lastTime.get(temp) > 120 * 1000) {
                        proxy.manager.closeTunnelsForPlan(temp, null);
                    }
                }
            }
        }

        public void restartSauceConnect(Map<String, Process> tunnelMap) {
            for (Entry<String, Process> temp : tunnelMap.entrySet()) {
                try {
                    temp.getValue().exitValue();
                    proxy.manager.removeUserFromTunnelMap(temp.getKey());
                    log.fine("Proccess Check : Already Closed : " + temp.getKey());
                    log.fine("Proccess Check : Trying to Restart : " + temp.getKey());
                    proxy.manager.openConnection(temp.getKey(), apiKeys.get(temp.getKey()), new File(
                            "Sauce-Connect.jar"), null, null, null);
                } catch (IllegalThreadStateException e) {
                    log.fine("Proccess Check : Working fine : " + temp.getKey());
                } catch (IOException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
}
