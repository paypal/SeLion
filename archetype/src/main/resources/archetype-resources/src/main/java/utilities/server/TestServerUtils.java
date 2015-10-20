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

package ${package}.utilities.server;

import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.server.handler.ResourceHandler;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;

import ${package}.logging.AppLogger;

/**
 * Leverages Jetty to create a locally running HTTP server. The server will bind to a dynamically available port.
 * Once bound, it is used by the example test classes for performing Web interactions.
 * 
 * Note: This server does not bind to any public IP address.
 */
public class TestServerUtils {

    static int serverPort;
    static String localIP;

    private static String TEST_APP_FILE = "/testapp.html";

    public static final String TEST_PAGE_DIR = "src/test/resources/testPages";

    static Server server;

    private static void createServer() {
        serverPort = PortProber.findFreePort();
        localIP = new NetworkUtils().getPrivateLocalAddress();
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
        AppLogger.getLogger().info(String.format("Loading page from [%s]", url));
    }

    public static String getAppURL() {
        String url = TestServerUtils.getBaseURL() + TEST_APP_FILE;
        logURLToConsole(url);
        return url;
    }
}
