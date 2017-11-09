/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016-2017 PayPal                                                                                     |
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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.paypal.selion.pojos.BrowserInformationCache;
import com.paypal.selion.pojos.BrowserStatisticsCollection;
import com.paypal.selion.pojos.BrowserStatisticsCollection.BrowserStatistics;
import com.paypal.selion.utils.ServletHelper;
import com.paypal.selion.proxy.SeLionRemoteProxy;

/**
 * <code>GridStatistics</code> servlet displays the current load on the Grid per browser, i.e., the number of requests
 * waiting on the queue for a browser and the maximum instances of that browser. The servlet responds only to client
 * using accept header for all media types and accept header of type application/json. This servlet should be injected
 * into the Grid. This servlet <strong>requires</strong> the remote proxies to update the {@link BrowserInformationCache}
 * upon initialization. Any {@link SeLionRemoteProxy} performs this required update.<br>
 *
 * <pre>
 * cURL clients
 *
 * Sample requests
 * curl -s http://<domain>:<port>/grid/admin/GridStatistics
 * curl -s -X GET http://<domain>:<port>/grid/admin/GridStatistics
 * curl -s -H "Accept: application/json" -X GET http://<domain>:<port>/grid/admin/GridStatistics
 *
 * Browser clients
 *
 * Go to the URL http://<domain>:<port>/grid/admin/GridStatistics
 *
 * Sample response
 * [{
 *     "browserName": "chrome",
 *     "statistics": {
 *         "waitingRequests": 2,
 *         "maxBrowserInstances": 10
 *     }
 * },
 * {
 *     "browserName": "firefox",
 *     "statistics": {
 *         "waitingRequests": 3,
 *         "maxBrowserInstances": 15
 *     }
 * },
 * {
 *     "browserName": "internet explorer",
 *     "statistics": {
 *         "waitingRequests": 0,
 *         "maxBrowserInstances": 1
 *     }
 * }]
 * </pre>
 */
public class GridStatistics extends RegistryBasedServlet {

    /**
     * Serial Version ID
     */
    private static final long serialVersionUID = -4200130800419092658L;

    public GridStatistics() {
        this(null);
    }

    public GridStatistics(GridRegistry registry) {
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
     * This method gets a list of {@link BrowserStatistics} and returns over HTTP as a json document
     *
     * @param request
     *            {@link HttpServletRequest} that represents the servlet request
     * @param response
     *            {@link HttpServletResponse} that represents the servlet response
     * @throws IOException
     */
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && ((acceptHeader.contains("*/*")) || (acceptHeader.contains("application/json")))) {
            ServletHelper.respondAsJsonWithHttpStatus(response, getGridLoadResponse(), HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                    "The servlet can only respond to application/json or */* Accept headers");
        }
    }

    private List<BrowserStatistics> getGridLoadResponse() {
        BrowserStatisticsCollection gridStatisticsCollection = getBrowserMaxStatistics();
        updateWaitingRequests(gridStatisticsCollection);
        return gridStatisticsCollection.getBrowserStatisticsList();
    }

    private BrowserStatisticsCollection getBrowserMaxStatistics() {
        BrowserStatisticsCollection browserStatisticsCollection = new BrowserStatisticsCollection();
        BrowserInformationCache browserInformationCache = BrowserInformationCache.getInstance();
        for (String browserName : BrowserInformationCache.SUPPORTED_BROWSERS) {
            int totalBrowserCapacity = browserInformationCache.getTotalBrowserCapacity(browserName, getRegistry());
            if (totalBrowserCapacity > 0) {
                browserStatisticsCollection.setMaxBrowserInstances(browserName, totalBrowserCapacity);
            }
        }
        return browserStatisticsCollection;
    }

    private void updateWaitingRequests(BrowserStatisticsCollection gridStatistics) {
        String capabilitiesBrowserName;
        for (DesiredCapabilities waitingCapabilities : this.getRegistry().getDesiredCapabilities()) {
            capabilitiesBrowserName = waitingCapabilities.getBrowserName();
            for (String browserName : BrowserInformationCache.SUPPORTED_BROWSERS) {
                if (capabilitiesBrowserName.startsWith(browserName)) {
                    gridStatistics.incrementWaitingRequests(browserName);
                }
            }
        }
    }

}
