/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

package com.paypal.selion.internal.platform.grid.browsercapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;

import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.internal.platform.grid.BrowserFlavors;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.internal.platform.grid.WebTestSession;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.EventListener;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.support.events.ElementEventListener;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This utility class is internally used by SeLion framework for driver factory operations.
 *
 */
final class DriverFactoryHelper {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    private DriverFactoryHelper() {
    }

    static String showCapabilities(DesiredCapabilities dc) {
        logger.entering(dc);
        StringBuilder capabilitiesAsString = new StringBuilder();
        Map<String, ?> capabilityMap = dc.asMap();
        capabilityMap.entrySet();

        for (Entry<String, ?> eachEntry : capabilityMap.entrySet()) {
            String key = eachEntry.getKey();
            capabilitiesAsString.append(key).append(":");
            if (!key.toLowerCase().contains("profile")) {
                capabilitiesAsString.append(eachEntry.getValue());
            } else {
                // Only for a firefox profile we are resorting to displaying the profile name alone
                // instead of the actual string since it can pollute our logs.
                capabilitiesAsString.append(ConfigManager.getConfig(Grid.getTestSession().getXmlTestName())
                        .getConfigProperty(ConfigProperty.SELENIUM_FIREFOX_PROFILE));
            }
            capabilitiesAsString.append(",");

        }
        // Not logging the return value, because this is the value that will be printed by createInstance
        // If the return value also gets logged, then user will see this information twice in the log files.
        logger.exiting();
        return capabilitiesAsString.toString();
    }

    static List<EventListener> getSeLionEventListeners() {
        List<Object> configuredListeners = Config
                .getListConfigProperty(ConfigProperty.SELENIUM_WEBDRIVER_EVENT_LISTENER);

        List<EventListener> eventListeners = new ArrayList<EventListener>();
        if (configuredListeners == null || configuredListeners.size() == 0) {
            return eventListeners;
        }

        for (Object configuredListener : configuredListeners) {
            // it is possible to get a List of { "", "   ", } depending on what the user specified.
            String listenerString = (String) configuredListener;
            if (StringUtils.isBlank(listenerString)) {
                continue;
            }

            try {
                Class<?> listener = Class.forName(listenerString);
                if (EventListener.class.isAssignableFrom(listener)) {
                    eventListeners.add((EventListener) listener.newInstance());
                    logger.info("Registered [" + listenerString + "] as a SeLion event listener.");
                } else {
                    logger.info("Skipping [" + listenerString + "] because it is not a subclass of "
                            + EventListener.class.getCanonicalName());
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                logger.warning("Unable to register [" + listenerString + "] as a SeLion event listener.");
            }
        }

        return eventListeners;
    }

    static void registerElementEventListeners() {
        List<Object> configuredListeners = Config.getListConfigProperty(ConfigProperty.ELEMENT_EVENT_LISTENER);

        if (configuredListeners == null || configuredListeners.size() == 0) {
            return;
        }

        for (Object configuredListener : configuredListeners) {
            // it is possible to get a List of { "", "   ", } depending on what the user specified.
            String listenerString = (String) configuredListener;
            if (StringUtils.isBlank(listenerString)) {
                continue;
            }

            try {
                Class<?> listener = Class.forName(listenerString);
                if (ElementEventListener.class.isAssignableFrom(listener)) {
                    Grid.getTestSession().getElementEventListeners().add((ElementEventListener) listener.newInstance());
                    logger.info("Registered [" + listenerString + "] as a SeLion element event listener.");
                } else {
                    logger.info("Skipping [" + listenerString + "] because it is not a subclass of "
                            + ElementEventListener.class.getCanonicalName());
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                logger.warning("Unable to register [" + listenerString + "] as a SeLion element event listener.");
            }
        }
    }

    static URL getURL() {

        URL url = null;
        String hostToRun = Config.getConfigProperty(ConfigProperty.SELENIUM_HOST);
        String protocol = Config.getConfigProperty(ConfigProperty.SELENIUM_PROTOCOL);

        boolean runLocally = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY));
        String port = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);

        if (runLocally) {
            hostToRun = "localhost";
        }
        try {
            if (StringUtils.isEmpty(hostToRun)) {
                String errMsg = "You must have a Selenium host configured when running remotely. "
                        + "You may provide it either via the JVM argument -DSELION_SELENIUM_HOST (or) "
                        + "via the TestNG suite file parameter : <parameter name=\"seleniumhost\" value=\"\" />";
                throw new IllegalStateException(errMsg);
            }
            url = new URL(protocol + "://" + hostToRun + ":" + port + "/wd/hub");
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return url;
    }

    static void setWindowSize(WebDriver driver) {
        WebTestSession config = Grid.getWebTestSession();
        if (config == null) {
            return;
        }
        if (config.getBrowserHeight() > 0 && config.getBrowserWidth() > 0) {
            int height = config.getBrowserHeight();
            int width = config.getBrowserWidth();
            driver.manage().window().setSize(new Dimension(width, height));
        } else if (config.getBrowserHeight() < 0 || config.getBrowserWidth() < 0) {
            throw (new IllegalArgumentException(
                    "Both the height and the width of the browser window should be greater than zero"));
        }
    }

    static void printDebugInfoForUser(RemoteWebDriver driver) {
        Properties props = new Properties();
        props.put("Selenium Version", new BuildInfo().getReleaseLabel());
        props.put("Client OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));

        String currentBrowser = "UNKNOWN browser";

        if (WebDriverPlatform.WEB.equals(Grid.getTestSession().getPlatform())) {
            currentBrowser = Grid.getWebTestSession().getBrowser();
        }

        if (BrowserFlavors.isHeadLessBrowser(currentBrowser)) {
            props.put("Browser", StringUtils.capitalize(currentBrowser.substring(1)));
        } else if (WebDriverPlatform.IOS.equals(Grid.getTestSession().getPlatform())
                || (WebDriverPlatform.ANDROID.equals(Grid.getTestSession().getPlatform()))) {
            props.put("Device", Grid.getMobileTestSession().getDevice().toString());
        } else {
            String userAgent = (String) driver.executeScript("return navigator.userAgent", "");
            String browserFlavor = "UNKNOWN browser";
            if (StringUtils.isNotBlank(userAgent)) {
                browserFlavor = extractBrowserInfo(userAgent);
            }
            props.put("Browser", browserFlavor);
        }
        logger.log(Level.INFO, "Running on: " + props.toString());

    }

    private static String extractBrowserInfo(String userAgent) {

        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        ReadableUserAgent agent = parser.parse(userAgent);
        OperatingSystem os = agent.getOperatingSystem();
        StringBuilder sb = new StringBuilder();
        sb.append(agent.getName());
        sb.append(" ").append(agent.getVersionNumber().toVersionString());
        sb.append(" running on ").append(os.getName()).append(" ").append(os.getVersionNumber().toVersionString());
        return sb.toString();

    }

}
