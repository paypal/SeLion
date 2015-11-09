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

package com.paypal.selion.internal.platform.grid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.os.CommandLine;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.grid.LauncherOptions;
import com.paypal.selion.grid.ThreadedLauncher;
import com.paypal.selion.grid.LauncherOptions.LauncherOptionsImpl;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local node.
 */
final class LocalNode extends AbstractBaseLocalServerComponent {
    private static final SimpleLogger LOGGER = SeLionLogger.getLogger();
    private static volatile LocalNode instance;

    static synchronized final LocalServerComponent getSingleton() {
        if (instance == null) {
            return new LocalNode().getLocalServerComponent();
        }
        return instance;
    }

    synchronized final LocalNode getLocalServerComponent() {
        if (instance == null) {
            instance = new LocalNode();

            instance.setHost(new NetworkUtils().getIpOfLoopBackIp4());
            instance.setPort(PortProber.findFreePort());

            String hubPort = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
            String hub = String.format("http://%s:%s/grid/register", instance.getHost(), hubPort);

            LauncherOptions launcherOptions = new LauncherOptionsImpl()
                    .setFileDownloadCheckTimeStampOnInvocation(false).setFileDownloadCleanupOnInvocation(false);

            List<String> downloadList = determineListOfDownloadsToProcess();

            instance.setLauncher(new ThreadedLauncher(new String[] { "-role", "node", "-port",
                    String.valueOf(instance.getPort()), "-proxy", DefaultRemoteProxy.class.getName(), "-host",
                    instance.getHost(), "-hub", hub }, launcherOptions, downloadList));
        }
        return instance;
    }

    @Override
    public void boot(AbstractTestSession testSession) {
        LOGGER.entering(testSession.getPlatform());
        if (!(testSession instanceof WebTestSession)) {
            return;
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

    /*
     * Based on platform type and current Config, determine whether dependent binaries are in place. Otherwise, add them
     * to the list of things to download.
     */
    private List<String> determineListOfDownloadsToProcess() {
        List<String> list = new ArrayList<>();

        if (!Config.getBoolConfigProperty(ConfigProperty.DOWNLOAD_DEPENDENCIES)) {
            return list;
        }

        // for IEDriver
        if (SystemUtils.IS_OS_WINDOWS) {
            if (!checkForPresenceOf(ConfigProperty.SELENIUM_IEDRIVER_PATH,
                    SeLionConstants.WEBDRIVER_IE_DRIVER_PROPERTY, SeLionConstants.IE_DRIVER)) {
                Config.setConfigProperty(ConfigProperty.SELENIUM_IEDRIVER_PATH, SeLionConstants.SELION_HOME_DIR
                        + SeLionConstants.IE_DRIVER);
                list.add("iedriver");
            }

        }

        // for chromedriver
        if (!checkForPresenceOf(ConfigProperty.SELENIUM_CHROMEDRIVER_PATH,
                SeLionConstants.WEBDRIVER_CHROME_DRIVER_PROPERTY, SeLionConstants.CHROME_DRIVER)) {
            Config.setConfigProperty(ConfigProperty.SELENIUM_CHROMEDRIVER_PATH, SeLionConstants.SELION_HOME_DIR
                    + SeLionConstants.CHROME_DRIVER);
            list.add("chromedriver");
        }

        // for phantomjs
        if (!checkForPresenceOf(ConfigProperty.SELENIUM_PHANTOMJS_PATH,
                SeLionConstants.WEBDRIVER_PHANTOMJS_DRIVER_PROPERTY, SeLionConstants.PHANTOMJS_DRIVER)) {
            Config.setConfigProperty(ConfigProperty.SELENIUM_PHANTOMJS_PATH, SeLionConstants.SELION_HOME_DIR
                    + SeLionConstants.PHANTOMJS_DRIVER);
            list.add("phantomjs");
        }

        return list;
    }

    /**
     * Return true when one of the following conditions is met <br>
     * <br>
     * 1. ConfigProperty for driverBinary is specified and not blank or null. <br>
     * 2. System Property which Selenium uses to find driverBinary is present. <br>
     * 3. driverBinary exists in the current working directory OR the PATH <br>
     */
    private boolean checkForPresenceOf(ConfigProperty property, String systemProperty, String driverBinary) {
        if (StringUtils.isBlank(Config.getConfigProperty(property)) && System.getProperty(systemProperty) == null) {
            // check the CWD and PATH for the driverBinary
            @SuppressWarnings("deprecation")
            String location = CommandLine.find(driverBinary.replace(".exe", ""));
            return (location != null);
        }
        return true;
    }
}
