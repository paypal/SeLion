/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

import io.selendroid.client.SelendroidCommandExecutor;
import io.selendroid.client.SelendroidDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.browsercapabilities.DesiredCapabilitiesFactory;
import com.paypal.selion.platform.html.support.events.ElementEventListener;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This factory class is responsible for providing the framework with a {@link RemoteWebDriver} instance based on the
 * browser type.
 * 
 */
public final class DriverFactory {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    public static RemoteWebDriver createInstance() {
        return createInstance(BrowserFlavors.getBrowser(Grid.getWebTestSession().getBrowser()));
    }

    /**
     * @param browser
     *            - enum that represents the browser flavor for which capabilities are being requested.
     * @return A {@link RemoteWebDriver} instance based on the browser type
     */
    public static RemoteWebDriver createInstance(BrowserFlavors browser) {

        DesiredCapabilities capability = DesiredCapabilitiesFactory.getCapabilities(browser);

        logger.log(Level.FINE, "Spawning a browser with the following capabilities : " + showCapabilities(capability));

        switch (browser) {
        case FIREFOX:
        case CHROME:
        case INTERNET_EXPLORER:
        case HTMLUNIT:
        case IPHONE:
        case IPAD:
        case OPERA:
        case PHANTOMJS:
        case SAFARI:
            return createDriverInstance(getURL(), capability);

        case GENERIC:
            if (Grid.getMobileTestSession().getDevice().contains("ipad")
                    || Grid.getMobileTestSession().getDevice().contains("iphone")) {

                return createIOSDriverInstance(getURL(), capability);

            } else if (Grid.getMobileTestSession().getDevice().contains("android")) {
                return createSelendroidInstance(getURL(), capability);
            }

        default:
            break;
        }
        return null;

    }

    private static SelendroidDriver createSelendroidInstance(URL url, DesiredCapabilities capability) {
        try {
            List<EventListener> listeners = getSeLionEventListeners();

            if (listeners.size() > 0) {
                return new SelionSelendroidDriver(new EventFiringCommandExecutor(new SelendroidCommandExecutor(url),
                        listeners), capability);
            } else {
                return new SelendroidDriver(url, capability);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static RemoteIOSDriver createIOSDriverInstance(URL url, DesiredCapabilities capability) {
        List<EventListener> listeners = getSeLionEventListeners();

        if (listeners.size() > 0) {
            return new SelionRemoteIOSDriver(new EventFiringCommandExecutor(new HttpCommandExecutor(url), listeners),
                    (IOSCapabilities) capability);
        } else {
            return new RemoteIOSDriver(url, (IOSCapabilities) capability);
        }
    }

    private static RemoteWebDriver createDriverInstance(URL url, DesiredCapabilities capability) {

        List<EventListener> listeners = getSeLionEventListeners();
        RemoteWebDriver driver = null;
        if (listeners.size() > 0) {
            driver = new RemoteWebDriver(new EventFiringCommandExecutor(new HttpCommandExecutor(url), listeners),
                    capability);
        } else {
            driver = new RemoteWebDriver(url, capability);
        }
        setWindowSize(driver);
        registerElementEventListeners();
        return driver;

    }

    private static String showCapabilities(DesiredCapabilities dc) {
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

    private static List<EventListener> getSeLionEventListeners() {
        String listeners = Config.getConfigProperty(ConfigProperty.SELENIUM_WEBDRIVER_EVENT_LISTENER).trim();

        List<EventListener> eventListeners = new ArrayList<EventListener>();

        if (StringUtils.isBlank(listeners)) {
            return eventListeners;
        }

        String[] allEventListeners = listeners.split(",");
        for (String eachEventListener : allEventListeners) {
            try {
                Object listener = Class.forName(eachEventListener.trim()).newInstance();
                if (EventListener.class.isAssignableFrom(listener.getClass())) {
                    eventListeners.add((EventListener) listener);
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                logger.warning("Unable to register [" + eachEventListener + "] as a selion event listener.");
            }
        }

        return eventListeners;
    }
    
    private static void registerElementEventListeners() {
        String listeners = Config.getConfigProperty(ConfigProperty.ELEMENT_EVENT_LISTENER).trim();

        if (StringUtils.isBlank(listeners)) {
            return;
        }

        String[] allEventListeners = listeners.split(",");
        for (String eachEventListener : allEventListeners) {
            try {
                Object listener = Class.forName(eachEventListener.trim()).newInstance();
                if (ElementEventListener.class.isAssignableFrom(listener.getClass())) {
                    Grid.getTestSession().getElementEventListeners().add((ElementEventListener)listener);
                    logger.info("Registered [" + eachEventListener + "] as a selion element event listener.");
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                logger.warning("Unable to register [" + eachEventListener + "] as a selion element event listener.");
            }
        }
    }

    private static URL getURL() {

        URL url = null;
        String hostToRun = Config.getConfigProperty(ConfigProperty.SELENIUM_HOST);

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
            url = new URL("http://" + hostToRun + ":" + port + "/wd/hub");
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return url;
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
