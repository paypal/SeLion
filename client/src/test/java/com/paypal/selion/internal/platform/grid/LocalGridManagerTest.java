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

import static org.testng.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;

public class LocalGridManagerTest {

    @Test(groups = { "local-grid-tests" }, singleThreaded = true)
    public void testlocalGridManagerStartHub() throws MalformedURLException, IOException {
        String runLocally = Config.getConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY);
        Config.setConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY, "true");

        String msg = "proxy found";
        try {
            WebTestSession testSession = new WebTestSession();
            LocalGridManager.spawnLocalHub(testSession);
            assertTrue(LocalHub.getSingleton().getLauncher().isRunning(), "The Hub should have started locally");
            assertTrue(LocalNode.getSingleton().getLauncher().isRunning(), "A Node should have started locally");

            JsonObject nodeStatus = getNodeStatus();
            assertNotNull(nodeStatus, "The node status should not have been null");
            // assertTrue(nodeStatus.get("success").getAsBoolean(),
            // "The node should have started properly and hooked itself to the local Grid.");
            assertTrue(nodeStatus.get("msg").getAsString().contains(msg), "The node should have been found");
        } finally {
            LocalGridManager.shutDownHub();
            Config.setConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY, runLocally);
            assertFalse(LocalHub.getSingleton().getLauncher().isRunning(), "The Hub should have been shutDown");
        }
    }

    private JsonObject getNodeStatus() throws MalformedURLException, IOException {
        String url = String.format("http://%s:%d/grid/api/proxy?id=http://%s:%d", LocalHub.getSingleton().getHost(),
                LocalHub.getSingleton().getPort(), LocalNode.getSingleton().getHost(), 
                LocalNode.getSingleton().getPort());

        StringBuffer actualResponse = new StringBuffer();
        JsonObject nodeStatus = null;
        URLConnection connection = null;
        InputStream isr = null;
        BufferedReader br = null;
        try {
            connection = new URL(url).openConnection();
            isr = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(isr));
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
}
