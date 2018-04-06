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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.paypal.selion.utils.ServletHelper;

/**
 * This is simple {@link RegistryBasedServlet} servlet which displays the list of nodes connected to the grid. This
 * servlet would have to be injected into the Grid. <br>
 */
public class ListAllNodes extends RegistryBasedServlet {
    private static final long serialVersionUID = -123L;

    public static final String RESOURCE_PAGE_FILE = "/com/paypal/selion/html/listAllNodes.html";

    /**
     * Query parameter to use which instructs this servlet to ping the nodes for status.
     */
    public static final String PING_NODES = "pingNodes";

    public ListAllNodes() {
        this(null);
    }

    public ListAllNodes(GridRegistry registry) {
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
     * This method gets all the nodes which are connected to the grid machine from the Registry and displays them in
     * html page.
     *
     * @param request
     *            {@link HttpServletRequest} that represents the servlet request
     * @param response
     *            {@link HttpServletResponse} that represents the servlet response
     * @throws IOException
     */
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean doStatusQuery = request.getParameter(PING_NODES) != null;
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.equalsIgnoreCase("application/json")) {
            ServletHelper.respondAsJsonWithHttpStatus(response, getProxyInfo(doStatusQuery), HttpServletResponse.SC_OK);
        } else {
            ServletHelper.respondAsHtmlUsingJsonAndTemplateWithHttpStatus(response, getProxyInfo(doStatusQuery),
                    RESOURCE_PAGE_FILE,
                    HttpServletResponse.SC_OK);
        }
    }

    private List<ProxyInfo> getProxyInfo(boolean doStatusQuery) {
        List<ProxyInfo> nodes = new ArrayList<>();
        ProxySet proxies = this.getRegistry().getAllProxies();
        Iterator<RemoteProxy> iterator = proxies.iterator();
        while (iterator.hasNext()) {
            RemoteProxy proxy = iterator.next();
            nodes.add(new ProxyInfo(proxy, doStatusQuery));
        }
        return nodes;
    }
}
