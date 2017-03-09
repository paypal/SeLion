/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;

import com.paypal.selion.grid.servlets.GridStatistics;
import org.apache.commons.lang.StringUtils;
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
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.SessionTerminationReason;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.SeLionBuildInfo;
import com.paypal.selion.SeLionBuildInfo.SeLionBuildProperty;
import com.paypal.selion.grid.servlets.GridAutoUpgradeDelegateServlet;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.node.servlets.LogServlet;
import com.paypal.selion.node.servlets.NodeAutoUpgradeServlet;
import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.pojos.BrowserInformationCache;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.test.utilities.logging.SimpleLogger;
import com.paypal.test.utilities.logging.SimpleLoggerSettings;

/**
 * This is a customized {@link DefaultRemoteProxy} for SeLion. <br>
 * <br>
 * This proxy, when utilized can: <br>
 * <ul>
 * <li>Count unique test sessions. After "n" test sessions, the proxy disconnects from the grid and issues a requests to
 * {@link NodeForceRestartServlet}. The number of unique sessions is controlled via the json config file
 * <code>SeLionConfig.json</code> with the setting <code>uniqueSessionCount</code>.<br>
 * <br>
 * For example:
 *
 * <pre>
 *  "uniqueSessionCount": 25
 * </pre>
 *
 * Here the value 25 indicates that the proxy will stop accepting new connections after 25 unique sessions and trigger a
 * graceful shutdown (see below). A value of <=0 indicates that this feature is disabled.</li>
 *
 * <li>Request remote proxy to shutdown either forcefully or gracefully -- issues a request to
 * {@link NodeForceRestartServlet}. In the case of a graceful restart, the proxy respects a wait timeout for any
 * sessions in progress to complete. This is controlled via the json config file <code>SeLionConfig.json</code> with the
 * setting <code>nodeRecycleThreadWaitTimeout</code>.<br>
 * <br>
 * For example:
 *
 * <pre>
 *  "nodeRecycleThreadWaitTimeout": 300
 * </pre>
 *
 * Here the value 300 is in seconds -- the proxy will wait this amount of time before forcing a shutdown action. A value
 * of 0 indicates that the proxy will wait indefinitely.</li>
 *
 * <li>Request remote proxy to auto upgrade -- issues a request to {@link NodeAutoUpgradeServlet}.</li>
 *
 * <li>Determine whether the remote proxy supports {@link NodeAutoUpgradeServlet}, {@link NodeForceRestartServlet}, and
 * {@link LogServlet}</li>
 * </ul>
 */
