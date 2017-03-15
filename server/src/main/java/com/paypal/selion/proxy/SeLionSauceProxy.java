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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.selion.grid.servlets.SauceServlet;
import com.paypal.selion.utils.SauceConfigReader;
import com.paypal.selion.utils.SauceLabsRestApi;

import org.apache.commons.lang.StringUtils;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.GridConfigurationException;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.common.exception.RemoteException;
import org.openqa.grid.common.exception.RemoteNotReachableException;
import org.openqa.grid.internal.BaseRemoteProxy;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.listeners.CommandListener;
import org.openqa.grid.internal.listeners.SelfHealingProxy;
import org.openqa.grid.internal.listeners.TimeoutListener;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.grid.selenium.proxy.WebProxyHtmlRenderer;
import org.openqa.selenium.remote.http.HttpMethod;

import com.paypal.selion.logging.SeLionGridLogger;

/**
 * This customized version of {@link BaseRemoteProxy} basically helps redirect all traffic to the SauceLabs cloud.
 */
public class SeLionSauceProxy extends BaseRemoteProxy implements CommandListener, TimeoutListener, SelfHealingProxy {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SeLionSauceProxy.class);

    /** {@link SauceConfigReader} instruction to use the provided username value also for the parent-tunnel, if omitted */
    public static final String DEFAULT_PARENT_TUNNEL_TO_USERNAME_TOKEN = "$username";
    /** Sauce labs cloud default idle timeout is 90 seconds */
    public static final int DEFAULT_IDLE_TIMEOUT = 90000;
    /** Overrides value in DefaultRemoteProxy */
    public static final int DEFAULT_POLLING_INTERVAL = 60000;
    /** Overrides value in DefaultRemoteProxy */
    public static final int DEFAULT_DOWN_POLLING_LIMIT = 3;

    private final SauceLabsRestApi sauceApi;
    private final String defaultParentTunnel;
    private final String defaultTunnelIdentifier;
    private final Lock accessLock = new ReentrantLock();
    private final SauceConfigReader sauceConfigReader;
    private final HtmlRenderer renderer;
    private Thread pollingThread;

    /*
     * Self Healing. Polling configuration
     */
    private volatile int pollingInterval;
    private volatile int downPollingLimit;

    /*
     * Self Healing part. Polls the remote, and marks it down if it cannot be reached after downPollingLimit is reached.
     */
    private volatile boolean down;
    private volatile boolean poll;

    /**
     * Sauce labs DesiredCapabilities that {@link SeLionSauceProxy} is concerned with
     */
    public static class SauceLabsCapability {
        public static final String SAUCE_USER_NAME_CAPABILITY = "sauceUserName";
        public static final String SAUCE_API_KEY_CAPABILITY = "sauceApiKey";
        public static final String USERNAME_CAPABILITY = "username";
        public static final String ACCESS_KEY_CAPABILITY = "accessKey";
        public static final String PARENT_TUNNEL_CAPABILITY = "parent-tunnel";
        public static final String TUNNEL_IDENTIFIER_CAPABILITY = "tunnelIdentifier";
        public static final String IDLE_TIMEOUT_CAPABILITY = "idleTimeout";
    }

    private static RegistrationRequest fixTimeout(RegistrationRequest request) {
        // make sure there is a timeout set for this proxy
        int timeout = request.getConfiguration().timeout != null ? request.getConfiguration().timeout : DEFAULT_IDLE_TIMEOUT;
        request.getConfiguration().timeout = timeout;
        return request;
    }

    public SeLionSauceProxy(RegistrationRequest request, Registry registry) {
        super(fixTimeout(request), registry);

        down = false;
        poll = true;

        pollingInterval = config.nodePolling != null ? config.nodePolling : DEFAULT_POLLING_INTERVAL;
        downPollingLimit = config.downPollingLimit != null ? config.downPollingLimit : DEFAULT_DOWN_POLLING_LIMIT;
        if ((pollingInterval <= 0) || (downPollingLimit <= 0)) {
            poll = false;
        }

        if (!SauceServlet.PROXY_ID.equals(remoteHost.toExternalForm())) {
            throw new GridException(SeLionSauceProxy.class.getSimpleName() + " can not be used by an external process");
        }

        try {
            sauceConfigReader = SauceConfigReader.getInstance();
            defaultParentTunnel = sauceConfigReader.getDefaultParentTunnel();
            defaultTunnelIdentifier = sauceConfigReader.getDefaultTunnelIdentifier();
            sauceApi = new SauceLabsRestApi();
        } catch (GridConfigurationException e) {
            throw new GridException("Failed to initialize proxy: ", e);
        }

        renderer = new WebProxyHtmlRenderer(this);
    }

    @Override
    public HtmlRenderer getHtmlRender() {
        return renderer;
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapabilities) {
        LOGGER.entering();
        TestSession session = null;

        try {
            // sleep for 2 seconds to mitigate hitting sauce REST api rate limits
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        try {
            accessLock.lock();

            // check for backwards compatible capability names that might come across the wire
            // TODO remove backwards compatible support in a future version.
            processDeprecatedCapabilities(requestedCapabilities);

            // default the credentials, if required
            processCredentials(requestedCapabilities);

            // setup the tunnelIdentifier, if required
            processTunnelIdentifierCapability(requestedCapabilities);

            // setup the parent-tunnel, if required
            processParentTunnelCapability(requestedCapabilities);

            // override any idleTimeout capability that may have been specified. Sauce and this proxy need to have
            // a similar idle timeout value.
            requestedCapabilities.put(SauceLabsCapability.IDLE_TIMEOUT_CAPABILITY, getTimeOut() / 1000);

            final int numberOfTCRunning = sauceApi.getNumberOfTCRunning();

            // if call to geNumberOfTCRunning returns -1, sauce REST API calls are failing.
            if (numberOfTCRunning == -1) {
                // mark the proxy as down and let the polling thread determine when it is back up.
                down = true;
                // if we aren't polling sauce labs, kick the session back to the hub.
                if (poll == false) {
                    LOGGER.exiting(null);
                    return null;
                }
            }

            if (numberOfTCRunning <= getMaxNumberOfConcurrentTestSessions()) {
                session = super.getNewSession(requestedCapabilities);
            }

            LOGGER.exiting((session != null) ? session.toString() : null);
            return session;
        } finally {
            accessLock.unlock();
        }
    }

    private void processDeprecatedCapabilities(Map<String, Object> requestedCapabilities) {
        final String username = (String) requestedCapabilities.get(SauceLabsCapability.SAUCE_USER_NAME_CAPABILITY);
        if (StringUtils.isNotBlank(username)) {
            requestedCapabilities.remove(SauceLabsCapability.SAUCE_USER_NAME_CAPABILITY);
            requestedCapabilities.put(SauceLabsCapability.USERNAME_CAPABILITY, username);
        }
        final String accessKey = (String) requestedCapabilities.get(SauceLabsCapability.SAUCE_API_KEY_CAPABILITY);
        if (StringUtils.isNotBlank(accessKey)) {
            requestedCapabilities.remove(SauceLabsCapability.SAUCE_API_KEY_CAPABILITY);
            requestedCapabilities.put(SauceLabsCapability.ACCESS_KEY_CAPABILITY, accessKey);
        }
    }

    private void processParentTunnelCapability(Map<String, Object> requestedCapabilities) {
        final String tunnelId = (String) requestedCapabilities.get(SauceLabsCapability.PARENT_TUNNEL_CAPABILITY);
        if (StringUtils.isBlank(tunnelId) && StringUtils.isNotBlank(defaultParentTunnel)) {
            // apply the default parent-tunnel according to the configured policy
            final String actualUserName = (String) requestedCapabilities.get(SauceLabsCapability.USERNAME_CAPABILITY);
            requestedCapabilities.put(SauceLabsCapability.PARENT_TUNNEL_CAPABILITY, (defaultParentTunnel
                    .equalsIgnoreCase(DEFAULT_PARENT_TUNNEL_TO_USERNAME_TOKEN)) ? actualUserName : defaultParentTunnel);
        }
    }

    private void processTunnelIdentifierCapability(Map<String, Object> requestedCapabilities) {
        final String tunnelId = (String) requestedCapabilities.get(SauceLabsCapability.TUNNEL_IDENTIFIER_CAPABILITY);
        if (StringUtils.isBlank(tunnelId) && StringUtils.isNotBlank(defaultTunnelIdentifier)) {
            requestedCapabilities.put(SauceLabsCapability.TUNNEL_IDENTIFIER_CAPABILITY, defaultTunnelIdentifier);
        }
    }

    private void processCredentials(Map<String, Object> requestedCapabilities) {
        if (sauceConfigReader.isRequireUserCredentials()) {
            return;
        }

        final String username = (String) requestedCapabilities.get(SauceLabsCapability.USERNAME_CAPABILITY);
        final String accessKey = (String) requestedCapabilities.get(SauceLabsCapability.ACCESS_KEY_CAPABILITY);
        if (StringUtils.isBlank(username)) {
            requestedCapabilities.put(SauceLabsCapability.USERNAME_CAPABILITY, sauceConfigReader.getUserName());
        }
        if (StringUtils.isBlank(accessKey)) {
            requestedCapabilities.put(SauceLabsCapability.ACCESS_KEY_CAPABILITY, sauceConfigReader.getApiKey());
        }
    }

    private boolean isMissingRequiredCapabilities(Map<String, Object> requestedCapabilities) {
        return requestedCapabilities.get(SauceLabsCapability.USERNAME_CAPABILITY) == null
                || requestedCapabilities.get(SauceLabsCapability.ACCESS_KEY_CAPABILITY) == null;
    }

    public void beforeCommand(TestSession session, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> requestedCapabilities = session.getRequestedCapabilities();

        // give up if sauce labs is down ...
        // this should fail faster vs. returning the session to hub by doing this check in #getNewSession
        // (DefaultRemoteProxy's behavior)
        if (down) {
            throw new GridException("Sauce Labs is currently down.");
        }

        if (session.get("authenticated-session") == null) {
            // error the session when there is no sauce user name and/or access key
            if (isMissingRequiredCapabilities(requestedCapabilities)) {
                throw new GridException("Sauce Labs credentials were not specified.");
            }

            final String username = (String) requestedCapabilities.get(SauceLabsCapability.USERNAME_CAPABILITY);
            final String accessKey = (String) requestedCapabilities.get(SauceLabsCapability.ACCESS_KEY_CAPABILITY);
            if (!sauceApi.isAuthenticated(username, accessKey)) {
                // TODO isAuthenticated will return false when Sauce labs is down even if the credentials are valid.
                throw new GridException("Sauce Labs credentials are invalid.");
            }
            // make a note that this session is already authenticated
            session.put("authenticated-session", true);
        }

        session.put("lastCommand", request.getMethod() + " - " + request.getPathInfo() + " executing ...");
    }

    private boolean isWebDriverCommand(HttpServletRequest request, HttpMethod method, String path) {
        return request.getMethod().equals(method.toString()) && request.getPathInfo().equals(path);
    }

    public void beforeRelease(TestSession session) {
        // release the resources remotely if the remote started a browser.
        if (session.getExternalKey() == null) {
            return;
        }
        boolean ok = session.sendDeleteSessionRequest();
        if (!ok) {
            LOGGER.warning("Error releasing the resources on timeout for session " + session);
        }
    }

    public void afterCommand(TestSession session, HttpServletRequest request, HttpServletResponse response) {
        session.put("lastCommand", request.getMethod() + " - " + request.getPathInfo() + " executed.");

        // throw an exception if we got back a HTTP 404
        // ignore if we were trying to DELETE a session
        if ((response.getStatus() == HttpServletResponse.SC_NOT_FOUND)
                && (!isWebDriverCommand(request, HttpMethod.DELETE,
                        String.format("/session/%s", session.getExternalKey())))) {
            throw new GridException("Sauce Labs session no longer exists. It may have timed out.");
        }
    }

    @Override
    public URL getRemoteHost() {
        try {
            return new URL(SauceServlet.PROXY_ID);
        } catch (MalformedURLException e) {
            LOGGER.severe(e.getMessage());
        }
        return remoteHost;
    }

    public boolean isAlive() {
        try {
            getStatus();
            return true;
        } catch (GridException e) {
            LOGGER.fine("Failed to check status of node: " + e.getMessage());
            return false;
        }
    }

    public void addNewEvent(RemoteException event) {
        if (event instanceof RemoteNotReachableException) {
            LOGGER.info(event.getMessage());
            down = true;
        }
    }

    public void onEvent(List<RemoteException> events, RemoteException lastInserted) {
        // not used locally or called by WebDriver but required by the SelfHealingProxy interface
    }

    public void startPolling() {
        // polls but does not disconnect the node, once down polling limit is reached
        pollingThread = new Thread(new Runnable() {
            int failedPollingTries; // 0 is the default
            long downSince; // 0L is the default

            public void run() {
                while (poll) {
                    try {
                        Thread.sleep(pollingInterval);
                        if (!isAlive()) {
                            if (!down) {
                                failedPollingTries++;
                                if (failedPollingTries >= downPollingLimit) {
                                    downSince = System.currentTimeMillis();
                                    addNewEvent(new RemoteNotReachableException(String.format(
                                            "Marking the node %s as down: cannot reach the node for %s tries",
                                            getId(), failedPollingTries)));
                                }
                            } else {
                                long downFor = System.currentTimeMillis() - downSince;
                                addNewEvent(new RemoteNotReachableException(String.format(
                                        "The node %s has been down for %s milliseconds", getId(), downFor)));
                            }
                        } else {
                            down = false;
                            failedPollingTries = 0;
                            downSince = 0;
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        }, "RemoteProxy failure poller thread for " + getId());
        pollingThread.start();
    }

    public void stopPolling() {
        poll = false;
        pollingThread.interrupt();
    }

    @Override
    public void teardown() {
        super.teardown();
        stopPolling();
    }
}
