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

import com.paypal.selion.utils.SauceConfigReader;
import org.apache.commons.lang.StringUtils;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SauceConfigChangeServletTest extends BaseGridRegistyServletTest {
    private static SauceConfigChangeServlet servlet;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        initRegistry();
        // Initialize the servlet under test
        servlet = new SauceConfigChangeServlet(registry);
    }

    /*
     * HTTP GET with no session. Should be redirected to login page
     */
    @Test
    public void testDoGetForRedirectToLoginServlet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateRedirectedToLoginServlet(response);
    }

    /*
     * HTTP POST with no session. Should be redirected to login page
     */
    @Test
    public void testDoPostForRedirectToLoginServlet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateRedirectedToLoginServlet(response);
    }

    /*
     * Config change form should be displayed
     */
    @Test
    public void testDoGetForConfigChangePage() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateHtmlResponseContent(response, "SeLion Grid - Sauce Proxy Configuration", "Sauce URL");
    }

    /*
     * Sauce config should be updated and config change success page displayed
     */
    @Test
    public void testDoPostForSuccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        request.addParameter(SauceConfigChangeServlet.SAUCE_URL_PARAM, "http://sauce-url");
        request.addParameter(SauceConfigChangeServlet.ACCESS_KEY_PARAM, "access-key");
        request.addParameter(SauceConfigChangeServlet.USERNAME_PARAM, "sauce-super-user");
        request.addParameter(SauceConfigChangeServlet.RETRY_PARAM, "1");
        request.addParameter(SauceConfigChangeServlet.TIMEOUT_PARAM, "1000");
        request.addParameter(SauceConfigChangeServlet.PARENT_TUNNEL_PARAM, "my-parent-tunnel");
        request.addParameter(SauceConfigChangeServlet.TUNNEL_IDENTIFIER_PARAM, "tunnel-100");
        request.addParameter(SauceConfigChangeServlet.REQUIRE_USER_CREDENTIALS_PARAM, "on");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateHtmlResponseContent(response, "Grid Management Console", "Sauce configuration updated successfully");

        SauceConfigReader reader = SauceConfigReader.getInstance();
        assertEquals(reader.getSauceURL(), "http://sauce-url");
        assertEquals(reader.getUserName(), "sauce-super-user");
        assertTrue(StringUtils.isNotBlank(reader.getAuthenticationKey()));
        assertEquals(reader.getSauceRetry(), 1);
        assertEquals(reader.getSauceTimeout(), 1000);
        assertEquals(reader.getDefaultParentTunnel(), "my-parent-tunnel");
        assertEquals(reader.getDefaultTunnelIdentifier(), "tunnel-100");
        assertTrue(reader.isRequireUserCredentials());

    }
}
