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

package com.paypal.selion.grid.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.node.servlets.NodeAutoUpgradeServlet;
import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.proxy.SeLionRemoteProxy;
import com.paypal.selion.utils.ServletHelper;

/**
 * This {@link RegistryBasedServlet} servlet is responsible for getting the following information from a Grid admin, and
 * relaying it to each of the nodes, so that they may go about gracefully upgrading themselves once they are done with
 * servicing any of the tests that are already running. This information is captured by {@link NodeAutoUpgradeServlet}
 * which actually undertakes the task of creating a simple properties file and dumps in all of this relayed information.
 * <ul>
 * <li>The URL from where Selenium jar, ChromeDriver binary, IEDriverServer binary can be downloaded.
 * <li>The checksum associated with the each of the artifacts so that it can be cross checked to ascertain validity of
 * the same.
 * </ul>
 * <br>
 * Requires the hub to also have {@link LoginServlet} available. Furthermore, only nodes which use
 * {@link SeLionRemoteProxy}, {@link NodeAutoUpgradeServlet}, and {@link NodeForceRestartServlet} or implement support
 * for the HTTP requests <b>/extra/NodeAutoUpgradeServlet</b> and <b>/extra/NodeForceRestartServlet</b> are compatible.<br>
 * <br>
 * If there isn't a process, such as SeLion's Grid with <i>continuousRestart</i> on, monitoring and restarting the node
 * on exit(), the node will be shutdown but not restarted.
 */
public class GridAutoUpgradeDelegateServlet extends RegistryBasedServlet {

    /**
     * Resource path to the grid auto upgrade html template file
     */
    public static final String RESOURCE_PAGE_FILE = "/com/paypal/selion/html/gridAutoUpgradeDelegateServlet.html";

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(GridAutoUpgradeDelegateServlet.class);
    private static final String IDS = "ids";
    public static final String PARAM_JSON = "downloadJSON";
    private static final long serialVersionUID = 1L;

    public GridAutoUpgradeDelegateServlet() {
        this(null);
    }

    public GridAutoUpgradeDelegateServlet(Registry registry) {
        super(registry);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);

    }

    /**
     * This method constructs the html page that gets the information pertaining to the jars/binaries and their artifact
     * checksums from the user. The same method can also act as the end point that relays this information to each of
     * the nodes as well.
     * 
     * @param request
     *            - {@link HttpServletRequest} that represent the servlet request
     * @param response
     *            - {@link HttpServletResponse} that represent the servlet response
     * @throws IOException
     */
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // No active session ? Redirect the user to the login page.
        if (request.getSession(false) == null) {
            response.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.SC_OK);
        // idList will have the list of all the nodes that are failed to
        // auto-upgrade
        // (node may be restarting at the time of issuing the command to
        // upgrade).
        String idList = request.getParameter(IDS);
        String downloadJSON = request.getParameter(PARAM_JSON);
        PrintWriter writer = response.getWriter();
        if (downloadJSON != null) {
            // proceed with relaying the information to each of the nodes if and
            // only if the user has provided all
            // information for performing the upgrade.

            List<String> pendingProxy = new ArrayList<String>();
            if (idList == null) {
                // there were no nodes that failed to auto upgrade
                for (RemoteProxy eachProxy : this.getRegistry().getAllProxies()) {
                    if (eachProxy == null) {
                        continue;
                    }
                    if ((eachProxy instanceof SeLionRemoteProxy) && (((SeLionRemoteProxy) eachProxy).supportsAutoUpgrade())) {
                        if (!((SeLionRemoteProxy) eachProxy).upgradeNode(downloadJSON)) {
                            pendingProxy.add(eachProxy.getId());
                        }
                    } else {
                        LOGGER.warning("Node " + eachProxy.getId() + " can not be auto upgraded.");
                    }
                }
            } else {
                // hmm.. there were one or more nodes that didn't go through
                // with the upgrade (maybe because they were processing some tests).
                for (String eachId : idList.split(",")) {

                    if (!eachId.trim().isEmpty()) {
                        RemoteProxy proxy = getRegistry().getProxyById(eachId.trim());
                        if (proxy == null) {
                            continue;
                        }
                        if ((proxy instanceof SeLionRemoteProxy) && (((SeLionRemoteProxy) proxy).supportsAutoUpgrade())) {
                            if (!((SeLionRemoteProxy) proxy).upgradeNode(downloadJSON)) {
                                pendingProxy.add(proxy.getId());
                            }
                        } else {
                            LOGGER.warning("Node " + proxy.getId() + " can not be auto upgraded.");
                        }
                    }
                }
            }

            if (pendingProxy.size() > 0) {
                writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
                writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                writer.write("<body id='main_body'>");
                writer.write("<form id='myForm' name='myForm' class='appnitro' method='post' onsubmit='validateForm()' action='"
                        + GridAutoUpgradeDelegateServlet.class.getSimpleName() + "' >");
                String ids = "";
                for (String temp : pendingProxy) {
                    ids = ids + temp + ",";
                }
                ids = StringUtils.chop(ids);
                writer.write("The following nodes were not auto upgraded: " + ids);
                writer.write("<br>Click the Submit button to retry.");
                writer.write("<input type='hidden' name='" + IDS + "' value='" + ids + "'>");
                writer.write("<input type='hidden' name='" + PARAM_JSON + "' value='" + downloadJSON + "'>");
                writer.write("<input id='saveForm' class='button_text' type='submit' name='submit' value='Submit' />");
                writer.write("</form>");
                writer.write("</body>");
                writer.write("</html>");
            } else {
                ServletHelper.displayMessageOnRedirect(writer, "Auto upgrade process initiated on all nodes.");
            }
        } else {
            /*
             * Auto Upgrade form will be displayed. This the default page for GridAutoUpgradeDelegateServlet
             */
            showDefaultPage(writer);

        }
    }

    private void showDefaultPage(PrintWriter writer) throws IOException {
        String downloadJSON = "";
        try {
            downloadJSON = FileUtils.readFileToString(new File(SeLionGridConstants.DOWNLOAD_JSON_FILE));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to open download.json file", e);
        }

        String template = IOUtils.toString(
                this.getClass().getResourceAsStream(RESOURCE_PAGE_FILE), "UTF-8");

        // Format the template with servlet name and download json values
        writer.write(String.format(template, GridAutoUpgradeDelegateServlet.class.getSimpleName(), downloadJSON));
    }
}
