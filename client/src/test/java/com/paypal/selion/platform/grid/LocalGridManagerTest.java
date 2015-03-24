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

package com.paypal.selion.platform.grid;

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertFalse;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertNotNull;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.platform.grid.LocalGridManager;
import com.paypal.selion.platform.grid.WebDriverPlatform;

public class LocalGridManagerTest {

    @Test(groups = { "local-grid-tests" })
    public void testlocalGridManagerStartHub() throws MalformedURLException, IOException, JSONException {
        String runLocally = Config.getConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY);
        Config.setConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY, "true");

        AbstractTestSession testSession = new AbstractTestSession() {
            
            @Override
            public SeLionSession startSession(Map<String, SeLionSession> sessions) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public SeLionSession startSesion() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public void initializeTestSession(InvokedMethodInformation method) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void initializeTestSession(InvokedMethodInformation method, Map<String, SeLionSession> sessionMap) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public WebDriverPlatform getPlatform() {
                return WebDriverPlatform.WEB;
            }
            
            @Override
            public void closeCurrentSession(Map<String, SeLionSession> sessionMap, InvokedMethodInformation result) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void closeAllSessions(Map<String, SeLionSession> sessionMap) {
                // TODO Auto-generated method stub
                
            }
        };
        String msg = "proxy found";
        try {
            LocalGridManager.spawnLocalHub(testSession);
            assertTrue(getHubStatus(), "The Hub should have started locally");
            JSONObject nodeStatus = getNodeStatus();
            assertNotNull(nodeStatus, "The node status should not have been null");
            assertTrue(nodeStatus.getBoolean("success"),
                    "The node should have started properly and hooked itself to the local Grid.");
            assertTrue(nodeStatus.getString("msg").contains(msg), "The node should have been found");
        } finally {
            LocalGridManager.shutDownHub();
            Config.setConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY, runLocally);
            assertFalse(getHubStatus(), "The Hub should have been shutDown");
        }
    }

    public JSONObject getNodeStatus() throws MalformedURLException, IOException, JSONException {
        JSONObject nodeStatus = null;
        String port = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String url = "http://localhost:" + port + "/grid/api/proxy?id=http://localhost:5555";
        URLConnection connection = new URL(url).openConnection();
        InputStream isr = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(isr));
        StringBuffer actualResponse = new StringBuffer();
        String eachLine = null;
        while ((eachLine = br.readLine()) != null) {
            actualResponse.append(eachLine);
        }
        if (actualResponse != null && actualResponse.length() > 0) {
            nodeStatus = new JSONObject(actualResponse.toString());
        }
        return nodeStatus;

    }

    public boolean getHubStatus() throws MalformedURLException, IOException, JSONException {
        boolean hubStatus = false;
        String port = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String url = "http://localhost:" + port + "/grid/api/hub";
        URLConnection hubConnection = new URL(url).openConnection();
        try {
            InputStream isr = hubConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(isr));
            StringBuffer information = new StringBuffer();
            String eachLine = null;
            while ((eachLine = br.readLine()) != null) {
                information.append(eachLine);
            }
            JSONObject fullResponse = new JSONObject(information.toString());
            if (fullResponse != null) {
                hubStatus = fullResponse.getBoolean("success");
            }
        } catch (ConnectException e) {
            hubStatus = false;
        }

        return hubStatus;
    }
}
