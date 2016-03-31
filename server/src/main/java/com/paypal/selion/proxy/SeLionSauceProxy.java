/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import com.paypal.selion.grid.servlets.SauceServlet;
import com.paypal.selion.utils.SauceLabsRestApi;

import org.apache.commons.lang.StringUtils;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.GridConfigurationException;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.BaseRemoteProxy;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;

import com.paypal.selion.logging.SeLionGridLogger;

/**
 * This customized version of {@link BaseRemoteProxy} basically helps redirect all traffic to the SauceLabs cloud.
 */
public class SeLionSauceProxy extends BaseRemoteProxy {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SeLionSauceProxy.class);
    private final SauceLabsRestApi sauceApi;
    private final Lock accessLock = new ReentrantLock();

    public SeLionSauceProxy(RegistrationRequest request, Registry registry) {
        super(request, registry);

        if (! SauceServlet.PROXY_ID.equals(remoteHost.toExternalForm())) {
            throw new GridException(SeLionSauceProxy.class.getSimpleName() + " can not be used by an external process");
        }

        try {
            sauceApi = new SauceLabsRestApi();
        } catch (GridConfigurationException e ) {
            throw new GridException("Failed to initialize proxy: ", e);
        }
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        TestSession session = null;
        try {
            accessLock.lock();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            if (sauceApi.getNumberOfTCRunning() <= getMaxNumberOfConcurrentTestSessions()) {
                String username = (String) requestedCapability.get("sauceUserName");
                String accessKey = (String) requestedCapability.get("sauceApiKey");
                String tunnelId = (String) requestedCapability.get("parent-tunnel");
                if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(accessKey)) {
                    requestedCapability.put("username", username);
                    requestedCapability.put("accessKey", accessKey);
                }
                if (StringUtils.isNotEmpty(tunnelId)) {
                    requestedCapability.put("parent-tunnel", tunnelId);
                }
                session = super.getNewSession(requestedCapability);
            }
        } finally {
            accessLock.unlock();
        }
        return session;
    }

    public URL getRemoteHost() {
        try {
            return new URL(SauceServlet.PROXY_ID);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return remoteHost;
    }

    public void teardown() {
        super.teardown();
    }

    public boolean isAlive() {
        // If the sauce server is down the grid is marking node as down. To avoid this it is overridden to always return
        // true.
        return true;
    }

}
