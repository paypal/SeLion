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

package com.paypal.selion;

import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;
import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.server.handler.ResourceHandler;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;

public class TestServerUtils {
    public static final String TEST_PAGE_DIR = "src/test/resources/testPages";

    private static final String DATE_PICKER_FILE = "/datePicker.html";
    private static final String CONTAINER_FILE = "/ContainerTest.html";
    private static final String TEST_EDITABLE_FILE = "/test_editable.html";
    
    private static final int[] sauceConnectPorts = { 2000, 2001, 2020, 2109, 2222, 2310, 3000, 3001, 3030, 3210, 3333, 4000,
            4001, 4040, 4321, 4502, 4503, 4567, 5000, 5001, 5050, 5432, 6000, 6001, 6060, 6666, 6543, 7000, 7070,
            7774, 7777, 8000, 8001, 8003, 8031, 8080, 8081, 8765, 8777, 8888, 9000, 9001, 9080, 9090, 9876, 9877, 9999,
            49221, 55001 };
    private static final int lowerBoundConnectPort = 10000;
    private static final int upperBoundConnectPort = 14000;

    private static int serverPort;
    private static String localIP;
    private static Server server;

    private static void createServer() {
        if (! Config.getBoolConfigProperty(ConfigProperty.SELENIUM_USE_SAUCELAB_GRID)) {
            for (int port = lowerBoundConnectPort; port <= upperBoundConnectPort; port++) {
                if (PortProber.pollPort(port)) {
                    serverPort = port;
                    break;
                }
            }
        }
        else {
            //use a port Sauce Connect can reach
            for (int port : sauceConnectPorts) {
                if (PortProber.pollPort(port)) {
                    serverPort = port;
                    break;
                }
            }
        }
        //last hope, any free port will have to do
        if (serverPort == 0) {
            serverPort = PortProber.findFreePort();
        }

        localIP = new NetworkUtils().getIp4NonLoopbackAddressOfThisMachine().getHostAddress();
        initServer();
    }

    private static void initServer() {
        server = new Server(serverPort);
        ResourceHandler handler = new ResourceHandler();
        handler.setDirectoriesListed(true);
        handler.setResourceBase(TEST_PAGE_DIR);
        server.setHandler(handler);
    }

    public static void startServer() throws Exception {
        if (server == null) {
            createServer();
        }
        if (!server.isRunning()) {
            server.start();
        }
    }

    public static void stopServer() throws Exception {
        if (server.isRunning()) {
            server.stop();
        }
    }

    private static String getBaseURL() {
        if (server == null) {
            throw new IllegalStateException("The server was never started. Please invoke startServer() first");
        }

        return "http://" + localIP + ":" + serverPort;
    }

    private static void logURLToConsole(String url) {
        SeLionLogger.getLogger().info(String.format("Loading page from [%s]", url));
    }

    public static String getDatePickerURL() {
        String url = TestServerUtils.getBaseURL() + DATE_PICKER_FILE;
        logURLToConsole(url);
        return url;
    }

    public static String getTestEditableURL() {
        String url = TestServerUtils.getBaseURL() + TEST_EDITABLE_FILE;
        logURLToConsole(url);
        return url;
    }

    public static String getContainerURL() {
        String url = TestServerUtils.getBaseURL() + CONTAINER_FILE;
        logURLToConsole(url);
        return url;
    }
}
