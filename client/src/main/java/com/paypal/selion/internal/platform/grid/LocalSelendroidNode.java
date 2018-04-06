/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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

package com.paypal.selion.internal.platform.grid;

import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;

import com.google.common.annotations.Beta;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.grid.ProcessLauncherConfiguration;
import com.paypal.selion.grid.ProcessLauncherOptions;
import com.paypal.selion.grid.SelendroidJarSpawner;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local selendroid node.
 */
@Beta
@Deprecated
final class LocalSelendroidNode extends AbstractBaseLocalServerComponent {
    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();
    private static volatile LocalSelendroidNode instance;

    static synchronized LocalServerComponent getSingleton() {
        if (instance == null) {
            instance = new LocalSelendroidNode().getLocalServerComponent();
        }
        return instance;
    }

    synchronized LocalSelendroidNode getLocalServerComponent() {
        if (instance == null) {
            instance = new LocalSelendroidNode();

            instance.setHost(new NetworkUtils().getIpOfLoopBackIp4());
            instance.setPort(PortProber.findFreePort());

            String hubPort = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
            String hub = String.format("http://%s:%s/grid/register", instance.getHost(), hubPort);

            String[] folder = new String[] { "", "" };
            String autFolder = Config.getConfigProperty(ConfigProperty.MOBILE_APP_FOLDER);
            if (StringUtils.isNotEmpty(autFolder)) {
                folder = new String[] { "-folder", autFolder };
            }

            String forceReinstall = "";
            if (Config.getBoolConfigProperty(ConfigProperty.SELENDROID_SERVER_FORCE_REINSTALL)) {
                forceReinstall = ("-forceReinstall");
            }

            ProcessLauncherOptions processOptions = new ProcessLauncherConfiguration().setContinuouslyRestart(false)
                    .setIncludeJarsInPresentWorkingDir(false).setIncludeParentProcessClassPath(false)
                    .setIncludeJavaSystemProperties(false).setFileDownloadCheckTimeStampOnInvocation(false)
                    .setFileDownloadCleanupOnInvocation(false);

            instance.setLauncher(new SelendroidJarSpawner(new String[] {
                    "-port", String.valueOf(instance.getPort()),
                    "-host", instance.getHost(),
                    "-hub", hub,
                    folder[0], folder[1],
                    "-selendroidServerPort", Config.getConfigProperty(ConfigProperty.SELENDROID_SERVER_PORT),
                    "-timeoutEmulatorStart",
                    Config.getConfigProperty(ConfigProperty.SELENDROID_EMULATOR_START_TIMEOUT),
                    "-serverStartTimeout", Config.getConfigProperty(ConfigProperty.SELENDROID_SERVER_START_TIMEOUT),
                    forceReinstall,
                    "-sessionTimeout", Config.getConfigProperty(ConfigProperty.MOBILE_DRIVER_SESSION_TIMEOUT) },
                    processOptions));

        }
        return instance;
    }

    @Override
    public void boot(AbstractTestSession testSession) {
        LOGGER.entering();

        // don't allow non-mobile test case to spawn the ios-driver node
        if ((testSession.getPlatform() != WebDriverPlatform.ANDROID) && !(testSession instanceof MobileTestSession)) {
            return;
        }
        // don't allow an appium test case to spawn the ios-driver node
        if (((MobileTestSession) testSession).getMobileNodeType() != MobileNodeType.SELENDROID) {
            return;
        }

        try {
            validateConfiguredOptions();
        } catch (IllegalArgumentException e) {
            throw e;
        }

        if (instance == null) {
            getLocalServerComponent();
        }
        super.boot(testSession);
        LOGGER.exiting();
    }

    @Override
    public void shutdown() {
        LOGGER.entering();
        if (instance == null) {
            LOGGER.exiting();
            return;
        }
        super.shutdown();
        LOGGER.exiting();
    }

    private void validateConfiguredOptions() {
        // Make sure the configured internal selendroid server port is not already in use
        int selendroidServerPort = Config.getIntConfigProperty(ConfigProperty.SELENDROID_SERVER_PORT);
        checkPort(selendroidServerPort, "for selendroid server");

        try {
            checkAndValidateParameters(ConfigProperty.SELENDROID_EMULATOR_START_TIMEOUT);
            checkAndValidateParameters(ConfigProperty.SELENDROID_SERVER_START_TIMEOUT);
            checkAndValidateParameters(ConfigProperty.MOBILE_DRIVER_SESSION_TIMEOUT);
        } catch (Exception e) { // NO SONAR
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /*
     * Checks the presence of selendroid specific parameters provided by the user and validates them.
     * IllegalArgumentException is thrown if the parameter is either insufficient or irrelevant. Throws a
     * NullPointerException if the received configProperty is null.
     *
     * @param configProperty a SeLion {@link ConfigProperty} to validate
     */
    private void checkAndValidateParameters(ConfigProperty configProperty) {
        LOGGER.entering(configProperty);
        try {
            switch (configProperty) {
            case SELENDROID_SERVER_START_TIMEOUT:
            case SELENDROID_EMULATOR_START_TIMEOUT: {
                // Selendroid takes timeoutEmulatorStart/serverStartTimeout in milliseconds.
                Config.getIntConfigProperty(configProperty);
                break;
            }
            case MOBILE_DRIVER_SESSION_TIMEOUT: {
                // Selendroid takes sessionTimeout in seconds.
                int receivedValue = Config.getIntConfigProperty(configProperty) / 1000;
                if (receivedValue == 0) {
                    String errorMessage = "Insufficient value received for configuration property "
                            + configProperty.getName() + ", probably value is less than 1000 milliseconds.";
                    throw new IllegalArgumentException(errorMessage);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException(
                        "Invalid selendroid configuration received for validation, configuration property = "
                                + configProperty.getName());
            }
            }
        } catch (ConversionException exe) {
            String errorMessage = "Invalid data received for configuration property " + configProperty.getName()
                    + ", probably not an integer for milliseconds.";
            throw new IllegalArgumentException(errorMessage, exe);
        }

        LOGGER.exiting();
    }
}
