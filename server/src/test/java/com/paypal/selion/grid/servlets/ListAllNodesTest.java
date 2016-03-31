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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests for ListAllNodes servlet
 */
public class ListAllNodesTest extends BaseGridRegistyServletTest {
    private static ListAllNodes servlet;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        initRegistry();
        // Initialize the servlet under test
        servlet = new ListAllNodes(registry);
    }

    @Test
    public void testPostForHtmlResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateHtmlResponseContent(response, "SeLion Grid - Node Console",
                String.format("\"remoteHost\":\"http://%s:%s\"", ipAddress, nodePort));
    }

    @Test
    public void testGetForHtmlResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateHtmlResponseContent(response, "SeLion Grid - Node Console",
                String.format("\"remoteHost\":\"http://%s:%s\"", ipAddress, nodePort));
    }

    private void validateJsonResponse(MockHttpServletResponse response) throws Exception {
        assertEquals(response.getContentType(), "application/json");
        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertEquals(response.getCharacterEncoding(), "UTF-8");
        assertTrue(response.getContentAsString().contains(
                String.format("\"remoteHost\":\"http://%s:%s\"", ipAddress, nodePort)));
    }

    @Test
    public void testGetForJsonResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Accept", "application/json");
        servlet.doGet(request, response);
        validateJsonResponse(response);
    }

    @Test
    public void testPostForJsonResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Accept", "application/json");
        servlet.doPost(request, response);
        validateJsonResponse(response);
    }
}
