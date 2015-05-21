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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpRequest;
import org.openqa.selenium.net.NetworkUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A base class that for starting Selenium nodes. It provides the functionality to wait for
 * a maximum of 60 seconds for the node to come up. After that it throws an IllegalStateException.
 * 
 */
class BaseNode {
    private SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * Checks if the node has come up every 10 seconds. Waits for the node to come up for a maximum of 60 seconds.
     * Throws an {@link IllegalStateException} with the exception message if the node can not be contacted
     * 
     * @param port
     *            the port the node should be running on
     * @param exceptionMsg
     *            the exception message to include, if node can not be reached
     */
    public void waitForNodeToComeUp(int port, String exceptionMsg) {
        logger.entering(port);
        for (int i = 0; i < 6; i++) {
            try {
                // Sleep for 10 seconds.
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            if (wasNodeSpawned(port)) {
                logger.exiting();
                return;
            }
        }
        throw new IllegalStateException(exceptionMsg);
    }

    private boolean wasNodeSpawned(int port) {
        logger.entering(port);
        String endPoint = String.format("http://%s:%d/wd/hub/status", 
                new NetworkUtils().getIpOfLoopBackIp4(), port);

        CloseableHttpClient client = HttpClientBuilder.create().build();
        try {
            URL url = new URL(endPoint);
            URL api = new URL("http://" + url.getHost() + ":" + url.getPort() + "/wd/hub/status");
            HttpHost host = new HttpHost(api.getHost(), api.getPort());

            BasicHttpRequest r = new BasicHttpRequest("GET", api.toExternalForm());

            HttpResponse response = client.execute(host, r);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IllegalStateException("hub down or not responding. Reason : "
                        + response.getStatusLine().getReasonPhrase());
            }
            JsonObject o = extractObject(response);
            boolean status = (o.get("status").getAsInt() == 0);
            logger.exiting(status);
            return status;
        } catch (Exception e) { //NOSONAR
            throw new IllegalStateException("Problem querying the status", e);
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    private JsonObject extractObject(HttpResponse resp) throws IOException {
        logger.entering(resp);
        BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
        StringBuilder s = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            s.append(line);
        }
        rd.close();
        logger.exiting(s.toString());
        return new JsonParser().parse(s.toString()).getAsJsonObject();
    }
}
