/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 eBay Software Foundation                                                                   |
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.openqa.grid.common.exception.GridException;
import org.openqa.selenium.net.NetworkUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.grid.ThreadedLauncher;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local Hub.
 * 
 */
class LocalHub implements LocalServerComponent {
    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();
    private static volatile LocalHub instance;
    private boolean isRunning = false;
    private ThreadedLauncher launcher;
    private ExecutorService executor;
    private String host;

    static synchronized LocalHub getInstance() {
        if (instance == null) {
            instance = new LocalHub();

            instance.host = new NetworkUtils().getIpOfLoopBackIp4();

            instance.launcher = new ThreadedLauncher(new String[] {
                    "-role", "hub",
                    "-port", Config.getConfigProperty(ConfigProperty.SELENIUM_PORT),
                    "-host", instance.host });
        }
        return instance;
    }

    public synchronized void shutdown() {
        if (!getInstance().isRunning) {
            return;
        }

        if (getInstance().executor != null) {
            try {
                getInstance().launcher.shutdown();
                getInstance().executor.shutdownNow();
                while (!getInstance().executor.isTerminated() || isHubUp()) {
                    getInstance().executor.awaitTermination(30, TimeUnit.SECONDS);
                }
                getInstance().isRunning = false;
                LOGGER.info("Local hub has been stopped");
            } catch (Exception e) { // NOSONAR
                String errorMsg = "An error occurred while attempting to shut down the local Hub.";
                LOGGER.log(Level.SEVERE, errorMsg, e);
            }
        }
    }

    public synchronized void boot(AbstractTestSession testSession) {
        if (getInstance().isRunning) {
            return;
        }

        getInstance().executor = Executors.newSingleThreadExecutor();
        Runnable worker = getInstance().launcher;
        try {
            getInstance().executor.execute(worker);
            waitForHubToComeUp();
            getInstance().isRunning = true;
            LOGGER.info("Local Hub spawned");
        } catch (IOException | IllegalStateException e) {
            throw new GridException("Failed to start a local Hub", e);
        }
    }

    private void waitForHubToComeUp() throws IOException {
        String errorMsg = "Unable to contact Hub after 60 seconds.";

        // wait for it to start, max 60 seconds
        int attempts = 0;
        while (attempts < 60) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            if (isHubUp()) {
                return;
            }
            attempts += 1;
        }
        throw new IllegalStateException(errorMsg);
    }

    boolean isHubUp() throws IOException {
        boolean hubStatus = false;
        String port = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String url = "http://" + getInstance().host + ":" + port + "/grid/api/hub";
        URLConnection hubConnection = new URL(url).openConnection();

        InputStream isr = null;
        BufferedReader br = null;
        try {
            isr = hubConnection.getInputStream();
            br = new BufferedReader(new InputStreamReader(isr));
            StringBuffer information = new StringBuffer();
            String eachLine;
            while ((eachLine = br.readLine()) != null) {
                information.append(eachLine);
            }
            JsonObject fullResponse = new JsonParser().parse(information.toString()).getAsJsonObject();
            if (fullResponse != null) {
                hubStatus = fullResponse.get("success").getAsBoolean();
            }
        } catch (ConnectException e) {
            hubStatus = false;
        } finally {
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(br);
        }

        return hubStatus;
    }

}
