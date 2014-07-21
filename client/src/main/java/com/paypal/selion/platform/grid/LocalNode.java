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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONObject;
import org.openqa.grid.common.exception.GridException;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local node.
 * 
 */
class LocalNode implements StandaloneServerCapabilities {
    private boolean isRunning = false, isRegistered = false;
    private final SimpleLogger logger = SeLionLogger.getLogger();
    private SeleniumServer node;

    @Override
    public void shutdown() {
        if (node != null) {
            try {
                node.stop();
                logger.log(Level.INFO, "Local node has been stopped");
            } catch (Exception e) {
                String errorMsg = "An error occured while attempting to shut down the local Node. Root cause: ";
                logger.log(Level.SEVERE, errorMsg, e);
            }
        }

    }

    @Override
    public void startUp(WebDriverPlatform platform) {
        logger.entering(platform);
        if (isRunning) {
            logger.exiting();
            return;
        }
        if (isRegistered) {
            logger.exiting();
            return;
        }
        // TODO: Figure out if this is still a valid statement
        // We shouldn't spawn a local node when user wants an ios-driver node or android node
        if (((platform == WebDriverPlatform.IOS) || (platform == WebDriverPlatform.ANDROID)) || (platform != WebDriverPlatform.WEB)) {
            logger.exiting();
            return;
        }
        LocalGridConfigFileParser parser = new LocalGridConfigFileParser();
        int port = parser.getPort();
        JSONObject request = parser.getRequest();

        RemoteControlConfiguration c = new RemoteControlConfiguration();
        c.setPort(port);

        try {
            node = new SeleniumServer(c);
            node.boot();
            isRunning = true;
            logger.log(Level.INFO, "Local node spawned");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new GridException("Failed to start a local Grid", e);
        }

        if (!isRegistered && isRunning) {
            String host = "localhost";
            String hubPort = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
            String registrationUrl = String.format("http://%s:%s/grid/register", host, hubPort);
            URL registration;
            try {
                registration = new URL(registrationUrl);
                registerNodeToHub(registration, request.toString());
                isRegistered = true;
                logger.log(Level.INFO, "Attached node to local hub " + registrationUrl);
            } catch (MalformedURLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                throw new GridException("Failed to start a local node", e);
            }
        }
    }

    /**
     * This method helps with creating a node and associating it with the already spawned Hub instance
     * 
     * @param registrationURL
     *            - The registration URL of the hub
     * @param json
     *            - A string that represents the capabilities and configurations in the JSON text file
     */
    private void registerNodeToHub(URL registrationURL, String json) {
        logger.entering(new Object[] { registrationURL, json });
        BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST",
                registrationURL.toExternalForm());
        String errorMsg = "Error sending the node registration request. ";

        try {
            r.setEntity(new StringEntity(json));
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, errorMsg, e);
            throw new GridException(errorMsg, e);
        }

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpHost host = new HttpHost(registrationURL.getHost(), registrationURL.getPort());

        HttpResponse response = null;
        try {
            response = client.execute(host, r);
        } catch (IOException e) {
            logger.log(Level.SEVERE, errorMsg, e);
            throw new GridException(errorMsg, e);
        } finally {
            IOUtils.closeQuietly(client);
        }

        if (response.getStatusLine().getStatusCode() != 200) {
            errorMsg += "Received status code " + response.getStatusLine().getStatusCode();
            logger.log(Level.SEVERE, errorMsg);
            throw new GridException(errorMsg);
        }
        logger.exiting();
    }

}
