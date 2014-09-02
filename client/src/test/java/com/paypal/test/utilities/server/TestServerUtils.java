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

package com.paypal.test.utilities.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;

import com.paypal.selion.logger.SeLionLogger;

public class TestServerUtils {

    static int serverPort = 0;
    static String localIP;

    private static String DATE_PICKER_FILE = "/datePicker.html";
    private static String CONTAINER_FILE = "/ContainerTest.html";
    private static String TEST_EDITABLE_FILE = "/test_editable.html";

    public static final String TEST_PAGE_DIR = "src/test/resources/testPages";

    static Server server;

    private static void createServer() {
        serverPort = PortProber.findFreePort();
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
