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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.proxy.SeLionRemoteProxy;
import com.paypal.selion.utils.ServletHelper;

/**
 * This {@link RegistryBasedServlet} based servlet is responsible for sending restart requests to all the registered
 * nodes.<br>
 * <br>
 * This requires the hub to also have {@link LoginServlet} available. Furthermore, only nodes which use
 * {@link SeLionRemoteProxy} and {@link NodeForceRestartServlet} or implement support for the HTTP request
 * <b>/extra/NodeForceRestartServlet</b> are compatible.<br>
 * <br>
 * If there isn't a process, such as SeLion's Grid with <i>continuousRestart</i> on, monitoring and restarting the node
 * on exit(), the node will be shutdown but not restarted.
 */
public class GridForceRestartDelegateServlet extends RegistryBasedServlet {

    private static final long serialVersionUID = 1L;

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(GridForceRestartDelegateServlet.class);

    public GridForceRestartDelegateServlet() {
        this(null);
    }

    public GridForceRestartDelegateServlet(Registry registry) {
        super(registry);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        process(req, response);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.entering();

        if (request.getSession(false) == null) {
            response.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }
        PrintWriter writer = response.getWriter();

        if (request.getParameter("form_id") != null && request.getParameter("form_id").equals("restart_nodes")) {
            boolean isForcedRestart = request.getParameter("submit").equals("Force Restart");

            String nodes[] = request.getParameterValues("nodes");

            if (nodes == null || nodes.length == 0) {
                ServletHelper.displayMessageOnRedirect(writer,
                        "Please select at least 1 node in order to perform restart.");
                return;
            }
            for (String node : nodes) {
                RemoteProxy proxy = this.getRegistry().getProxyById(node);
                if (proxy == null) {
                    continue;
                }

                if ((proxy instanceof SeLionRemoteProxy) && (((SeLionRemoteProxy) proxy).supportsForceShutdown())) {
                    if (isForcedRestart) {
                        LOGGER.info("Sending forced restart request to " + proxy.getId());
                        ((SeLionRemoteProxy) proxy).forceNodeShutdown();
                    } else {
                        LOGGER.info("Sending restart request to " + proxy.getId());
                        ((SeLionRemoteProxy) proxy).requestNodeShutdown();
                    }
                } else {
                    LOGGER.warning("Node " + node + " does not support restart.");
                }
            }
            ServletHelper.displayMessageOnRedirect(writer, "Restart process initiated on all nodes.");
        } else {
            ServletHelper.displayHeader(writer);
            writer.write("<div id='form_container'>");
            writer.write("<form id='myForm' name='myForm' class='appnitro' method='post' action='"
                    + GridForceRestartDelegateServlet.class.getSimpleName() + "' >");
            writer.write("<div class='form_description'>");
            writer.write("<h2>SeLion Grid - Node Restart</h2>");
            writer.write("<p>Use this page to restart nodes</p>");
            writer.write("</div>");
            writer.write("<ul>");
            writer.write("<li id='li_1' >");
            writer.write("<label class='description' for='element_1'>List of nodes to restart </label>");

            ProxySet proxies = this.getRegistry().getAllProxies();
            Iterator<RemoteProxy> iterator = proxies.iterator();
            boolean nodesPresent = false;
            while (iterator.hasNext()) {
                RemoteProxy proxy = iterator.next();
                if (proxy == null) {
                    continue;
                }

                if ((proxy instanceof SeLionRemoteProxy) && (((SeLionRemoteProxy) proxy).supportsForceShutdown())) {
                    writer.write("<input name='nodes' class='element checkbox' type='checkbox' value='" + proxy.getId()
                            + "' />");
                    writer.write(((SeLionRemoteProxy) proxy).isScheduledForRecycle() ? "<label class='choice'>"
                            + proxy.getId() + " (restart scheduled)</label>" : "<label class='choice'>" + proxy.getId()
                            + "</label>");
                    nodesPresent = true;
                } else {
                    LOGGER.warning("Node " + proxy.getId() + " does not support force restart.");
                }
            }

            writer.write("</li>");
            String defaultMsg = "<div style=\"padding-top: 20px\"><label class='choice'>No nodes are available to restart</label class='choice'></div>";
            if (nodesPresent) {
                defaultMsg = "<li class='buttons'><input type='hidden' name='form_id' value='restart_nodes' />"
                        + "<input id='saveForm' class='button_text' type='submit' name='submit' value='Restart'/>"
                        + "<input id='saveForm' class='button_text' type='submit' name='submit' value='Force Restart'/></li>";
            }
            writer.write(defaultMsg);
            writer.write("</ul>");
            writer.write("</form>");
            ServletHelper.displayFooter(writer);
            writer.write("</body>");
            writer.write("</html>");
        }

        LOGGER.exiting();
    }

}
