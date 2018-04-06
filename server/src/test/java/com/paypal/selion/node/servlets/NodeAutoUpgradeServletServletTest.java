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

package com.paypal.selion.node.servlets;

import com.paypal.selion.grid.servlets.GridAutoUpgradeDelegateServlet;
import com.paypal.selion.pojos.SeLionGridConstants;
import org.apache.commons.io.FileUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;

import java.io.File;

public class NodeAutoUpgradeServletServletTest extends BaseNodeServletTest {
    private static NodeAutoUpgradeServlet servlet;

    @BeforeClass
    public void beforeClass() {
        servlet = new NodeAutoUpgradeServlet();
    }

    @Test
    public void testDoGet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateJsonResponse(response, HttpServletResponse.SC_OK, "ready");
    }

    @Test
    public void testDoPostBadRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, "failed");
    }

    @Test
    public void testDoPostForbidden() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(GridAutoUpgradeDelegateServlet.PARAM_JSON, "[]");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateJsonResponse(response, HttpServletResponse.SC_FORBIDDEN, "failed");
    }

    @Test
    public void testDoPost() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(InsecureHttpPostAuthChallenge.TOKEN_PARAMETER,
                             InsecureHttpPostAuthChallenge.CONFIGURED_TOKEN_VALUE);
        request.addParameter(GridAutoUpgradeDelegateServlet.PARAM_JSON, FileUtils.readFileToString(new File(
                SeLionGridConstants.DOWNLOAD_JSON_FILE), "UTF-8"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateJsonResponse(response, HttpServletResponse.SC_OK, "success");
    }
}
