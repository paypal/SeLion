/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import com.paypal.selion.SeLionBuildInfo;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.SeLionBuildInfo.SeLionBuildProperty;
import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.grid.servlets.GridAutoUpgradeDelegateServlet;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.node.servlets.NodeAutoUpgradeServlet;
import com.paypal.selion.utils.ConfigParser;
import com.paypal.selion.utils.ConfigParser.ConfigParserException;
import com.paypal.test.utilities.logging.SimpleLogger;
import com.paypal.test.utilities.logging.SimpleLoggerSettings;

/**
 * This is a customized {@link DefaultRemoteProxy} for SeLion. This proxy when injected into the Grid, starts counting
 * unique test sessions. After "n" test sessions, the proxy unhooks the node gracefully from the grid and self
 * terminates gracefully. The number of unique sessions is controlled via a json config file : "SeLionConfig.json". A
 * typical entry in this file looks like:
 * 
 * <pre>
 *  "uniqueSessionCount": 25
 * </pre>
 * 
 * Here UniqueSessionCount represents the max. number of tests that the node will run before recycling itself.
 */
public class SeLionRemoteProxy extends DefaultRemoteProxy {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SeLionRemoteProxy.class);
    private SimpleLogger proxyLogger;
    private static final int MAX_SESSION_ALLOWED = 50;

    private int maxSessionsAllowed, totalSessionsCompleted = 0, totalSessionsStarted = 0;
    private boolean forceShutDown = false;
    private String machine;
    private NodeRecycleThread nodeRecycleThread = new NodeRecycleThread(getId());

    private int getUniqueSessionCount() {
        try {
            return ConfigParser.parse().getInt("uniqueSessionCount");
        } catch (ConfigParserException e) {// NOSONAR
            // Purposely gobbling the exception here and NOT doing anything with it.
            // We cannot afford to throw exceptions from within a Proxy
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        // if we are here, then it means there was a problem loading the
        // session count value from the configuration json file.
        // so lets return back a default session count.
        return MAX_SESSION_ALLOWED;

    }

    /**
     * @param request
     *            a {@link RegistrationRequest} request which represents the basic information that is to be consumed by
     *            the grid when it is registering a new node.
     * @param registry
     *            a {@link Registry} object that represent's the Grid's registry.
     * @throws IOException
     */
    public SeLionRemoteProxy(RegistrationRequest request, Registry registry) throws IOException {
        super(request, registry);
        StringBuffer info = new StringBuffer();
        maxSessionsAllowed = getUniqueSessionCount();
        machine = getRemoteHost().getHost();

        SimpleLoggerSettings loggerSettings = new SimpleLoggerSettings();
        loggerSettings.setUserLogFileName(machine + ".log");
        loggerSettings.setLogsDir(SeLionGridConstants.LOGS_DIR);
        loggerSettings.setDevLevel(Level.OFF);
        loggerSettings.setLoggerName(machine);
        loggerSettings.setClassName(SeLionRemoteProxy.class.getSimpleName());
        loggerSettings.setIdentifier(SeLionBuildInfo.getBuildValue(SeLionBuildProperty.SELION_VERSION));
        loggerSettings.setMaxFileCount(1);
        loggerSettings.setMaxFileSize(5);
        proxyLogger = SimpleLogger.getLogger(loggerSettings);

        info.append("New proxy instantiated for the machine ").append(machine);
        proxyLogger.info(info.toString());
        info = new StringBuffer();
        info.append("SeLionRemoteProxy will attempt to recycle the node [");
        info.append(machine).append("] after ").append(maxSessionsAllowed).append(" unique sessions");
        proxyLogger.info(info.toString());
    }

    /**
     * Upgrades the node by calling {@link NodeAutoUpgradeServlet} and then {@link #requestNodeShutdown}
     * 
     * @param downloadJSON
     *            the download.json to install on node
     * @return <code>true</code> on success. <code>false</code> when an error occured.
     */
    public boolean upgradeNode(String downloadJSON) {
        final int TIME_OUT = 30 * 1000;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build();

        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        String url = String.format("http://%s:%d/extra/%s", machine, this.getRemoteHost().getPort(),
                NodeAutoUpgradeServlet.class.getSimpleName());
        HttpPost post = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        NameValuePair jsonNVP = new BasicNameValuePair(GridAutoUpgradeDelegateServlet.PARAM_JSON, downloadJSON);
        nvps.add(jsonNVP);

        try {
            post.setEntity(new UrlEncodedFormEntity(nvps));
            client.execute(post);
        } catch (ClientProtocolException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        requestNodeShutdown();
        return true;
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        LOGGER.entering();
        TestSession session;
        synchronized (this) {
            if (totalSessionsStarted >= maxSessionsAllowed || forceShutDown) {
                proxyLogger.fine("Was max sessions reached? " + (totalSessionsStarted >= maxSessionsAllowed)
                        + " on node " + getId());
                proxyLogger.fine("Was this a forcible shutdown? " + (forceShutDown) + " on node " + getId());
                LOGGER.exiting(null);
                return null;
            }
            session = super.getNewSession(requestedCapability);
            if (session != null) {
                // count ONLY if the session was a valid one
                totalSessionsStarted++;
                if (totalSessionsStarted >= maxSessionsAllowed) {
                    startNodeRecycleThread();
                }
                proxyLogger.fine("Beginning session #" + totalSessionsStarted + " (" + session.toString() + ")");
            }
            LOGGER.exiting((session != null) ? session.toString() : null);
            return session;
        }
    }

    private void startNodeRecycleThread() {
        if (!nodeRecycleThread.isAlive()) {
            nodeRecycleThread.start();
        }
    }

    private void stopNodeRecycleThread() {
        if (nodeRecycleThread.isAlive()) {
            try {
                nodeRecycleThread.shutdown();
                nodeRecycleThread.join(2000); // Wait no longer than 2x the recycle thread's loop
            } catch (InterruptedException e) { // NOSONAR
                // ignore
            }
        }
    }

    @Override
    public void afterSession(TestSession session) {
        LOGGER.entering();
        synchronized (this) {
            totalSessionsCompleted++;
            if (totalSessionsCompleted <= maxSessionsAllowed) {
                proxyLogger.fine("Completed session #" + totalSessionsCompleted + " (" + session.toString() + ")");
            }
            proxyLogger.fine("Total number of slots used: " + getTotalUsed() + " on node: " + getId());
        }
        LOGGER.exiting();
    }

    /**
     * Gracefully shuts the node down by;<br>
     * <br>
     * 1. Stops accepting new sessions<br>
     * 2. Waits for sessions to complete<br>
     * 3. Calls {@link #forceNodeShutdown}<br>
     */
    public synchronized void requestNodeShutdown() {
        LOGGER.entering();
        forceShutDown = true;
        startNodeRecycleThread();
        LOGGER.exiting();
    }

    /**
     * Forcefully shuts the node down by calling {@link NodeForceRestartServlet}
     */
    public synchronized void forceNodeShutdown() {
        LOGGER.entering();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String url = String.format("http://%s:%d/extra/%s", machine, this.getRemoteHost().getPort(),
                NodeForceRestartServlet.class.getSimpleName());
        HttpPost post = new HttpPost(url);
        int responseStatusCode = HttpStatus.SC_NOT_FOUND;
        try {
            HttpResponse response = client.execute(post);
            responseStatusCode = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (responseStatusCode == HttpStatus.SC_OK) {
                proxyLogger.info("Node " + machine + " shutdown successfully.");
            } else {
                proxyLogger.info("Node " + machine + " did not shutdown. Return code was " + responseStatusCode);
            }

            try {
                client.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        stopNodeRecycleThread();
        // remove the node from the hub registry, if it reported 200 OK for NodeForceRestartServlet request
        if (responseStatusCode == HttpStatus.SC_OK) {
            getRegistry().removeIfPresent(this);
        }
        LOGGER.exiting();
    }

    /**
     * Thread will recycle the node when all active sessions are completed
     */
    class NodeRecycleThread extends Thread {
        private volatile boolean running;
        private String nodeId;

        NodeRecycleThread(String nodeId) {
            running = false;
            this.nodeId = nodeId;
        }

        @Override
        public void run() {
            LOGGER.entering();
            running = true;
            int timeout = 0; // Default. Wait forever.
            int expired = 0;

            try {
                timeout = ConfigParser.parse().getInt("nodeRecycleThreadWaitTimeout");
            } catch (ConfigParserException e) { // NOSONAR
                // ignore we have a default already
            }

            proxyLogger.fine("Started NodeRecycleThread with " + ((timeout == 0) ? "no" : "a " + timeout + " second")
                    + " timeout for node " + nodeId);
            while (keepLooping(expired, timeout)) {
                try {
                    sleep(1000);
                    expired += 1;
                } catch (InterruptedException e) {
                    if (running) {
                        //SEVERE, only if shutdown() was not called
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                    running = false;
                    proxyLogger.warning("NodeRecycleThread was interrupted.");
                    LOGGER.exiting();
                    return;
                }
            }

            if (wasExpired(expired, timeout)) {
                proxyLogger.info("Timeout occurred while waiting for sessions to complete. Shutting down the node.");
            } else {
                proxyLogger.info("All sessions are complete. Shutting down the node.");
            }
            forceNodeShutdown();
            LOGGER.exiting();
        }

        private boolean keepLooping(int expired, int timeout) {
            return (getTotalUsed() > 0) && (running) && ((expired < timeout) || (timeout == 0));
        }

        private boolean wasExpired(int expired, int timeout) {
            return (expired >= timeout) && (timeout != 0);
        }

        public void shutdown() {
            LOGGER.entering();
            running = false;
            proxyLogger.fine("Shutting down NodeRecycleThread for node " + nodeId);
            interrupt();
            LOGGER.exiting();
        }
    }
}
