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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.google.common.io.ByteStreams;
import com.paypal.selion.node.servlets.LogServlet;
import com.paypal.selion.utils.FileBackedStringBuffer;

/**
 * This is simple {@link RegistryBasedServlet} servlet which basically display the list of nodes connected to the grid.
 * This servlet would have to be injected into the Grid.
 * <br><br>
 * In addition this servlet will provide a link to view logs on the node. The node must use {@link LogServlet} or
 * handle a HTTP request to <b>/extra/LogServlet</b> for this to work.
 * 
 */
public class ListAllNodes extends RegistryBasedServlet {
    private static final long serialVersionUID = -123L;

    public ListAllNodes() {
        this(null);
    }

    public ListAllNodes(Registry registry) {
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
     * This method getS all the nodes which are connected to the grid machine from the Registry and displays them in
     * html page.
     * 
     * @param request
     *            - {@link HttpServletRequest} that represent the servlet request
     * @param response
     *            - {@link HttpServletResponse} that represent the servlet response
     * @throws IOException
     */
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);

        FileBackedStringBuffer buffer = new FileBackedStringBuffer();

        buffer.append("<html>").append("<head>").append("<title>Grid Logs Console</title>");
        buffer.append("</head>").append("<body>");

        ProxySet proxies = this.getRegistry().getAllProxies();
        Iterator<RemoteProxy> iterator = proxies.iterator();
        while (iterator.hasNext()) {
            RemoteProxy proxy = iterator.next();
            URL remoteHost = proxy.getRemoteHost();
            String nodeAddress = remoteHost + "/extra/" + LogServlet.class.getSimpleName();
            buffer.append("<br>View logs on <a href=").append(nodeAddress).append(" target=_blank>")
                    .append(remoteHost.getHost()).append("</a></br>");
        }
        buffer.append("</body></html>");
        InputStream in = new ByteArrayInputStream(buffer.toString().getBytes("UTF-8"));
        try {
            ByteStreams.copy(in, response.getOutputStream());
        } finally {
            in.close();
            response.flushBuffer();
        }
    }
}