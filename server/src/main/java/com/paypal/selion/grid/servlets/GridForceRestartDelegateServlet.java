/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.proxy.SeLionRemoteProxy;
import com.paypal.selion.utils.ServletHelper;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    /**
     * Resource path to the grid auto upgrade html template file
     */
    public static final String RESOURCE_PAGE_FILE = "/com/paypal/selion/html/gridForceRestartDelegateServlet.html";

    private static final long serialVersionUID = 1L;

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(GridForceRestartDelegateServlet.class);

    /**
     * Request parameter used to perform the restart. Value must be 'restart_nodes'.
     */
    public static final String FORM_ID = "form_id";

    /**
     * Request parameter used to indicate restart type. Forced or Graceful.
     */
    public static final String SUBMIT = "submit";

    /**
     * Request parameter which contains nodes to restart.
     */
    public static final String NODES = "nodes";

    public GridForceRestartDelegateServlet() {
        this(null);
    }

    public GridForceRestartDelegateServlet(GridRegistry registry) {
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

        if (request.getParameter(FORM_ID) != null && request.getParameter(FORM_ID).equals("restart_nodes")) {
            boolean isForcedRestart = request.getParameter(SUBMIT).equals("Force Restart");

            String nodes[] = request.getParameterValues(NODES);

            if (nodes == null || nodes.length == 0) {
                ServletHelper.respondAsHtmlWithMessage(response,
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
            ServletHelper.respondAsHtmlWithMessage(response, "Restart process initiated on all nodes.");
        } else {
            List<ProxyInfo> proxies = getProxyInfo();
            ServletHelper.respondAsHtmlUsingJsonAndTemplateWithHttpStatus(response, proxies, RESOURCE_PAGE_FILE,
                    HttpServletResponse.SC_OK);
        }

        LOGGER.exiting();
    }

    private List<ProxyInfo> getProxyInfo() {
        List<ProxyInfo> nodes = new ArrayList<>();
        ProxySet proxies = this.getRegistry().getAllProxies();
        Iterator<RemoteProxy> iterator = proxies.iterator();
        while (iterator.hasNext()) {
            RemoteProxy proxy = iterator.next();
            if ((proxy instanceof SeLionRemoteProxy) && (((SeLionRemoteProxy) proxy).supportsForceShutdown())) {
                nodes.add(new ProxyInfo(proxy, false));
            }
        }
        return nodes;
    }
}
