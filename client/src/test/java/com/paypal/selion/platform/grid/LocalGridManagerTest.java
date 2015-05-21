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

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.LocalGridManager;

public class LocalGridManagerTest {

    @Test(groups = { "local-grid-tests" }, singleThreaded = true)
    public void testlocalGridManagerStartHub() throws MalformedURLException, IOException {
        String runLocally = Config.getConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY);
        Config.setConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY, "true");

        String msg = "proxy found";
        try {
            WebTestSession testSession = new WebTestSession();
            LocalGridManager.spawnLocalHub(testSession);
            assertTrue(getHubStatus(), "The Hub should have started locally");
            JsonObject nodeStatus = getNodeStatus();
            assertNotNull(nodeStatus, "The node status should not have been null");
            assertTrue(nodeStatus.get("success").getAsBoolean(),
                    "The node should have started properly and hooked itself to the local Grid.");
            assertTrue(nodeStatus.get("msg").getAsString().contains(msg), "The node should have been found");
        } finally {
            LocalGridManager.shutDownHub();
            Config.setConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY, runLocally);
            assertFalse(getHubStatus(), "The Hub should have been shutDown");
        }
    }

    public JsonObject getNodeStatus() throws MalformedURLException, IOException {
        JsonObject nodeStatus = null;
        String port = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String url = "http://127.0.0.1:" + port + "/grid/api/proxy?id=http://127.0.0.1:"
                + LocalNode.getInstance().getPort();
        URLConnection connection = null;
        InputStream isr = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(isr));
        try {
            connection = new URL(url).openConnection();
            isr = connection.getInputStream();
            StringBuffer actualResponse = new StringBuffer();
            String eachLine = null;
            while ((eachLine = br.readLine()) != null) {
                actualResponse.append(eachLine);
            }
            if (actualResponse != null && actualResponse.length() > 0) {
                nodeStatus = new JsonParser().parse(actualResponse.toString()).getAsJsonObject();
            }
        } finally {
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(br);
        }
        return nodeStatus;

    }

    public boolean getHubStatus() throws MalformedURLException, IOException {
        boolean hubStatus = false;
        String port = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String url = "http://127.0.0.1:" + port + "/grid/api/hub";
        URLConnection hubConnection = new URL(url).openConnection();
        InputStream isr = hubConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(isr));
        try {
            StringBuffer information = new StringBuffer();
            String eachLine = null;
            while ((eachLine = br.readLine()) != null) {
                information.append(eachLine);
            }
            JsonObject fullResponse = new JsonParser().parse(information.toString()).getAsJsonObject();
            if (fullResponse != null) {
                hubStatus = fullResponse.get("success").getAsBoolean();
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            hubStatus = false;
        }
        finally {
            isr.close();
            br.close();
        }

        return hubStatus;
    }
}
