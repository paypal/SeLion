/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.selion.pojos.PropsKeys;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.ServletHelper;

/**
 * This servlet retrieves selenium url, selenium checksum, ie driver url , ie driver checksum, chrome driver url and
 * chrome checksum from the client and writes those details in dowload.properties file in the current node
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
        if (! ServletHelper.hasAllParameters(request)) {
            return;
        }
        Properties prop = new Properties();
        try {
            // set the properties value
            for (PropsKeys eachKey : PropsKeys.getValuesForCurrentPlatform()) {
                String value = request.get(eachKey.getKey());
                if (value != null) {
                    prop.setProperty(eachKey.getKey(), value.trim());
                }
            }
            FileOutputStream f = new FileOutputStream(SeLionGridConstants.DOWNLOAD_FILE_PATH);
            prop.store(f, null);
            f.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
