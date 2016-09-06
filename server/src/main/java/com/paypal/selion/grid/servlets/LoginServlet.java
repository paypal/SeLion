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

package com.paypal.selion.grid.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.utils.CapabilityMatcher;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.paypal.selion.grid.matchers.SeLionSauceCapabilityMatcher;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.AuthenticationHelper;
import com.paypal.selion.utils.ServletHelper;

/**
 * This plain vanilla servlet is responsible for supporting login/logout and display of the main page.
 * 
 */
public class LoginServlet extends RegistryBasedServlet {

    private static final long serialVersionUID = 1L;

    private static final String RESOURCE_PAGE_FILE = "/com/paypal/selion/html/loginServlet.html";

    /**
     * Request parameter to perform logout. Value must be 'true'.
     */
    public static final String LOGOUT = "logout";

    /**
     * Request parameter to perform login. Value must be 'login'.
     */
    public static final String FORM_ID = "form_id";

    /**
     * Request parameter that carries the userid
     */
    public static final String USER_ID = "userid";

    /**
     * Request parameter that carries the password
     */
    public static final String PASSWORD = "password";

    public LoginServlet(Registry registry) {
        super(registry);
    }

    public LoginServlet() {
        this(null);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter(LOGOUT) != null && req.getParameter(LOGOUT).equals("true")) {

            HttpSession session = req.getSession();
            if (session != null) {
                session.invalidate();
            }
            ServletHelper.respondAsHtmlUsingArgsAndTemplateWithHttpStatus(resp, RESOURCE_PAGE_FILE,
                    HttpServletResponse.SC_OK, "Enter username and password");
        } else {
            process(req, resp);
        }
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameter(FORM_ID) != null && req.getParameter(FORM_ID).equals("login")) {
            String userId = req.getParameter(USER_ID);
            String password = req.getParameter(PASSWORD);
            // For already created session , if the session has username and the password then use the same to
            // authenticate user else get back to the parameters from the request
            HttpSession currentSession = req.getSession(false);
            if (currentSession != null) {
                userId = (String) currentSession.getAttribute(USER_ID);
                password = (String) currentSession.getAttribute(PASSWORD);
            }

            if (!AuthenticationHelper.authenticate(userId, password)) {
                /*
                 * To display error message if invalid username or password is entered
                 */
                ServletHelper.respondAsHtmlUsingArgsAndTemplateWithHttpStatus(resp, RESOURCE_PAGE_FILE,
                        HttpServletResponse.SC_OK, "<b>Invalid Credentials. Enter valid Username and Password</b>");
            } else {

                /*
                 * After successful login main page will be displayed with links to force restart and autoupgrade. Note:
                 * For every re-direction, a new session is created and the userId and password are forwarded with the
                 * session
                 */
                req.getSession(true);
                req.getSession().setAttribute(USER_ID, userId);
                req.getSession().setAttribute(PASSWORD, password);

                String page = SeLionGridConstants.GRID_HOME_PAGE_URL;
                CapabilityMatcher matcher = getRegistry().getConfiguration().capabilityMatcher;
                if (matcher instanceof SeLionSauceCapabilityMatcher) {
                    page = SeLionGridConstants.SAUCE_GRID_HOMEPAGE_URL;
                }

                resp.sendRedirect(page);
            }

        } else {

            /*
             * Login form will be displayed to get user name and password. If already created sessions are available,
             * those sessions will be invalidated
             */
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            ServletHelper.respondAsHtmlUsingArgsAndTemplateWithHttpStatus(resp, RESOURCE_PAGE_FILE,
                    HttpServletResponse.SC_OK, "Enter username and password");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        process(request, response);
    }
}
