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

import com.paypal.selion.grid.matchers.SeLionSauceCapabilityMatcher;
import com.paypal.selion.pojos.SeLionGridConstants;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.testng.Assert.*;

/**
 * Tests for LoginServlet
 */
public class LoginServletTest extends BaseGridRegistyServletTest {
    private static LoginServlet servlet;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        initRegistry();
        servlet = new LoginServlet(registry);
    }

    private void validateForNewLoginPage(MockHttpServletResponse response) throws Exception {
        validateHtmlResponseContent(response, "Grid Login", "Enter username and password");
    }

    /*
     * Default login page should be presented when session is not present
     */
    @Test
    public void testGetForNewLoginPage() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateForNewLoginPage(response);
    }

    /*
     * Default login page should be presented when session is not present
     */
    @Test
    public void testPostForNewLoginPage() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateForNewLoginPage(response);
    }

    /*
     * Session should be invalidated and default login page presented
     */
    @Test
    public void testGetForNewLoginPageWithSessionPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateForNewLoginPage(response);
    }

    /*
     * Session should be invalidated and default login page presented
     */
    @Test
    public void testPostForNewLoginPageWithSessionPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateForNewLoginPage(response);
    }

    /*
     * Logout action should occur and logged out page should be displayed
     */
    @Test
    public void testGetForLogout() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        request.addParameter(LoginServlet.LOGOUT, "true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateForNewLoginPage(response);
        // session should be invalidated
        assertNull(request.getSession(false));
    }

    private void validateLoginSuccess(MockHttpServletRequest request, MockHttpServletResponse response, String location)
            throws Exception {
        assertEquals(response.getStatus(), HttpServletResponse.SC_MOVED_TEMPORARILY);
        assertEquals(response.getHeaders("Location").get(0), location);
        assertEquals(request.getSession(false).getAttribute(LoginServlet.USER_ID), "admin");
        assertEquals(request.getSession(false).getAttribute(LoginServlet.PASSWORD), "admin");
    }

    private void validateLoginFailure(MockHttpServletRequest request, MockHttpServletResponse response)
            throws Exception {
        validateHtmlResponseContent(response, "Grid Login", "Invalid Credentials");
        // a new session should still be active
        assertNotNull(request.getSession(false));
    }

    /*
     * HTTP GET based login action should succeed
     */
    @Test
    public void testGetForLogin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(LoginServlet.FORM_ID, "login");
        request.addParameter(LoginServlet.USER_ID, "admin");
        request.addParameter(LoginServlet.PASSWORD, "admin");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateLoginSuccess(request, response, SeLionGridConstants.GRID_HOME_PAGE_URL);
    }

    /*
     * HTTP GET based login action should fail
     */
    @Test
    public void testGetForLoginFailure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        request.addParameter(LoginServlet.FORM_ID, "login");
        request.addParameter(LoginServlet.USER_ID, "admin");
        request.addParameter(LoginServlet.PASSWORD, "password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateLoginFailure(request, response);
    }

    /*
     * HTTP GET based login action with valid credentials in session but invalid credentials in query parameters should
     * succeed
     */
    @Test
    public void testGetForLoginWithSessionCredentialsPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // add the valid credentials to the session
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginServlet.USER_ID, "admin");
        session.setAttribute(LoginServlet.PASSWORD, "admin");
        // add invalid credentials to the query parameters
        request.addParameter(LoginServlet.FORM_ID, "login");
        request.addParameter(LoginServlet.USER_ID, "admin");
        request.addParameter(LoginServlet.PASSWORD, "password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateLoginSuccess(request, response, SeLionGridConstants.GRID_HOME_PAGE_URL);
    }

    /*
     * HTTP POST based login action should succeed
     */
    @Test
    public void testPostForLogin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(LoginServlet.FORM_ID, "login");
        request.addParameter(LoginServlet.USER_ID, "admin");
        request.addParameter(LoginServlet.PASSWORD, "admin");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateLoginSuccess(request, response, SeLionGridConstants.GRID_HOME_PAGE_URL);
    }

    /*
     * HTTP POST based login action should fail
     */
    @Test
    public void testPostForLoginFailure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        request.addParameter(LoginServlet.FORM_ID, "login");
        request.addParameter(LoginServlet.USER_ID, "admin");
        request.addParameter(LoginServlet.PASSWORD, "password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateLoginFailure(request, response);
    }

    /*
     * HTTP POST based login action with valid credentials in session but invalid credentials in query parameters should
     * succeed
     */
    @Test
    public void testPostForLoginWithSessionCredentialsPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // add the valid credentials to the session
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginServlet.USER_ID, "admin");
        session.setAttribute(LoginServlet.PASSWORD, "admin");
        // add invalid credentials to the query parameters
        request.addParameter(LoginServlet.FORM_ID, "login");
        request.addParameter(LoginServlet.USER_ID, "admin");
        request.addParameter(LoginServlet.PASSWORD, "password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateLoginSuccess(request, response, SeLionGridConstants.GRID_HOME_PAGE_URL);
    }

    /*
     * Test login action for selion sauce grid
     */
    @Test
    public void testProcessForSauceGridLogin() throws Exception {
        // Create a hubConfig with the Sauce capability matcher
        GridHubConfiguration hubConfig = new GridHubConfiguration();
        hubConfig.capabilityMatcher = new SeLionSauceCapabilityMatcher();

        // Create a Selenium grid registry, using the new hubConfig
        Registry registry = Registry.newInstance(null, hubConfig);

        // Create a Selenium grid registration request
        GridNodeConfiguration nodeConfig = new GridNodeConfiguration();
        nodeConfig.host = ipAddress;
        nodeConfig.port = nodePort;

        RegistrationRequest registrationRequest = new RegistrationRequest(nodeConfig);

        // Add a DefaultRemoteProxy to the registry
        RemoteProxy remoteProxy = new DefaultRemoteProxy(registrationRequest, registry);
        registry.add(remoteProxy);

        LoginServlet servlet = new LoginServlet(registry);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(LoginServlet.FORM_ID, "login");
        request.addParameter(LoginServlet.USER_ID, "admin");
        request.addParameter(LoginServlet.PASSWORD, "admin");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateLoginSuccess(request, response, SeLionGridConstants.SAUCE_GRID_HOMEPAGE_URL);
    }
}
