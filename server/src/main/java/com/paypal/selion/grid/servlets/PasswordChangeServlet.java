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

import com.paypal.selion.utils.AuthenticationHelper;
import com.paypal.selion.utils.ServletHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

/**
 * This servlet provides the ability to change the password for servlets which require/use {@link LoginServlet}
 */
public class PasswordChangeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Resource path to the password change html template file
     */
    public static final String RESOURCE_PAGE_FILE = "/com/paypal/selion/html/passwordChangeServlet.html";

    /**
     * Form parameter for the old password
     */
    public static final String OLD_PASSWORD = "oldPassword";

    /**
     * Form parameter for the new password (first entry)
     */
    public static final String NEW_PASSWORD_1 = "newPassword1";

    /**
     * Form parameter for the new password (second entry)
     */
    public static final String NEW_PASSWORD_2 = "newPassword2";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        askForCredentialsPage(resp);
    }

    private void askForCredentialsPage(HttpServletResponse resp) throws IOException {
        loadPage(resp, "Fill out the form to change the management console password");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Redirecting to login page if session is not found
        if (req.getSession(false) == null) {
            resp.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }

        String userId = (String) req.getSession(false).getAttribute(LoginServlet.USER_ID);
        String oldPassword = req.getParameter(OLD_PASSWORD);
        String newPassword1 = req.getParameter(NEW_PASSWORD_1);
        String newPassword2 = req.getParameter(NEW_PASSWORD_2);

        if (!newPassword1.contentEquals(newPassword2) || newPassword1 == null || newPassword2 == null) {
            loadPage(resp, "<b>The new passwords do not match</b>");
        } else if (!AuthenticationHelper.authenticate(userId, oldPassword)) {
            loadPage(resp, "<b>The old password did not match the one on record</b>");
        } else if (!AuthenticationHelper.changePassword(userId, newPassword1)) {
            loadPage(resp, "<b>Something went wrong while changing the password.</b>");
        } else {
            HttpSession session = req.getSession(false);
            if (session != null) {
                // invalidating the current session so that the password change is reflected in the forth coming session
                session.invalidate();
            }
            ServletHelper.respondAsHtmlWithMessage(resp, "<p align='center'><b>Password changed</b></p>");
        }
    }

    private void loadPage(HttpServletResponse resp, String errorMessage) throws IOException {
        ServletHelper.respondAsHtmlUsingArgsAndTemplateWithHttpStatus(resp, RESOURCE_PAGE_FILE,
                HttpServletResponse.SC_OK, errorMessage);
    }
}
