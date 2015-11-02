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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.SauceConfigReader;
import com.paypal.selion.utils.ServletHelper;

/**
 * This {@link RegistryBasedServlet} based servlet update the Sauce Configuration json file based on the input provided
 * via POST operation and re-load the SauceConfigReader properties. For GET request it will return the
 * updateSauceConfigPage.html content. URL of the Servlet :
 * <code>http://{hub-host}:{hub-port}/grid/admin/SauceConfigChangeServlet</code>. <br>
 * <br>
 * This requires the hub to also have {@link LoginServlet} available.
 */
public class SauceConfigChangeServlet extends RegistryBasedServlet {

    /**
     * Resource path to the sauce config html file
     */
    public static final String RESOURCE_PAGE_FILE = "/pages/updateSauceConfigPage.html";

    private static final long serialVersionUID = 1L;

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SauceConfigChangeServlet.class);

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
        String finalHtml = IOUtils.toString(this.getClass().getResourceAsStream(RESOURCE_PAGE_FILE), "UTF-8");
        writer.write(finalHtml);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Redirecting to login page if session is not found
        if (req.getSession(false) == null) {
            resp.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }

        String msg = "<p align='center'><b>Sauce configuration updated successfully</b></p>";
        String sauceURL = req.getParameter("sauceURL");
        String key = req.getParameter("username") + ":" + req.getParameter("accessKey");
        String authKey = new String(Base64.encodeBase64(key.getBytes()));

        Path path = Paths.get(SeLionGridConstants.SAUCE_CONFIG_FILE);
        boolean isUpdateSuccess = false;

        try (BufferedWriter bw = Files.newBufferedWriter(path, Charset.defaultCharset())) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("authenticationKey", authKey);
            jsonObject.addProperty("sauceURL", sauceURL);
            bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
            LOGGER.info("Sauce config file updated");
            isUpdateSuccess = true;
        } catch (Exception e) {
            msg = "<p align='center'><b>Sauce config file update failed. Please refer to the log file for the failure.</b></p>";
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        if (isUpdateSuccess) {
            // Load configuration once again because its updated just now
            SauceConfigReader.getInstance().loadConfig();
        }

        ServletHelper.displayMessageOnRedirect(resp.getWriter(), msg);
    }
}
