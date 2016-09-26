/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.proxy.SeLionSauceProxy;
import com.paypal.selion.utils.SauceLabsRestApi;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.GridConfigurationException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.internal.HttpClientFactory;

import com.paypal.selion.utils.ServletHelper;

/**
 * This {@link RegistryBasedServlet} based servlet takes care of spinning up or tearing down a virtual sauce node.<br>
 * <br>
 * This servlet requires the hub to also have {@link LoginServlet} available.
 */
public class SauceServlet extends RegistryBasedServlet {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SauceServlet.class);
    private static final long serialVersionUID = 9187677490975386050L;

    private static final String MAX_INSTANCES_CONFIG_PROPERTY = "maxInstances";
    private static final String SAUCELABS_BROWSER_NAME = "saucelabs";
    private static final String PROXY_HOST = "ondemand.saucelabs.com";
    private static final int PROXY_PORT = 80;

    /**
     * The proxy id that will be used to register to the hub
     */
    public static final String PROXY_ID = "http://" + PROXY_HOST + ":" + PROXY_PORT;

    /**
     * Request parameter that trigger a proxy shutdown action
     */
    public static final String SHUTDOWN_PARAM = "shutdown";

    private boolean registered;

    public SauceServlet(Registry registry) {
        super(registry);
    }

    public SauceServlet() {
        this(null);
    }

    private String formatForHtmlTemplate(String message) {
        return String.format("<p align='center'><b>%s</b></p>", message);
    }

    @Override
    protected Registry getRegistry() {
        // ensure the Registry returned reflects the hub state.
        final Registry localRegistry = super.getRegistry();
        final Registry hubRegistry = localRegistry.getHub().getRegistry();
        // yes, we only care if they are the same object reference.
        return (localRegistry.equals(hubRegistry)) ? localRegistry : hubRegistry;
    }

    /*
     * Disconnects the virtual node from the hub
     */
    private synchronized void disconnectVirtualSauceNodeFromGrid(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        // Redirecting to login page if session is not found
        if (req.getSession(false) == null) {
            resp.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }

        String msg = "There is no sauce node running.";
        final SeLionSauceProxy proxy = (SeLionSauceProxy) getRegistry().getProxyById(PROXY_ID);
        if (proxy != null) {
            proxy.teardown();
            getRegistry().removeIfPresent(proxy);
            msg = "Sauce node shutdown successfully.";
        }

        registered = false;
        LOGGER.info(msg);
        ServletHelper.respondAsHtmlWithMessage(resp, formatForHtmlTemplate(msg));
    }

    /*
     * A helper method that takes care of registering a virtual node to the hub.
     */
    private synchronized void registerVirtualSauceNodeToGrid(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Redirecting to login page if session is not found
        if (req.getSession(false) == null) {
            resp.sendRedirect(LoginServlet.class.getSimpleName());
            return;
        }

        String respMsg = "Sauce node already registered.";
        if (registered) {
            ServletHelper.respondAsHtmlWithMessage(resp, formatForHtmlTemplate(respMsg));
            LOGGER.info(respMsg);
            return;
        }

        HttpClientFactory httpClientFactory = new HttpClientFactory();
        respMsg = "Sauce node registration failed. Please refer to the log file for failure details.";
        try {
            final int port = getRegistry().getHub().getConfiguration().port;
            final URL registration = new URL("http://localhost:" + port + "/grid/register");

            BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST",
                    registration.toExternalForm());
            request.setEntity(new StringEntity(getRegistrationRequestEntity()));
            HttpHost host = new HttpHost(registration.getHost(), registration.getPort());
            HttpClient client = httpClientFactory.getHttpClient();
            HttpResponse response = client.execute(host, request);

            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                respMsg = "Sauce node registered successfully.";
                registered = true;
            }
        } catch (IOException | GridConfigurationException e) { // We catch the GridConfigurationException here to fail
                                                               // gracefully
            // TODO Consider retrying on failure
            LOGGER.log(Level.WARNING, "Unable to register sauce node: ", e);
        } finally {
            httpClientFactory.close();
        }
        LOGGER.info(respMsg);
        ServletHelper.respondAsHtmlWithMessage(resp, formatForHtmlTemplate(respMsg));
    }

    /*
     * Update the registration request entity
     */
    private String getRegistrationRequestEntity() throws FileNotFoundException {
        // update the registration request with the max concurrent sessions/vms
        final GridNodeConfiguration gnc =
                GridNodeConfiguration.loadFromJSON(SeLionGridConstants.NODE_SAUCE_CONFIG_FILE);

        // get the max concurrent vm's allowed for the account from sauce labs
        final SauceLabsRestApi restApi;
        try {
            restApi = new SauceLabsRestApi();
        } catch (GridConfigurationException e) {
            throw e;
        }

        final int maxConcurrent = restApi.getMaxConcurrency();
        if (maxConcurrent != -1) {
            // update max sessions
            gnc.maxSession = maxConcurrent;

            // update browser max instances for all saucelabs "browser" types
            for (DesiredCapabilities caps : gnc.capabilities) {
                if (caps.getBrowserName().equals(SAUCELABS_BROWSER_NAME)) {
                    caps.setCapability(MAX_INSTANCES_CONFIG_PROPERTY, maxConcurrent);
                }
            }
        }

        // ensure the proxy host, port, id is set for http://ondemand.saucelabs.com:80
        if (StringUtils.isBlank(gnc.host)) {
            gnc.host = PROXY_HOST;
        }
        if (StringUtils.isBlank(gnc.id)) {
            gnc.id = PROXY_ID;
        }
        if (gnc.port == null || gnc.port < 0) {
            gnc.port = PROXY_PORT;
        }

        return new RegistrationRequest(gnc, SeLionSauceProxy.class.getSimpleName(), 
                "SeLion Grid Virtual Sauce Proxy").toJson().toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter(SHUTDOWN_PARAM) != null) {
            disconnectVirtualSauceNodeFromGrid(request, response);
            return;
        }
        registerVirtualSauceNodeToGrid(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if (request.getParameter(SHUTDOWN_PARAM) != null) {
            disconnectVirtualSauceNodeFromGrid(request, response);
            return;
        }
        registerVirtualSauceNodeToGrid(request, response);
    }

}