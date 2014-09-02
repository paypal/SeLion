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

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.selion.pojos.ProcessInfo;
import com.paypal.selion.utils.process.ProcessHandler;
import com.paypal.selion.utils.process.ProcessHandlerException;
import com.paypal.selion.utils.process.ProcessHandlerFactory;

/**
 * A simple servlet which basically issues a System.exit() when invoked. This servlet would have to be injected into the
 * node [not the Grid] so that it can help in terminating the node.
 * 
 */
public class NodeForceRestartServlet extends HttpServlet {

    private static final long serialVersionUID = -8308677302003045927L;
    private static final Logger log = Logger.getLogger(NodeForceRestartServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
        shutdownNode();
    }

    protected void shutdownNode() {
        log.warning("Shutting down the node");
        try {
            ProcessHandler handler = ProcessHandlerFactory.createInstance();
            List<ProcessInfo> process = handler.potentialProcessToBeKilled();
            handler.killProcess(process);
            log.info("Successfully killed all stalled processes");
        } catch (ProcessHandlerException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            System.exit(0);
        }
    }

}
