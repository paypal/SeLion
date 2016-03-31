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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests for {@link GridAutoUpgradeDelegateServlet}
 */
// TODO Add tests which consider SeLionRemoteProxy instances
public class GridAutoUpgradeDelegateServletTest extends BaseGridRegistyServletTest {
    private static GridAutoUpgradeDelegateServlet servlet;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        initRegistry();
        // Initialize the servlet under test
        servlet = new GridAutoUpgradeDelegateServlet(registry);
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

    @Test
    public void testPostUpgradeHtmlResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        request.addParameter(GridAutoUpgradeDelegateServlet.PARAM_JSON, "[]");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateHtmlResponseContent(response, "Auto upgrade process initiated on all nodes.");
    }

    private void validateDefaultHtmlResponse(MockHttpServletResponse response) throws Exception {
        validateHtmlResponseContent(response, "SeLion Grid - Update Nodes", "\"name\": \"selenium\"");
    }

    @Test
    public void testGetDefaultHtmlResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateDefaultHtmlResponse(response);
    }

    @Test
    public void testPostDefaultHtmlResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateDefaultHtmlResponse(response);
    }
}
