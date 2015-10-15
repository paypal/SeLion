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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.SessionTerminationReason;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import com.paypal.selion.SeLionBuildInfo;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.SeLionBuildInfo.SeLionBuildProperty;
import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.grid.servlets.GridAutoUpgradeDelegateServlet;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.node.servlets.NodeAutoUpgradeServlet;
import com.paypal.selion.utils.ConfigParser;
import com.paypal.test.utilities.logging.SimpleLogger;
import com.paypal.test.utilities.logging.SimpleLoggerSettings;

import javax.servlet.http.HttpServlet;

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
    private static final int MAX_SESSION_ALLOWED = 50;
    private static final int CONNECTION_TIMEOUT = 30000;

    private volatile boolean forceShutDown;
    private volatile int totalSessionsCompleted, totalSessionsStarted;

    private SimpleLogger proxyLogger;
    private int maxSessionsAllowed;
    private String machine;
    private NodeRecycleThread nodeRecycleThread;
    private Lock accessLock;

    /**
     * @param request
     *            a {@link RegistrationRequest} request which represents the basic information that is to be consumed by
     *            the grid when it is registering a new node.
     * @param registry
     *            a {@link Registry} object that represents the Grid's registry.
     * @throws IOException
     */
    public SeLionRemoteProxy(RegistrationRequest request, Registry registry) throws IOException {
        super(request, registry);

        forceShutDown = false;
        totalSessionsCompleted = 0;
        totalSessionsStarted = 0;
        maxSessionsAllowed = ConfigParser.parse().getInt("uniqueSessionCount", MAX_SESSION_ALLOWED);
        machine = getRemoteHost().getHost();
        nodeRecycleThread = new NodeRecycleThread(getId());
        accessLock = new ReentrantLock();

        // Setup the proxy logger
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

        // Log initialization info
        StringBuffer info = new StringBuffer();
        info.append("New proxy instantiated for the machine ").append(machine);
        proxyLogger.info(info.toString());
        info = new StringBuffer();
        info.append("SeLionRemoteProxy will attempt to recycle the node [");
        info.append(machine).append("] after ").append(maxSessionsAllowed).append(" unique sessions");
        proxyLogger.info(info.toString());
    }

    private boolean isSupportedOnNode(Class<? extends HttpServlet> servlet) {
        LOGGER.entering();

        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(CONNECTION_TIMEOUT).build();

        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        String url = String.format("http://%s:%d/extra/%s", machine, getRemoteHost().getPort(),
                servlet.getSimpleName());

        try {
            HttpGet get = new HttpGet(url);
            final HttpResponse getResponse = client.execute(get);

            if (getResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                proxyLogger.warning("Node " + getId() + " does not have or support "
                        + servlet.getSimpleName());
                LOGGER.exiting(false);
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            LOGGER.exiting(false);
            return false;
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return true;
    }

    private HttpResponse sendToNodeServlet(Class<? extends HttpServlet> servlet, List<NameValuePair> nvps) {
        LOGGER.entering();

        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(CONNECTION_TIMEOUT).build();

        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        String url = String.format("http://%s:%d/extra/%s", machine, this.getRemoteHost().getPort(),
                servlet.getSimpleName());

        HttpResponse postResponse = null;
        try {
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(nvps));
            postResponse = client.execute(post);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        LOGGER.exiting(postResponse);
        return postResponse;
    }

    /**
     * Upgrades the node by calling {@link NodeAutoUpgradeServlet} and then {@link #requestNodeShutdown}
     * 
     * @param downloadJSON
     *            the download.json to install on node
     * @return <code>true</code> on success. <code>false</code> when an error occured.
     */
    public boolean upgradeNode(String downloadJSON) {
        LOGGER.entering(downloadJSON);

        // verify the servlet is supported on the node
        if (!isSupportedOnNode(NodeAutoUpgradeServlet.class)) {
            LOGGER.exiting(false);
            return false;
        }

        // call the NodeAutoUpgradeServlet on the node
        proxyLogger.fine("Upgrading node " + getId());

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(NodeAutoUpgradeServlet.TOKEN_PARAMETER,
                NodeAutoUpgradeServlet.CONFIGURED_TOKEN_VALUE));
        NameValuePair jsonNVP = new BasicNameValuePair(GridAutoUpgradeDelegateServlet.PARAM_JSON, downloadJSON);
        nvps.add(jsonNVP);

        HttpResponse response = sendToNodeServlet(NodeAutoUpgradeServlet.class, nvps);
        if (response == null) {
            proxyLogger.warning("Node " + getId() + " failed to upgrade and returned a null response.");
            LOGGER.exiting(false);
            return false;
        }

        final int responseStatusCode = response.getStatusLine().getStatusCode();
        if (responseStatusCode != HttpStatus.SC_OK) {
            proxyLogger.warning("Node " + getId() + " failed to upgrade and returned HTTP " + responseStatusCode);
            LOGGER.exiting(false);
            return false;
        }

        requestNodeShutdown();
        LOGGER.exiting(true);
        return true;
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        LOGGER.entering();

        // verification should be before lock to avoid unnecessarily acquiring lock
        if (totalSessionsStarted >= maxSessionsAllowed || forceShutDown) {
            LOGGER.exiting(null);
            return logSessionInfo();
        }

        try {
            accessLock.lock();

            // As per double-checked locking pattern need to have check once again
            // to avoid spawning additional session then maxSessionAllowed
            if (totalSessionsStarted >= maxSessionsAllowed || forceShutDown) {
                LOGGER.exiting(null);
                return logSessionInfo();
            }

            TestSession session = super.getNewSession(requestedCapability);
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
        } finally {
            accessLock.unlock();
        }
    }

    private TestSession logSessionInfo() {
        proxyLogger.fine("Was max sessions reached? " + (totalSessionsStarted >= maxSessionsAllowed) + " on node "
                + getId());
        proxyLogger.fine("Was this a forced shutdown? " + (forceShutDown) + " on node " + getId());
        return null;
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
        totalSessionsCompleted++;
        proxyLogger.fine("Completed session #" + totalSessionsCompleted + " (" + session.toString() + ")");
        proxyLogger.fine("Total number of slots used: " + getTotalUsed() + " on node: " + getId());
        LOGGER.exiting();
    }

    /**
     * Gracefully shuts the node down by;<br>
     * <br>
     * 1. Stops accepting new sessions<br>
     * 2. Waits for sessions to complete<br>
     * 3. Calls {@link #forceNodeShutdown}<br>
     */
    public void requestNodeShutdown() {
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

        // stop the node recycle thread
        stopNodeRecycleThread();

        // verify the servlet is supported on the node
        if (!isSupportedOnNode(NodeForceRestartServlet.class)) {
            LOGGER.exiting();
            return;
        }

        // clean up the test slots
        for (TestSlot slot : getTestSlots()) {
            if (slot.getSession() != null) {
                totalSessionsCompleted++;
                proxyLogger.info("Timing out session #" + totalSessionsCompleted + " ("
                        + slot.getSession().toString() + ")");
                getRegistry().forceRelease(slot, SessionTerminationReason.TIMEOUT);
            }
        }

        // call the node servlet
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(NodeForceRestartServlet.TOKEN_PARAMETER,
                NodeForceRestartServlet.CONFIGURED_TOKEN_VALUE));

        HttpResponse response = sendToNodeServlet(NodeForceRestartServlet.class, nvps);
        if (response == null) {
            proxyLogger.warning("Node " + getId() + " failed to shutdown and returned a null response.");
            LOGGER.exiting(false);
            return;
        }

        final int responseStatusCode = response.getStatusLine().getStatusCode();
        if (responseStatusCode != HttpStatus.SC_OK) {
            proxyLogger.info("Node " + getId() + " did not shutdown and returned HTTP " + responseStatusCode);
            LOGGER.exiting(false);
            return;
        }

        proxyLogger.info("Node " + getId() + " shutdown successfully.");
        // remove the node from the hub registry, if it reported 200 OK for NodeForceRestartServlet request
        getRegistry().removeIfPresent(this);

        LOGGER.exiting();
    }

    /**
     * Thread will recycle the node when all active sessions are completed
     */
    class NodeRecycleThread extends Thread {
        private static final int DEFAULT_TIMEOUT = 0; // Waits forever
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
            int timeout = ConfigParser.parse().getInt("nodeRecycleThreadWaitTimeout", DEFAULT_TIMEOUT);
            int expired = 0;

            proxyLogger.fine("Started NodeRecycleThread with " + ((timeout == 0) ? "no" : "a " + timeout + " second")
                    + " timeout for node " + nodeId);
            while (keepLooping(expired, timeout)) {
                try {
                    sleep(1000);
                    expired += 1;
                } catch (InterruptedException e) {
                    if (running) {
                        // SEVERE, only if shutdown() was not called
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
