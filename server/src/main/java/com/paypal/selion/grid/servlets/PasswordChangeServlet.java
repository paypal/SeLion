/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;

import com.paypal.selion.utils.AuthenticationHelper;
import com.paypal.selion.utils.ServletHelper;

/**
 * This servlet provides the ability to change the password for servlets which require/use {@link LoginServlet}
 */
public class PasswordChangeServlet extends HttpServlet {

    /**
     * Resource path to the password change html template file
     */
    public static final String RESOURCE_PAGE_FILE = "/com/paypal/selion/html/passwordChangeServlet.html";

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        askForCredentialsPage(writer);
    }

    private void askForCredentialsPage(PrintWriter writer) throws IOException {
        String changePasswordMessage = "Fill out the form to change the management console password";

        String template = IOUtils.toString(this.getClass().getResourceAsStream(RESOURCE_PAGE_FILE), "UTF-8");
        writer.write(String.format(template, PasswordChangeServlet.class.getSimpleName(), changePasswordMessage));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

        String userid = (String) req.getSession().getAttribute("userId");
        String oldPassword = req.getParameter("oldPassword");
        String newPassword1 = req.getParameter("newPassword1");
        String newPassword2 = req.getParameter("newPassword2");

        if (!newPassword1.contentEquals(newPassword2) || newPassword1 == null || newPassword2 == null) {
            errorPage(writer, "The new passwords do not match");
        } else if (!AuthenticationHelper.authenticate(userid, oldPassword)) {
            errorPage(writer, "The old password did not match the one on record");

        } else if (!AuthenticationHelper.changePassword(userid, newPassword1)) {
            errorPage(writer, "Something went wrong while changing the password.");
        } else {
            HttpSession session = req.getSession(false);
            if (session != null) {
                // invalidating the current session so that the password change is reflected in the forth coming session
                session.invalidate();
            }
            ServletHelper.displayMessageOnRedirect(writer, "<p align='center'><b>Password changed</b></p>");
        }
    }

    private void errorPage(PrintWriter writer, String errorMessage) {
        writer.write("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
        writer.write("<html xmlns='http://www.w3.org/1999/xhtml'>");
        writer.write("<head>");
        writer.write("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        writer.write("<title>Grid Management Console</title>");
        writer.write("<link rel='stylesheet' type='text/css' href='/grid/resources/form/view.css' media='all' >");
        writer.write("<script type='text/javascript' src='/grid/resources/form/view.js'></script>");
        writer.write("</head>");
        writer.write("<body id='main_body' >");
        writer.write("<img id='top' src='/grid/resources/form/top.png' alt=''>");
        writer.write("<div id='form_container'>");
        writer.write("<p>" + errorMessage + "</p>");
        ServletHelper.displayFooter(writer);
        writer.write("</body>");
        writer.write("</html>");
    }

}
