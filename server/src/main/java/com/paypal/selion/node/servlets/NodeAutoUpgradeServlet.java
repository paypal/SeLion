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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.paypal.selion.grid.servlets.GridAutoUpgradeDelegateServlet;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.ServletHelper;

/**
 * This servlet retrieves the download.json content from the HTTP request and writes it to dowload.json file on the
 * current node
 * 
 */
public class NodeAutoUpgradeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(ServletHelper.getParameters(request));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(ServletHelper.getParameters(req));
    }

    public void process(Map<String, String> request) {
        if (request.get(GridAutoUpgradeDelegateServlet.PARAM_JSON) == null) {
            return;
        }
        try {
            String json = request.get(GridAutoUpgradeDelegateServlet.PARAM_JSON);
            FileUtils.writeStringToFile(new File(SeLionGridConstants.DOWNLOAD_JSON_FILE), json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
