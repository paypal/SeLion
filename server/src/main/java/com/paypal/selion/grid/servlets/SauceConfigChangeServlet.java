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

package com.paypal.selion.grid.servlets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.SauceConfigReader;
import com.paypal.selion.utils.ServletHelper;

/**
 * This {@link RegistryBasedServlet} based servlet update the Sauce Configuration json file based on the input provided
 * via POST operation and re-load the SauceConfigReader proerties. For GET request it will return the
 * updateSauceConfigPage.html content. URL of the Servlet :
 * <code>http://localhost:4444/grid/admin/SauceConfigChangeServlet</code>. Here <code>localhost</code> can be replaced
 * with the IP/name of the machine running the Hub and <code>4444</code> can be replaced with the port number on which
 * the Hub is listening to.
 * 
 */
public class SauceConfigChangeServlet extends RegistryBasedServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SauceConfigChangeServlet.class.getName());

    public SauceConfigChangeServlet(Registry registry) {
        super(registry);
    }

    public SauceConfigChangeServlet() {
        this(null);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null) {
            resp.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }
        PrintWriter writer = resp.getWriter();
        loadSauceConfigPage(writer);
    }

    private void loadSauceConfigPage(PrintWriter writer) throws IOException {
        String finalHtml = IOUtils.toString(this.getClass().getResourceAsStream(
                SeLionGridConstants.UPDATE_SAUCE_CONFIG_PAGE), "UTF-8");
        writer.write(finalHtml);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Redirecting to login page if session is not found
        if (req.getSession(false) == null) {
            resp.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }

        String msg = "<p align='center'><b>Sauce Configuration Updated Successfully</b></p>";
        String sauceURL = req.getParameter("sauceURL");
        String key = req.getParameter("username") + ":" + req.getParameter("accessKey");
        String authKey = new String(Base64.encodeBase64(key.getBytes()));

        Path path = Paths.get(SeLionGridConstants.SAUCE_CONFIG);
        boolean isUpdateSuccess = false;

        try (BufferedWriter bw = Files.newBufferedWriter(path, Charset.defaultCharset())) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("authenticationKey", authKey);
            jsonObject.addProperty("sauceURL", sauceURL);
            bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
            LOG.info("Sauce Config file updated successfully");
            isUpdateSuccess = true;
        } catch (Exception e) {
            msg = "<p align='center'><b>Sauce Config updation got failed. Please refer the log file for the failure</b></p>";
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

        if (isUpdateSuccess) {
            // Load configuration once again because its updated just now
            SauceConfigReader.getInstance().loadConfig();
        }

        ServletHelper.displayMessageOnRedirect(resp.getWriter(), msg);
    }
}
