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

package com.paypal.selion.node.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.paypal.selion.grid.servlets.GridAutoUpgradeDelegateServlet;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.ServletHelper;

/**
 * This servlet retrieves the download.json content from the HTTP request and writes it to dowload.json file on the
 * current node
 * 
 */
public class NodeAutoUpgradeServlet extends HttpServlet implements InsecureHttpPostAuthChallenge {

    private static final long serialVersionUID = 1L;
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(NodeAutoUpgradeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.entering();
        ServletHelper.respondAsJsonWithHttpStatus(resp, new NodeResponseBody().setReady(), HttpServletResponse.SC_OK);
        LOGGER.exiting();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.entering();
        Map<String, String> requestParams = ServletHelper.getParameters(req);

        if (requestParams.get(GridAutoUpgradeDelegateServlet.PARAM_JSON) == null) {
            ServletHelper.respondAsJsonWithHttpStatus(resp, new NodeResponseBody().setFailed(),
                    HttpServletResponse.SC_BAD_REQUEST);
            LOGGER.exiting();
            return;
        }

        if (!CONFIGURED_TOKEN_VALUE.equals(requestParams.get(TOKEN_PARAMETER))) {
            ServletHelper.respondAsJsonWithHttpStatus(resp, new NodeResponseBody().setFailed(),
                    HttpServletResponse.SC_FORBIDDEN);
            LOGGER.exiting();
            return;
        }

        try {
            String json = requestParams.get(GridAutoUpgradeDelegateServlet.PARAM_JSON);
            FileUtils.writeStringToFile(new File(SeLionGridConstants.DOWNLOAD_JSON_FILE), json);
            ServletHelper.respondAsJsonWithHttpStatus(resp, new NodeResponseBody().setSuccess(),
                    HttpServletResponse.SC_OK);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            NodeResponseBody exceptionResponse = new NodeResponseBody(e.getLocalizedMessage());
                    ServletHelper.respondAsJsonWithHttpStatus(resp, exceptionResponse,
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        LOGGER.exiting();
    }
}