public class SeLionRemoteProxy extends DefaultRemoteProxy {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SeLionRemoteProxy.class);
    private static final int DEFAULT_MAX_SESSIONS_ALLOWED = 50;
    private static final int CONNECTION_TIMEOUT = 30000;

    private volatile boolean scheduledShutdown;
    private volatile int totalSessionsCompleted, totalSessionsStarted;

    private final boolean canForceShutdown, canAutoUpgrade, canViewLogs;
    private final long proxyStartMillis;
    private final SimpleLogger proxyLogger;
    private final String machine;
    private final NodeRecycleThread nodeRecycleThread;
    private final Lock accessLock;

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

        proxyStartMillis = System.currentTimeMillis();
        scheduledShutdown = false;
        totalSessionsCompleted = 0;
        totalSessionsStarted = 0;
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
        loggerSettings.setIdentifier("SeLion-Grid-" + SeLionBuildInfo.getBuildValue(SeLionBuildProperty.SELION_VERSION));
        loggerSettings.setMaxFileCount(1);
        loggerSettings.setMaxFileSize(5);
        proxyLogger = SimpleLogger.getLogger(loggerSettings);

        // Log initialization info
        final int port = getRemoteHost().getPort();

        StringBuffer info = new StringBuffer();
        info.append("New proxy instantiated for the node ").append(machine).append(":").append(port);
        proxyLogger.info(info.toString());
        info = new StringBuffer();
        if (isEnabledMaxUniqueSessions()) {
            info.append("SeLionRemoteProxy will attempt to recycle the node ");
            info.append(machine).append(":").append(port).append(" after ").append(getMaxSessionsAllowed())
                .append(" unique sessions");
        } else {
            info.append("SeLionRemoteProxy will not attempt to recycle the node ");
            info.append(machine).append(":").append(port).append(" based on unique session counting.");
        }
        proxyLogger.info(info.toString());

        // Enable the cache to store the browser information only when the
        // "com.paypal.selion.grid.servlets.GridStatistics" is enabled - results in
        // better memory management if the servlet is not loaded
        if (isSupportedOnHub(GridStatistics.class)) {
            updateBrowserCache(request);
        }

        // detect presence of SeLion servlet capabilities on proxy
        canForceShutdown = isSupportedOnNode(NodeForceRestartServlet.class);
        canAutoUpgrade = isSupportedOnNode(NodeAutoUpgradeServlet.class);
        canViewLogs = isSupportedOnNode(LogServlet.class);
    }

    /**
     * Determine if the hub supports the servlet in question by looking at the registry configuration.
     * @param servlet
     *            the {@link HttpServlet} to ping
     * @return <code>true</code> or <code>false</code>
     */
    private boolean isSupportedOnHub(Class<? extends HttpServlet> servlet) {
        LOGGER.entering();
        final boolean response = getRegistry().getConfiguration().servlets.contains(servlet.getCanonicalName());
        LOGGER.exiting(response);
        return response;
    }

    /**
     * Determine if the remote proxy supports the servlet in question by sending a http request to the remote. The
     * proxy configuration could also be used to make a similar decision. This approach allows the remote to use a
     * servlet which implements the same functionality as the `servlet` expected but does not necessarily reside in the
     * same namespace. This method expects the `servlet` to return HTTP 200 OK as an indication that the remote proxy
     * supports the `servlet` in question.
     *
     * @param servlet
     *            the {@link HttpServlet} to ping
     * @return <code>true</code> or <code>false</code>
     */
    private boolean isSupportedOnNode(Class<? extends HttpServlet> servlet) {
        LOGGER.entering();

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(CONNECTION_TIMEOUT).build();

        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        String url = String.format("http://%s:%d/extra/%s", machine, getRemoteHost().getPort(),
                servlet.getSimpleName());

        try {
            HttpGet get = new HttpGet(url);
            final HttpResponse getResponse = client.execute(get);

            if (getResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                proxyLogger.warning("Node " + getId() + " does not have or support " + servlet.getSimpleName());
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
        LOGGER.exiting(true);
        return true;
    }

    private HttpResponse sendToNodeServlet(Class<? extends HttpServlet> servlet, List<NameValuePair> nvps) {
        LOGGER.entering();

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(CONNECTION_TIMEOUT).build();

        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
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
        if (!supportsAutoUpgrade()) {
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

    /**
     * @return whether the proxy has reached the max unique sessions
     */
    private boolean isMaxUniqueSessionsReached() {
        if (!isEnabledMaxUniqueSessions()) {
            return false;
        }
        return totalSessionsStarted >= getMaxSessionsAllowed();
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        LOGGER.entering();

        // verification should be before lock to avoid unnecessarily acquiring lock
        if (isMaxUniqueSessionsReached() || scheduledShutdown) {
            LOGGER.exiting(null);
            return logSessionInfo();
        }

        try {
            accessLock.lock();

            // As per double-checked locking pattern need to have check once again
            // to avoid spawning additional session then maxSessionAllowed
            if (isMaxUniqueSessionsReached() || scheduledShutdown) {
                LOGGER.exiting(null);
                return logSessionInfo();
            }

            TestSession session = super.getNewSession(requestedCapability);
            if (session != null) {
                // count ONLY if the session was a valid one
                totalSessionsStarted++;
                if (isMaxUniqueSessionsReached()) {
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
        proxyLogger.fine("Was max sessions reached? " + (isMaxUniqueSessionsReached()) + " on node " + getId());
        proxyLogger.fine("Was this a scheduled shutdown? " + (scheduledShutdown) + " on node " + getId());
        return null;
    }

    private void startNodeRecycleThread() {
        if (!getNodeRecycleThread().isAlive()) {
            getNodeRecycleThread().start();
        }
    }

    private void stopNodeRecycleThread() {
        if (getNodeRecycleThread().isAlive()) {
            try {
                getNodeRecycleThread().shutdown();
                getNodeRecycleThread().join(2000); // Wait no longer than 2x the recycle thread's loop
            } catch (InterruptedException e) { // NOSONAR
                // ignore
            }
        }
    }

    private void updateBrowserCache(RegistrationRequest request) throws MalformedURLException {
        // Update the browser information cache. Used by GridStatics servlet
        for (DesiredCapabilities desiredCapabilities : request.getConfiguration().capabilities) {
            Map<String, ?> capabilitiesMap = desiredCapabilities.asMap();
            String browserName = capabilitiesMap.get(CapabilityType.BROWSER_NAME).toString();
            String maxInstancesAsString = capabilitiesMap.get("maxInstances").toString();
            if (StringUtils.isNotBlank(browserName) && StringUtils.isNotBlank(maxInstancesAsString)) {
                int maxInstances = Integer.valueOf(maxInstancesAsString);
                BrowserInformationCache cache = BrowserInformationCache.getInstance();
                cache.updateBrowserInfo(getRemoteHost(), browserName, maxInstances);
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
        scheduledShutdown = true;
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
        if (!canForceShutdown) {
            // allow this proxy to keep going
            disableMaxSessions();
            scheduledShutdown = false;
            LOGGER.exiting();
            return;
        }

        // clean up the test slots
        for (TestSlot slot : getTestSlots()) {
            if (slot.getSession() != null) {
                totalSessionsCompleted++;
                proxyLogger.info("Timing out session #" + totalSessionsCompleted + " (" + slot.getSession().toString()
                        + ")");
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
            // stop the polling thread, mark this proxy as down, force this proxy to re-register
            teardown("it failed to shutdown and must re-register");
            LOGGER.exiting();
            return;
        }

        final int responseStatusCode = response.getStatusLine().getStatusCode();
        if (responseStatusCode != HttpStatus.SC_OK) {
            proxyLogger.warning("Node " + getId() + " did not shutdown and returned HTTP " + responseStatusCode);
            // stop the polling thread, mark this proxy as down, force this proxy to re-register
            teardown("it failed to shutdown and must re-register");
            LOGGER.exiting();
            return;
        }

        // stop the polling thread, mark this proxy as down
        teardown("it is shutdown");

        proxyLogger.info("Node " + getId() + " shutdown successfully.");

        LOGGER.exiting();
    }

    private void teardown(String reason) {
        addNewEvent(new RemoteUnregisterException(String.format("Unregistering node %s because %s.",
                getId(), reason)));
        teardown();
    }

    /**
     * Thread will recycle the node when all active sessions are completed
     */
    class NodeRecycleThread extends Thread {
        private static final int DEFAULT_TIMEOUT = 0; // Waits forever
        private volatile boolean running;
        private final String nodeId;

        NodeRecycleThread(String nodeId) {
            super();
            running = false;
            this.nodeId = nodeId;
        }

        @Override
        public void run() {
            LOGGER.entering();
            running = true;
            int timeout = getThreadWaitTimeout();
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

        int getThreadWaitTimeout() {
            final String key = "nodeRecycleThreadWaitTimeout";
            return config.custom.containsKey(key) ? Integer.parseInt(config.custom.get(key)) : DEFAULT_TIMEOUT;
        }

        private boolean keepLooping(int expired, int timeout) {
            return (getTotalUsed() > 0) && running && ((expired < timeout) || (timeout == 0));
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

    /**
     * @return the {@link NodeRecycleThread} associated with this proxy
     */
    private NodeRecycleThread getNodeRecycleThread() {
        return nodeRecycleThread;
    }

    /**
     * disables the max session count for this node.
     */
    private void disableMaxSessions() {
        proxyLogger.warning("Disabling max unique sessions for Node " + getId());
        config.custom.put("uniqueSessionCount", "-1");
    }

    /**
     * @return an integer value which represents the number of unique sessions this proxy allows for before
     *         automatically spinning up a {@link NodeRecycleThread}
     */
    private int getMaxSessionsAllowed() {
        final String key = "uniqueSessionCount";
        return config.custom.containsKey(key) ? Integer.parseInt(config.custom.get(key)) : DEFAULT_MAX_SESSIONS_ALLOWED;
    }

    /**
     * @return total uptime since proxy came online in minutes
     */
    public long getUptimeInMinutes() {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - proxyStartMillis);
    }

    /**
     * @return total number of sessions completed since proxy came online
     */
    public int getTotalSessionsComplete() {
        return totalSessionsCompleted;
    }

    /**
     * @return total number of sessions started since proxy came online
     */
    public int getTotalSessionsStarted() {
        return totalSessionsStarted;
    }

    /**
     * @return <code>true</code> or <code>false</code>, whether the proxy is scheduled for recycle
     */
    public boolean isScheduledForRecycle() {
        return getNodeRecycleThread().isAlive();
    }

    /**
     * @return whether the proxy supports/has running {@link NodeForceRestartServlet}
     */
    public boolean supportsForceShutdown() {
        return canForceShutdown;
    }

    /**
     * @return whether the proxy supports/has running {@link NodeAutoUpgradeServlet}
     */
    public boolean supportsAutoUpgrade() {
        return canAutoUpgrade;
    }

    /**
     * @return whether the proxy supports/has running {@link LogServlet}
     */
    public boolean supportsViewLogs() {
        return canViewLogs;
    }

    /**
     * @return whether the proxy is enabled to limit the number of unique sessions before triggering a graceful shutdown
     */
    private boolean isEnabledMaxUniqueSessions() {
        return (getMaxSessionsAllowed() > 0);
    }

}
