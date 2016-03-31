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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

public class SauceServletTest extends BaseGridRegistyServletTest {
    private static SauceServlet servlet;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        initRegistry();
        try {
            if (registry == null) {
                fail("registry can not be null for SauceServletTest");
            }
            hub.start();
        } catch (Exception e) {
            fail("Unable to start a hub for SauceServletTest", e);
        }
        // Initialize the servlet under test
        servlet = new SauceServlet(registry);
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {
        hub.stop();
    }

    @Test
    public void testGetForRedirectToLoginServlet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateRedirectedToLoginServlet(response);
    }

    @Test
    public void testPostForRedirectToLoginServlet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateRedirectedToLoginServlet(response);
    }

    /*
     * Verify via HTTP GET that we can start a Sauce proxy
     */
    @Test
    public void testDoGet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateHtmlResponseContent(response, "Grid Management Console", "Sauce node registered successfully");
        assertSame(hub.getRegistry(), servlet.getRegistry(), "hub and servlet do not point to the same registry");
        int tries = 0;
        do {
            Thread.sleep(1000); // Give the hub a second to complete registration of the new virtual node
            tries += 1;
        } while ((hub.getRegistry().getAllProxies().size() < 1) && (tries < 10));
        assertNotNull(hub.getRegistry().getProxyById(SauceServlet.PROXY_ID), "getProxyById returned null");
    }

    /*
     * Verify via HTTP POST we can only start one Sauce node
     */
    @Test(dependsOnMethods = { "testDoGet" })
    public void testDoPost() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateHtmlResponseContent(response, "Grid Management Console", "Sauce node already registered");
        assertSame(hub.getRegistry(), servlet.getRegistry(), "hub and servlet do not point to the same registry");
        assertNotNull(hub.getRegistry().getProxyById(SauceServlet.PROXY_ID), "getProxyById returned null");
    }

    /*
     * Verify via HTTP GET that we can stop a Sauce proxy
     */
    @Test(dependsOnMethods = { "testDoPost" })
    public void testDoGetForShutdown() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        request.addParameter(SauceServlet.SHUTDOWN_PARAM, "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateHtmlResponseContent(response, "Grid Management Console", "Sauce node shutdown successfully");
    }

    /*
     * Verify via HTTP POST that we get no sauce running when requesting shutdown
     */
    @Test(dependsOnMethods = { "testDoGetForShutdown" })
    public void testDoPostForShutdown() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        request.addParameter(SauceServlet.SHUTDOWN_PARAM, "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateHtmlResponseContent(response, "Grid Management Console", "There is no sauce node running");
    }
}
