/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 eBay Software Foundation                                                                   |
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.openqa.grid.common.exception.GridException;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;

import com.google.common.annotations.Beta;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.grid.IOSDriverJarSpawner;
import com.paypal.selion.grid.ProcessLauncherOptions;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local ios driver node.
 */
@Beta
class LocalIOSNode extends BaseNode implements LocalServerComponent {
    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();
    private static volatile LocalIOSNode instance;
    private int port;
    private boolean isRunning = false;
    private IOSDriverJarSpawner launcher;
    private ExecutorService executor;
    private String host;

    static synchronized LocalIOSNode getInstance() {
        if (instance == null) {
            instance = new LocalIOSNode();

            instance.host = new NetworkUtils().getIpOfLoopBackIp4();
            instance.port = PortProber.findFreePort();

            try {
                String hubPort = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
                String hub = String.format("http://%s:%s/grid/register", instance.host, hubPort);

                String[] folder = new String[] { "", "" };
                String autFolder = Config.getConfigProperty(ConfigProperty.MOBILE_APP_FOLDER);
                if (StringUtils.isNotEmpty(autFolder)) {
                    folder = new String[] { "-folder", autFolder };
                }

                ProcessLauncherOptions processOptions = new ProcessLauncherOptions().setContinuouslyRestart(false)
                        .setIncludeJarsInPresentWorkingDir(false).setIncludeParentProcessClassPath(false)
                        .setIncludeJavaSystemProperties(false);

                instance.launcher = new IOSDriverJarSpawner(new String[] {
                        "-port", String.valueOf(instance.port),
                        "-host", instance.host,
                        "-hub", hub,
                        folder[0], folder[1],
                        "-sessionTimeout", checkAndValidateParameters(ConfigProperty.MOBILE_DRIVER_SESSION_TIMEOUT) },
                        processOptions);

            } catch (IllegalArgumentException e) {
                // TODO refactor #checkAndValidateParameters to fallback on the default value rather than throw this
                // exception
            }
        }
        return instance;
    }

    public synchronized void shutdown() {
        if (!getInstance().isRunning) {
            return;
        }

        if (getInstance().executor != null) {
            try {
                getInstance().launcher.shutdown();
                getInstance().executor.shutdownNow();
                while (!getInstance().executor.isTerminated()) {
                    getInstance().executor.awaitTermination(30, TimeUnit.SECONDS);
                }
                getInstance().isRunning = false;
                LOGGER.info("Local ios-driver node has been stopped");
            } catch (Exception e) { // NOSONAR
                String errorMsg = "An error occurred while attempting to shut down the ios-driver local Node.";
                LOGGER.log(Level.SEVERE, errorMsg, e);
            }
        }
    }

    public synchronized void boot(AbstractTestSession testSession) {
        LOGGER.entering(testSession.getPlatform());

        // don't allow non-mobile test case to spawn the ios-driver node
        if ((testSession.getPlatform() != WebDriverPlatform.IOS) && !(testSession instanceof MobileTestSession)) {
            return;
        }
        // don't allow an appium test case to spawn the ios-driver node
        if (((MobileTestSession) testSession).getMobileNodeType() != MobileNodeType.IOS_DRIVER) {
            return;
        }

        if (getInstance().isRunning) {
            LOGGER.exiting();
            return;
        }

        getInstance().executor = Executors.newSingleThreadExecutor();
        Runnable worker = getInstance().launcher;
        try {
            getInstance().executor.execute(worker);
            waitForNodeToComeUp(getPort(),
                    "Unable to contact Node after 60 seconds.");
            getInstance().isRunning = true;
            LOGGER.log(Level.INFO, "Local ios-driver Node spawned");
        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GridException("Failed to start a local ios-driver Node", e);
        }
    }

    /**
     * Checks the presence of ios-driver specific parameters provided by the user and validates them.
     * IllegalArgumentException is thrown if the parameter is either insufficient or irrelevant. Throws a
     * NullPointerException if the received configProperty is null.
     * 
     * @param configProperty
     *            a SeLion {@link ConfigProperty} to validate
     */
    private static String checkAndValidateParameters(ConfigProperty configProperty) {
        LOGGER.entering(configProperty);
        String validatedValue = null;
        switch (configProperty) {
        case MOBILE_DRIVER_SESSION_TIMEOUT:
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
        LOGGER.exiting(validatedValue);
        return validatedValue;
    }

    int getPort() {
        return getInstance().port;
    }

}
