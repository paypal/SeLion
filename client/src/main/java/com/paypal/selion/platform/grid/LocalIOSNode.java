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

package com.paypal.selion.platform.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.grid.common.exception.GridException;
import org.uiautomation.ios.IOSServer;
import org.uiautomation.ios.IOSServerConfiguration;
import org.uiautomation.ios.grid.IOSMutableRemoteProxy;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local ios driver 
 * node.
 */
class LocalIOSNode extends AbstractNode implements LocalServerComponent {
    protected boolean isRunning = false;
    private IOSServer server = null;
    private SimpleLogger logger = SeLionLogger.getLogger();

    public void shutdown() {
        if (server != null) {
            try {
                server.stop();
                logger.log(Level.INFO, "Local iOS Node has been stopped");
            } catch (Exception e) {
                String errorMsg = "An error occured while attempting to shut down the local iOS Node. Root cause: ";
                logger.log(Level.SEVERE, errorMsg, e);
            }
        }
    }

    public void boot(WebDriverPlatform platform) {
        logger.entering(platform);
        if (isRunning) {
            logger.exiting();
            return;
        }
        if (platform != WebDriverPlatform.IOS) {
            logger.exiting();
            return;
        }
        String host = "localhost";
        String hubPort = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String registrationUrl = String.format("http://%s:%s/grid/register", host, hubPort);

        try {
            int port = new LocalGridConfigFileParser().getPort() + 1;
            startIOSDriverNode(port);
            waitForNodeToComeUp(port);
            isRunning = true;
            logger.log(Level.INFO, "Attached iOSDriver node to local hub " + registrationUrl);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new GridException("Failed to start a local iOS Node", e);
        }

    }

    public void waitForNodeToComeUp(int port) {
        waitForNodeToComeUp(port, "Encountered problems when attempting to register the IOS Node to the local Grid");
    }

    private void startIOSDriverNode(int port) throws Exception {
        logger.entering(port);
        List<String> args = new ArrayList<String>();
        args.add(" -hub ");
        args.add(" http://127.0.0.1:" + Config.getIntConfigProperty(ConfigProperty.SELENIUM_PORT) + "/grid/register");
        args.add(" -port ");
        args.add(Integer.toString(port));
        String autFolder = Config.getConfigProperty(ConfigProperty.SELENIUM_NATIVE_APP_FOLDER);
        if (autFolder != null && !autFolder.trim().isEmpty()) {
            args.add(" -folder ");
            args.add(autFolder);
        }
        args.add(" -proxy ");
        args.add(IOSMutableRemoteProxy.class.getCanonicalName());
        args.add(" -host ");
        args.add(" 127.0.0.1 ");
        Handler[] handlers = Logger.getLogger("").getHandlers();
        Level level = Logger.getLogger("").getLevel();

        IOSServerConfiguration config = IOSServerConfiguration.create(args.toArray(new String[args.size()]));
        server = new IOSServer(config);
        
        // HACK :: put the RootLogger back into the original state
        // remove all handlers first
        for (Handler handler : Logger.getLogger("").getHandlers()) {
            Logger.getLogger("").removeHandler(handler);
        }
        // put the original ones back
        for (Handler handler : handlers) {
            Logger.getLogger("").addHandler(handler);
        }
        // reset the log level
        Logger.getLogger("").setLevel(level);

        server.start();
        logger.exiting();
    }

}
