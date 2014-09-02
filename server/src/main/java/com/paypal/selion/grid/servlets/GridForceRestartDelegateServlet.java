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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.paypal.selion.proxy.SeLionRemoteProxy;
import com.paypal.selion.utils.ServletHelper;

/**
 * This {@link RegistryBasedServlet} based servlet is responsible for sending restart requests to all the registered
 * nodes.
 * 
 */
public class GridForceRestartDelegateServlet extends RegistryBasedServlet {

    private static final long serialVersionUID = 1L;

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

        if (request.getSession(false) == null) {
            response.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }
        PrintWriter writer = response.getWriter();

        if (request.getParameter("form_id") != null && request.getParameter("form_id").equals("restart_nodes")) {
            String nodes[] = request.getParameterValues("nodes");

            if (nodes == null || nodes.length == 0) {
                ServletHelper.displayMessageOnRedirect(writer,"Please select atleast 1 node in order to perform restarts.");
                return;
            }
            for (String temp : nodes) {
                SeLionRemoteProxy proxy = (SeLionRemoteProxy) this.getRegistry().getProxyById(temp);
                if (proxy != null) {
                    proxy.shutdownNode();
                }
            }
            ServletHelper.displayMessageOnRedirect(writer,"All nodes were forcibly restarted.");
        } else {
            ServletHelper.displayHeader(writer, "SeLion Grid Marimba - Force Restart on Nodes");
            writer.write("<div id='form_container'>");
            writer.write("<form id='myForm' name='myForm' class='appnitro' method='post' action='"
                    + GridForceRestartDelegateServlet.class.getSimpleName() + "' >");
            writer.write("<div class='form_description'>");
            writer.write("<h2>SeLion Grid Marimba</h2>");
            writer.write("<p>This will help us to forcefully restart all or selected nodes</p>");
            writer.write("</div>");
            writer.write("<ul>");
            writer.write("<li id='li_1' >");
            writer.write("<label class='description' for='element_1'>List of nodes to restart forcefully </label>");
            writer.write("<span>");

            ProxySet proxies = this.getRegistry().getAllProxies();
            Iterator<RemoteProxy> iterator = proxies.iterator();
            boolean nodesPresent = false;
            while (iterator.hasNext()) {
                SeLionRemoteProxy proxy = (SeLionRemoteProxy) iterator.next();
                writer.write("<input name='nodes' class='element checkbox' type='checkbox' value='" + proxy.getId()
                        + "' />");
                writer.write("<label class='choice'>" + proxy.getId() + "</label>");
                nodesPresent = true;
            }

            writer.write("</span></li>");
            String defaultMsg = "<label class='choice'>No Nodes are available to Restart</label>";
            if (nodesPresent) {
                defaultMsg = "<li class='buttons'><input type='hidden' name='form_id' value='restart_nodes' /> <input id='saveForm' class='button_text' type='submit' name='submit' value='Submit' /></li>";
            }
            writer.write(defaultMsg);
            writer.write("</ul>");
            writer.write("</form>");
            ServletHelper.displayFooter(writer);
            writer.write("</body>");
            writer.write("</html>");
        }
    }

}
