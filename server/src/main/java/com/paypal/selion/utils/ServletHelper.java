/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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

package com.paypal.selion.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.grid.servlets.LoginServlet;

/**
 * A utility class that basically helps in extracting information from Servlet request/responses also has the commonly
 * repeated HTML code used for display purposes
 * 
 */
public final class ServletHelper {
    /**
     * Helps retrieve the parameters and its values as a Map
     * 
     * @param request
     *            A {@link HttpServletRequest} that represents the request from which the parameters and their
     *            corresponding values are to be extracted.
     * @return A {@link Map} that represents the parameters and their values
     */
    public static Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<?> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            String value = request.getParameter(key);
            if (StringUtils.isNotEmpty(value)) {
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    /**
     * Sends an HTTP response as a application/json document and with a HTTP status code.
     * 
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param response
     *            The response object which will be serialized to a JSON document
     * @param statusCode
     *            The HTTP status code to send with the response
     * @throws IOException
     */
    public static void respondAsJsonWithHttpStatus(HttpServletResponse resp, Object response, int statusCode)
            throws IOException {
        String json = new GsonBuilder().serializeNulls().create().toJson(response);
        String jsonUtf8 = new String(json.getBytes(), "UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusCode);
        resp.getOutputStream().print(jsonUtf8);
        resp.flushBuffer();
    }

    /**
     * Sends an HTTP response as a text/html document and with a HTTP status code. Injects a json object into the
     * template before responding.
     * 
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param response
     *            The response object which will be serialized to a JSON document
     * @param resourcePageTemplate
     *            The HTML template to use which is loaded as a classpath resource
     * @param statusCode
     *            The HTTP status code to send with the response
     * @throws IOException
     */
    public static void respondAsHtmlUsingJsonAndTemplateWithHttpStatus(HttpServletResponse resp, Object response,
            String resourcePageTemplate, int statusCode) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String template = IOUtils.toString(ServletHelper.class.getResourceAsStream(resourcePageTemplate), "UTF-8");
        final String json = new GsonBuilder().serializeNulls().create().toJson(response);
        final String jsonUtf8 = new String(json.getBytes(), "UTF-8");
        template = String.format(template, jsonUtf8);
        resp.getOutputStream().print(template);
        resp.flushBuffer();
    }

    /**
     * Utility method used to display a message when re-direction happens in the UI flow
     * 
     * @param writer
     *            The {@link PrintWriter} object that corresponds to a response
     * @param reDirectMessage
     *            Message to display
     */
    public static void displayMessageOnRedirect(PrintWriter writer, String reDirectMessage) {
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
        writer.write("<div style=\"margin: 20px 20px 0; padding:0 0 20px;\">");
        writer.write("<div class='form_description'>");
        writer.write("<h2>Grid Management Console</h2>");
        writer.write("<p>" + reDirectMessage + "</p>");
        writer.write("</div>");
        writer.write("<div id='footer'>");
        writer.write("<a href='/grid/admin/" + LoginServlet.class.getSimpleName() + "?form_id=login'>Go To Home</a>");
        writer.write("&nbsp;&nbsp;&nbsp;&nbsp;");
        writer.write("<a href='/grid/admin/" + LoginServlet.class.getSimpleName() + "?logout=true'>Logout</a>");
        writer.write("<p>Created by the SeLion Project</p>");
        writer.write("</div></div>");
        writer.write("</div> <img id='bottom' src='/grid/resources/form/bottom.png' alt=''>");
        writer.write("</body>");
        writer.write("</html>");

    }

    /**
     * Utility method to display the LoginPage for authenticating user
     * 
     * @param writer
     *            The {@link PrintWriter} object that corresponds to a response
     * @param messageToDisplay
     *            Status message to be displayed
     */
    public static void loginToGrid(PrintWriter writer, String messageToDisplay) {
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

        writer.write("<form id='form_720145' class='appnitro'  method='post' action='"
                + LoginServlet.class.getSimpleName() + "'>");
        writer.write("<div class='form_description'>");
        writer.write("<h2>Grid Login</h2>");
        writer.write("<p>" + messageToDisplay + "</p>");
        writer.write("</div>");
        writer.write("<ul >");

        writer.write("<li id='li_1' >");

        writer.write("<label class='description' for='userid'>User</label>");
        writer.write("<div>");
        writer.write("<input id='userid' name='userid' class='element text medium' type='text' maxlength='255' value=''/>");
        writer.write("</div>");
        writer.write("</li>       <li id='li_2' >");
        writer.write("<label class='description' for='password'>Password </label>");
        writer.write("<div>");
        writer.write("<input id='password' name='password' class='element text medium' type='password' maxlength='255' value=''/>");
        writer.write("</div>");
        writer.write("</li>");

        writer.write("<li class='buttons'>");
        writer.write("<input type='hidden' name='form_id' value='login' />");

        writer.write("<input id='saveForm' class='button_text' type='submit' name='submit' value='Submit' />");
        writer.write("        </li></ul></form>");
        writer.write("<div id='footer'>");
        writer.write("<p>Created by the SeLion Project</p>");
        writer.write("</div>");
        writer.write("</div> <img id='bottom' src='/grid/resources/form/bottom.png' alt=''>");
        writer.write("</body>");
        writer.write("</html>");
    }

    /**
     * Utility method to display footer to a HTML page which contains Home and Logout link
     * 
     * @param writer
     *            The {@link PrintWriter} object that corresponds to a response
     */
    public static void displayFooter(PrintWriter writer) {
        writer.write("<div id='footer'>");
        writer.write("<a href='/grid/admin/" + LoginServlet.class.getSimpleName() + "?form_id=login'>Go To Home</a>");
        writer.write("&nbsp;&nbsp;&nbsp;&nbsp;");
        writer.write("<a href='/grid/admin/" + LoginServlet.class.getSimpleName() + "?logout=true'>Logout</a>");
        writer.write("<p>Created by the SeLion Project</p>");
        writer.write("</div>");
        writer.write("</div> <img id='bottom' src='/grid/resources/form/bottom.png' alt=''>");
    }

    /**
     * Utility method to display header to a HTML page
     * 
     * @param writer
     *            The {@link PrintWriter} object that corresponds to a response
     */
    public static void displayHeader(PrintWriter writer) {
        writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        writer.write("<head>");
        writer.write("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        writer.write("<title>Grid Management Console</title>");
        writer.write("<link rel='stylesheet' type='text/css' href='/grid/resources/form/view.css' media='all' >");
        writer.write("<script type='text/javascript' src='/grid/resources/form/view.js'></script>");
        writer.write("</head>");
        writer.write("<body id='main_body'>");
        writer.write("<img id='top' src='/grid/resources/form/top.png' alt='' >");
    }
}
