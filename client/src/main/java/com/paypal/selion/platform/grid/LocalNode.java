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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;

import com.paypal.selion.grid.ThreadedLauncher;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local node.
 * 
 */
class LocalNode extends BaseNode implements LocalServerComponent {
    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();
    private static volatile LocalNode instance;
    private int port;
    private boolean isRunning = false;
    private ThreadedLauncher launcher;
    private ExecutorService executor;
    private String host;

    static synchronized LocalNode getInstance() {
        if (instance == null) {
            instance = new LocalNode();

            instance.host = new NetworkUtils().getIpOfLoopBackIp4();
            instance.port = PortProber.findFreePort();

            instance.launcher = new ThreadedLauncher(new String[] {
                    "-role", "node",
                    "-port", String.valueOf(instance.port),
                    "-proxy", DefaultRemoteProxy.class.getName(),
                    "-host", instance.host,
                    "-hubHost", instance.host });
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
                while (!getInstance().executor.isTerminated()) {
                    getInstance().executor.awaitTermination(30, TimeUnit.SECONDS);
                }
                getInstance().isRunning = false;
                LOGGER.info("Local node has been stopped");
            } catch (Exception e) { //NOSONAR
                String errorMsg = "An error occurred while attempting to shut down the local Node.";
                LOGGER.log(Level.SEVERE, errorMsg, e);
            }
        }

    }

    public synchronized void boot(AbstractTestSession testSession) {
        LOGGER.entering(testSession.getPlatform());
        if (getInstance().isRunning) {
            LOGGER.exiting();
            return;
        }

        if (!(testSession instanceof WebTestSession)) {
            LOGGER.exiting();
            return;
        }

        getInstance().executor = Executors.newSingleThreadExecutor();
        Runnable worker = getInstance().launcher;
        try {
            getInstance().executor.execute(worker);
            waitForNodeToComeUp(getPort(),
                    "Unable to contact Node after 60 seconds.");
            getInstance().isRunning = true;
            LOGGER.info("Local Node spawned");
        } catch (IllegalStateException e) {
            throw new GridException("Failed to start a local Node", e);
        }
    }

    int getPort() {
        return getInstance().port;
    }
}
