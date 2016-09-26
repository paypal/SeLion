/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import javax.servlet.http.HttpServletResponse;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.BaseRemoteProxy;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.utils.CapabilityMatcher;
import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.base.Preconditions;

public class BaseGridRegistyServletTest {
    protected String ipAddress;
    protected Registry registry;
    protected Hub hub;
    protected int nodePort;
    protected int hubPort;

    BaseGridRegistyServletTest() {
        ipAddress = new NetworkUtils().getIpOfLoopBackIp4();
        nodePort = 1234;
        hubPort = 1234;
    }

    public void initRegistry() {
        if ((registry != null) && (hub != null)) {
            return;
        }

        int tries = 0;
        while ((hubPort == nodePort) && (tries < 5)) {
            hubPort = PortProber.findFreePort();
            nodePort = PortProber.findFreePort();
            tries += 1;
        }

        initRegistry(new DefaultCapabilityMatcher(), hubPort, nodePort);
    }

    public void initRegistry(CapabilityMatcher matcher, int hubPort, int nodePort) {
        if ((registry != null) && (hub != null)) {
            return;
        }

        Preconditions.checkArgument(hubPort > 0);
        Preconditions.checkArgument(nodePort > 0);
        Preconditions.checkArgument(hubPort != nodePort);
        Preconditions.checkArgument(matcher != null);

        this.hubPort = hubPort;
        this.nodePort = nodePort;

        // Create a Selenium grid hub configuration
        GridHubConfiguration hubConfig = new GridHubConfiguration();

        hubConfig.port = hubPort;
        hubConfig.capabilityMatcher = matcher;

        // Create a Hub instance
        hub = new Hub(hubConfig);

        // Create a Selenium grid registry, using the new hubConfig
        registry = Registry.newInstance(hub, hubConfig);

        // Create a Selenium grid registration request
        GridNodeConfiguration nodeConfig = new GridNodeConfiguration();
        nodeConfig.host = ipAddress;
        nodeConfig.port = nodePort;

        RegistrationRequest registrationRequest = new RegistrationRequest(nodeConfig);

        // Add a BaseRemoteProxy to the registry
        RemoteProxy remoteProxy = new BaseRemoteProxy(registrationRequest, registry);
        registry.add(remoteProxy);
    }

    public void validateRedirectedToLoginServlet(MockHttpServletResponse response) {
        assertEquals(response.getStatus(), HttpServletResponse.SC_MOVED_TEMPORARILY);
        assertEquals(response.getHeaders("Location").get(0), LoginServlet.class.getSimpleName());
    }

    public void validateHtmlResponseContent(MockHttpServletResponse response, String... contains) throws Exception {
        assertEquals(response.getContentType(), "text/html");
        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertEquals(response.getCharacterEncoding(), "UTF-8");
        for (String contain : contains) {
            assertTrue(response.getContentAsString().contains((contain)));
        }
    }
}
