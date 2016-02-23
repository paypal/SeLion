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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LogServletTest {
    private static LogServlet servlet;

    @BeforeClass
    public void beforeClass() {
        servlet = new LogServlet();
    }

    private void validateResultingPage(MockHttpServletResponse response, String... contains) throws Exception {
        assertEquals(response.getContentType(), "text/html");
        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertEquals(response.getCharacterEncoding(), "UTF-8");
        for (String contain : contains) {
            assertTrue(response.getContentAsString().contains((contain)));
        }
    }

    @Test
    public void testDoGet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateResultingPage(response, "View Logs on - localhost");
    }

    @Test
    public void testDoPost() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateResultingPage(response, "View Logs on - localhost");
    }
}
