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

import org.apache.commons.configuration.ConversionException;
import org.openqa.grid.common.exception.GridException;
import org.uiautomation.ios.IOSServer;
import org.uiautomation.ios.IOSServerConfiguration;
import org.uiautomation.ios.grid.IOSMutableRemoteProxy;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local ios driver node.
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

    public void boot(AbstractTestSession testSession) {
        logger.entering(testSession.getPlatform());

        // don't allow non-mobile test case to spawn the ios-driver node
        if ((testSession.getPlatform() != WebDriverPlatform.IOS) && !(testSession instanceof MobileTestSession)) {
            return;
        }
        // don't allow an appium test case to spawn the ios-driver node
        if (((MobileTestSession) testSession).getMobileNodeType() != MobileNodeType.IOS_DRIVER) {
            return;
        }

        if (isRunning) {
            logger.exiting();
            return;
        }
        String host = "localhost";
        String hubPort = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String registrationUrl = String.format("http://%s:%s/grid/register", host, hubPort);

        try {
            int port = new LocalGridConfigFileParser().getPort() + 1;
            startIOSDriverNode(port);
            waitForNodeToComeUp(port, "Encountered problems when attempting to register the IOS Node to the local Grid");
            isRunning = true;
            logger.log(Level.INFO, "Attached iOSDriver node to local hub " + registrationUrl);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new GridException("Failed to start a local iOS Node", e);
        }
    }

    private void startIOSDriverNode(int port) throws Exception {
        logger.entering(port);
        List<String> args = new ArrayList<String>();
        args.add(" -hub ");
        args.add(" http://127.0.0.1:" + Config.getIntConfigProperty(ConfigProperty.SELENIUM_PORT) + "/grid/register");
        args.add(" -port ");
        args.add(Integer.toString(port));
        String autFolder = Config.getConfigProperty(ConfigProperty.MOBILE_APP_FOLDER);
        if (autFolder != null && !autFolder.trim().isEmpty()) {
            args.add(" -folder ");
            args.add(autFolder);
        }
        String newSessionTimeoutSec = checkAndValidateParameters(ConfigProperty.IOSDRIVER_NEWSESSION_TIMEOUT);
        args.add(" -newSessionTimeoutSec ");
        args.add(newSessionTimeoutSec);
        String sessionTimeout = checkAndValidateParameters(ConfigProperty.MOBILE_DRIVER_SESSION_TIMEOUT);
        args.add(" -sessionTimeout ");
        args.add(sessionTimeout);
        String maxIdleBetweenCommands = checkAndValidateParameters(ConfigProperty.IOSDRIVER_COMMAND_INTERVAL);
        args.add(" -maxIdleBetweenCommands ");
        args.add(maxIdleBetweenCommands);
        args.add(" -proxy ");
        args.add(IOSMutableRemoteProxy.class.getCanonicalName());
        args.add(" -host ");
        args.add(" 127.0.0.1 ");
        Handler[] handlers = Logger.getLogger("").getHandlers();
        Level level = Logger.getLogger("").getLevel();

        // add an option to skip ios default logger
        args.add(" -skipLoggerConfiguration");
        args.add(Boolean.TRUE.toString());

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

    private String checkAndValidateParameters(ConfigProperty configProperty) {

        // Checks the presence of ios-driver specific parameters provided by the user and validates them.
        // IllegalArgumentException is thrown if the parameter is either insufficient or irrelevant. Throws a
        // NullPointerException if the received configProperty is null.
        logger.entering(configProperty);
        String validatedValue = null;
        switch (configProperty) {
        case IOSDRIVER_NEWSESSION_TIMEOUT:
        case MOBILE_DRIVER_SESSION_TIMEOUT:
        case IOSDRIVER_COMMAND_INTERVAL:
            try {
                int receivedValue = Config.getIntConfigProperty(configProperty) / 1000;
                if (receivedValue == 0) {
                    String errorMessage = "Insufficient value received for configuration property "
                            + configProperty.getName() + ", probably value is less than 1000 milliseconds.";
                    throw new IllegalArgumentException(errorMessage);
                } else {
                    validatedValue = String.valueOf(receivedValue);
                }
            } catch (ConversionException exe) {
                String errorMessage = "Invalid data received for configuration property " + configProperty.getName()
                        + ", probably not an integer for milliseconds.";
                throw new IllegalArgumentException(errorMessage, exe);
            }
            break;
        default:
            throw new IllegalArgumentException(
                    "Invalid ios-server configuration received for validation, configuration property = "
                            + configProperty.getName());
        }
        logger.exiting(validatedValue);
        return validatedValue;
    }

}
