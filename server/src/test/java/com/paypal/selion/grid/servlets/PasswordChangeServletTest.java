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

import javax.servlet.http.HttpSession;

/**
 * Tests for PasswordChangeServlet
 */
public class PasswordChangeServletTest extends BaseGridRegistyServletTest {
    private static PasswordChangeServlet servlet;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        servlet = new PasswordChangeServlet();
    }

    @Test
    public void testDoGet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        validateHtmlResponseContent(response, "SeLion Grid - Change Password",
                "Fill out the form to change the management console password");
    }

    @Test
    public void testDoPostSuccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginServlet.USER_ID, "admin");
        request.addParameter(PasswordChangeServlet.OLD_PASSWORD, "admin");
        request.addParameter(PasswordChangeServlet.NEW_PASSWORD_1, "admin");
        request.addParameter(PasswordChangeServlet.NEW_PASSWORD_2, "admin");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateHtmlResponseContent(response, "Grid Management Console", "Password changed");

    }

    @Test
    public void testDoPostOldPasswordFailure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginServlet.USER_ID, "admin");
        request.addParameter(PasswordChangeServlet.OLD_PASSWORD, "password");
        request.addParameter(PasswordChangeServlet.NEW_PASSWORD_1, "admin");
        request.addParameter(PasswordChangeServlet.NEW_PASSWORD_2, "admin");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateHtmlResponseContent(response, "Grid Management Console",
                "The old password did not match the one on record");
    }

    @Test
    public void testDoPostPasswordMismatchFailure() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginServlet.USER_ID, "admin");
        request.addParameter(PasswordChangeServlet.OLD_PASSWORD, "admin");
        request.addParameter(PasswordChangeServlet.NEW_PASSWORD_1, "admin");
        request.addParameter(PasswordChangeServlet.NEW_PASSWORD_2, "password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateHtmlResponseContent(response, "Grid Management Console", "The new passwords do not match");
    }

    @Test
    public void testDoPostNoSessionRedirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(PasswordChangeServlet.OLD_PASSWORD, "admin");
        request.addParameter(PasswordChangeServlet.NEW_PASSWORD_1, "admin");
        request.addParameter(PasswordChangeServlet.NEW_PASSWORD_2, "admin");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);
        validateRedirectedToLoginServlet(response);
    }
}
