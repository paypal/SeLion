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

package com.paypal.selion.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.http.HttpStatus;

/**
 * A utility class that basically helps in extracting information from Servlet request/responses also has the commonly
 * load the HTML template from resource and write it to the response.
 *
 */
public final class ServletHelper {

    private static final String MESSAGE_RESOURCE_PAGE_FILE = "/com/paypal/selion/html/message.html";

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
     * Sends a HTTP response as a application/json document and with a HTTP status code.
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
     * Sends a HTTP response as a text/html document and with a HTTP status code. Injects a json object into the
     * template before responding.
     *
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param response
     *            The response object which will be serialized to a JSON document.
     * @param resourcePageTemplate
     *            The HTML template to use which is loaded as a classpath resource.
     * @param statusCode
     *            The HTTP status code to send with the response.
     * @throws IOException
     */
    public static void respondAsHtmlUsingJsonAndTemplateWithHttpStatus(HttpServletResponse resp, Object response,
            String resourcePageTemplate, int statusCode) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusCode);

        String template = IOUtils.toString(ServletHelper.class.getResourceAsStream(resourcePageTemplate), "UTF-8");
        final String json = new GsonBuilder().serializeNulls().create().toJson(response);
        final String jsonUtf8 = new String(json.getBytes(), "UTF-8");
        template = String.format(template, jsonUtf8);
        resp.getOutputStream().print(template);
        resp.flushBuffer();
    }

    /**
     * Sends a HTTP response as a text/html document and with a HTTP status code. Injects the the provided arguments
     * into the template before responding.
     *
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param resourcePageTemplate
     *            The HTML template to use which is loaded as a classpath resource.
     * @param statusCode
     *            The HTTP status code to send with the response.
     * @param args
     *            Data which will be inserted in to the template. Data must be typed in accordance with template
     *            expectations -- all args are passed to <code>String.format</code>
     * @throws IOException
     */
    public static void respondAsHtmlUsingArgsAndTemplateWithHttpStatus(HttpServletResponse resp,
            String resourcePageTemplate, int statusCode, Object... args) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusCode);

        String template = IOUtils.toString(ServletHelper.class.getResourceAsStream(resourcePageTemplate), "UTF-8");
        template = String.format(template, args);
        resp.getOutputStream().print(template);
        resp.flushBuffer();
    }

    /**
     * Sends a HTTP response as a text/html document and with a HTTP status code.
     *
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param resourcePageTemplate
     *            The HTML template to use which is loaded as a classpath resource.
     * @param statusCode
     *            The HTTP status code to send with the response.
     * @throws IOException
     */
    public static void respondAsHtmlUsingTemplateWithHttpStatus(HttpServletResponse resp, String resourcePageTemplate,
            int statusCode) throws IOException {
        respondAsHtmlUsingArgsAndTemplateWithHttpStatus(resp, resourcePageTemplate, statusCode);
    }

    /**
     * Sends a HTTP response as a text/html document. Replies with HTTP status code 200 OK
     *
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param resourcePageTemplate
     *            The HTML template to use which is loaded as a classpath resource.
     * @throws IOException
     */
    public static void respondAsHtmlUsingTemplate(HttpServletResponse resp, String resourcePageTemplate)
            throws IOException {
        respondAsHtmlUsingTemplateWithHttpStatus(resp, resourcePageTemplate, HttpStatus.SC_OK);
    }

    /**
     * Utility method used to display a message when re-direction happens in the UI flow. Uses the template
     * {@link #MESSAGE_RESOURCE_PAGE_FILE}
     *
     * @param resp
     *            A {@link HttpServletResponse} object that the servlet is responding on.
     * @param message
     *            Message to display.
     * @throws IOException
     */
    public static void respondAsHtmlWithMessage(HttpServletResponse resp, String message) throws IOException {
        respondAsHtmlUsingArgsAndTemplateWithHttpStatus(resp, MESSAGE_RESOURCE_PAGE_FILE, HttpStatus.SC_OK, message);
    }
}
