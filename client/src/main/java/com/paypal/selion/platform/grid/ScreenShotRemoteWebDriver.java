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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.testng.Reporter;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.browsercapabilities.DesiredCapabilitiesFactory;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * An extension of {@link EventFiringWebDriver} which provides additional capabilities
 */
public final class ScreenShotRemoteWebDriver extends EventFiringWebDriver {

    private ScreenShotRemoteWebDriver(WebDriver driver) {
        super(driver);
    }

    private static SimpleLogger logger = SeLionLogger.getLogger();

    protected static String showCapabilities(DesiredCapabilities dc) {
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

    /**
     * Apart from opening up the URL in a browser, this method optionally takes care of logging in the "open" action on
     * to the test report viz., the reports generated via BFReporter.
     * 
     */
    @Override
    public void get(String url) {
        super.get(url);
        if (Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING))) {
            Reporter.log("&#8594;Loaded URL: " + url, false);
        }
        printDebugInfoForUser();
    }

    private void printDebugInfoForUser() {
        Properties props = new Properties();
        props.put("Selenium V", new org.openqa.selenium.internal.BuildInfo().getReleaseLabel());
        props.put("Client OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        String currentBrowser = Grid.getWebTestSession().getBrowser();
        if (BrowserFlavors.isHeadLessBrowser(BrowserFlavors.getBrowser(currentBrowser))) {
            props.put("Running on ", StringUtils.capitalize(currentBrowser.substring(1)));
        } else {
            String userAgent = (String) this.executeScript("return navigator.userAgent", "");
            String browserFlavor = "UNKNOWN browser";
            if (StringUtils.isNotBlank(userAgent)) {
                browserFlavor = extractBrowserInfo(userAgent);
            }
            props.put("Browser", browserFlavor);
        }
        logger.log(Level.INFO, "Running on :" + props.toString());
    }

    private String extractBrowserInfo(String userAgent) {
        Browser browser = Browser.parseUserAgentString(userAgent);
        OperatingSystem os = OperatingSystem.parseUserAgentString(userAgent);
        StringBuilder sb = new StringBuilder();
        sb.append(browser.getName());
        sb.append(" v:").append(browser.getVersion(userAgent));
        sb.append(" running on ").append(os.getName());
        return sb.toString();

    }

    public static ScreenShotRemoteWebDriver createInstance() {
        return createInstance(BrowserFlavors.getBrowser(Grid.getWebTestSession().getBrowser()));
    }

    public static ScreenShotRemoteWebDriver createInstance(BrowserFlavors browser) {
        logger.entering(browser);
        URL url = null;
        String hostToRun = Config.getConfigProperty(ConfigProperty.SELENIUM_HOST);

        boolean runLocally = Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY));
        String port = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);

        if (runLocally) {
            hostToRun = "localhost";
        }
        try {
            if (StringUtils.isEmpty(hostToRun)) {
                String errMsg = "You must have a Selenium host configured when running remotely. " +
            "You may provide it either via the JVM argument -DSELION_SELENIUM_HOST (or) " +
            "via the TestNG suite file parameter : <parameter name=\"seleniumhost\" value=\"\" />";
                throw new IllegalStateException(errMsg);
            }
            url = new URL("http://" + hostToRun + ":" + port + "/wd/hub");
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        DesiredCapabilities capability = DesiredCapabilitiesFactory.getCapabilities(browser);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Spawning a browser with the following capabilities : "
                    + showCapabilities(capability));
        }

        RemoteWebDriver driver = new RemoteWebDriver(url, capability);
        setWindowSize(driver);
        ScreenShotRemoteWebDriver wrappedInstance = new ScreenShotRemoteWebDriver(driver);
        registerWebDriverEventListeners(wrappedInstance);

        logger.exiting(driver);

        return wrappedInstance;
    }
    
    private static void registerWebDriverEventListeners(EventFiringWebDriver driver) {
        String listeners = Config.getConfigProperty(ConfigProperty.SELENIUM_WEBDRIVER_EVENT_LISTENER);
        if (StringUtils.isBlank(listeners)) {
            return;
        }

        String[] allEventListeners = listeners.split(",");
        for (String eachEventListener : allEventListeners) {
            try {
                Object listener = Class.forName(eachEventListener).newInstance();
                if (WebDriverEventListener.class.isAssignableFrom(listener.getClass())) {
                    driver.register((WebDriverEventListener) listener);
                    logger.info("Registered [" + eachEventListener + "] as a webdriver event listener.");
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                logger.warning("Unable to register [" + eachEventListener + "] as a webdriver event listener.");
            }
        }
    }

    private static void setWindowSize(WebDriver driver) {
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

}
