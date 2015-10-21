/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion.grid.servlets;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import net.jcip.annotations.Immutable;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSlot;
import org.openqa.selenium.remote.CapabilityType;

import com.paypal.selion.node.servlets.LogServlet;
import com.paypal.selion.proxy.SeLionRemoteProxy;

/**
 * Internal only. Used to create an immutable response object for the {@link ListAllNodes} servlet which gets serialized
 * application/json.
 */
@Immutable
@SuppressWarnings("unused")
final class ProxyInfo {
    /**
     * Information not available
     */
    private static final String NOT_AVAILABLE = "not available";

    /**
     * Proxy is online
     */
    private static final String ONLINE = "online";

    /**
     * Proxy is offline
     */
    private static final String OFFLINE = "offline";

    /**
     * The url to view logs (via LogServlet) on the proxy. Defaults to {@value #NOT_AVAILABLE}
     * 
     * @see SeLionRemoteProxy#supportsViewLogs()
     */
    private String logsLocation = NOT_AVAILABLE;

    /**
     * @see RemoteProxy#isBusy()
     */
    private boolean isBusy;

    /**
     * @see RemoteProxy#getResourceUsageInPercent()
     */
    private float percentResourceUsage;

    /**
     * @see RemoteProxy#getTotalUsed()
     */
    private int totalUsed;

    /**
     * @see SeLionRemoteProxy#isScheduledForRecycle(). Defaults to false
     */
    private boolean isShuttingDown;

    /**
     * @see SeLionRemoteProxy#getTotalSessionsComplete(). Defaults to -1
     */
    private int totalSessionsComplete = -1;

    /**
     * @see SeLionRemoteProxy#getTotalSessionsStarted(). Defaults to -1
     */
    private int totalSessionsStarted = -1;

    /**
     * @see SeLionRemoteProxy#getUptimeInMinutes(). Defaults to -1
     */
    private long uptimeInMinutes = -1;

    /**
     * @see RemoteProxy#getConfig()
     */
    private Map<String, Object> configuration;

    /**
     * Calls {@link RemoteProxy#getStatus()} to determine if proxy is {@value #ONLINE} or {@value #OFFLINE}. Defaults to
     * {@value #NOT_AVAILABLE}
     */
    private String status = NOT_AVAILABLE;

    /**
     * Calls {@link RemoteProxy#getStatus()} to determine proxy version. Defaults to {@value #NOT_AVAILABLE}
     */
    private String version = NOT_AVAILABLE;

    /**
     * Calls {@link RemoteProxy#getStatus()} to determine proxy OS. Defaults to {@value #NOT_AVAILABLE}
     */
    private String os = NOT_AVAILABLE;

    /**
     * proxy usage by slot type
     */
    private Map<String, SlotInfo> slotUsage;

    private final class SlotInfo {
        private int used;
        private int percentUsed;
        private final int maxInstances;

        private SlotInfo() {
            this(1);
        }

        private SlotInfo(int maxInstances) {
            used = 0;
            percentUsed = 0;
            this.maxInstances = maxInstances;
        }

        private void addUsed() {
            used++;
            updateUsage();
        }

        private void updateUsage() {
            percentUsed = 100 * used / maxInstances;
        }
    }

    private ProxyInfo() {
        // defeat instantiation.
    }

    /**
     * Initializes proxy information from the supplied {@link RemoteProxy} object. Queries the proxy for status over
     * HTTP.
     *
     * @param proxy
     *            the {@link RemoteProxy}
     */
    ProxyInfo(RemoteProxy proxy) {
        this(proxy, true);
    }

    /**
     * Initializes proxy information from the supplied {@link RemoteProxy} object
     * 
     * @param proxy
     *            the {@link RemoteProxy}
     * @param queryStatus
     *            whether to query the node status over HTTP via /wd/hub/status
     */
    ProxyInfo(RemoteProxy proxy, boolean queryStatus) {
        // selenium supported features
        isBusy = proxy.isBusy();
        percentResourceUsage = proxy.getResourceUsageInPercent();
        totalUsed = proxy.getTotalUsed();
        configuration = proxy.getConfig();

        determineStatus(proxy, queryStatus);
        initUsageBySlot(proxy);

        // SelionRemoteProxy only
        initSeLionRemoteProxySpecificValues(proxy);

    }

    private void determineStatus(RemoteProxy proxy, boolean doQuery) {
        if (!doQuery) {
            return;
        }

        status = "offline";
        try {
            JsonObject value = proxy.getStatus().get("value").getAsJsonObject();
            status = "online";
            version = value.get("build").getAsJsonObject().get("version").getAsString();
            StringBuilder buf = new StringBuilder();
            buf.append(value.get("os").getAsJsonObject().get("name").getAsString());
            buf.append(" ");
            buf.append(value.get("os").getAsJsonObject().get("version").getAsString());
            os = buf.toString();
        } catch (Exception e) { // NOSONAR
            // ignore
        }
    }

    private void initUsageBySlot(RemoteProxy proxy) {
        // figure out usage by slot type
        slotUsage = new HashMap<>();
        for (TestSlot slot : proxy.getTestSlots()) {
            String slotType = getSlotType(slot);
            SlotInfo info = slotUsage.get(slotType);
            if (info == null) {
                info = new SlotInfo(getMaxInstances(slot));
            }
            if (slot.getSession() != null) {
                info.addUsed();
            }
            slotUsage.put(slotType, info);
        }
    }

    // SeLion specific features
    private void initSeLionRemoteProxySpecificValues(RemoteProxy proxy) {
        if (SeLionRemoteProxy.class.getCanonicalName().equals(
                proxy.getOriginalRegistrationRequest().getRemoteProxyClass())) {
            SeLionRemoteProxy srp = (SeLionRemoteProxy) proxy;

            // figure out if the proxy is scheduled to shutdown
            isShuttingDown = srp.isScheduledForRecycle();

            // update the logsLocation if the proxy supports LogServlet
            if (srp.supportsViewLogs()) {
                logsLocation = proxy.getRemoteHost().toExternalForm() + "/extra/" + LogServlet.class.getSimpleName();
            }

            totalSessionsStarted = srp.getTotalSessionsStarted();
            totalSessionsComplete = srp.getTotalSessionsComplete();
            uptimeInMinutes = srp.getUptimeInMinutes();
        }
    }

    private String getSlotType(TestSlot slot) {
        Map<String, Object> caps = slot.getCapabilities();
        String browserName = (String) caps.get(CapabilityType.BROWSER_NAME);
        String version = (String) caps.get(CapabilityType.VERSION);
        if (version != null) {
            return browserName.concat(":v").concat(version);
        }
        return browserName;
    }

    private int getMaxInstances(TestSlot slot) {
        Map<String, Object> caps = slot.getCapabilities();
        return Integer.parseInt(caps.get(RegistrationRequest.MAX_INSTANCES).toString());
    }
}
