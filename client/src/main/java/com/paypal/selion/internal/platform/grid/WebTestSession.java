/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IInvokedMethod;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.platform.grid.browsercapabilities.WebDriverFactory;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.RemoteNodeInformation;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A class for loading and representing the {@link WebTest} annotation parameters. Also performs sanity checks.
 */
public class WebTestSession extends AbstractTestSession {

    private String browser = "";
    private int browserHeight;
    private int browserWidth;
    private static final SimpleLogger logger = SeLionLogger.getLogger();

    WebTestSession() {
        super();
    }

    /**
     * Call this to initialize the {@link WebTestSession} object from the TestNG {@link IInvokedMethod}
     * 
     * @param method
     *            - the TestNG {@link IInvokedMethod}
     * 
     */
    @Override
    public void initializeTestSession(InvokedMethodInformation method) {

        this.initTestSession(method);
        WebTest webTestAnnotation = method.getAnnotation(WebTest.class);
        // check class has webtest annotation (i.e. using shared sessions)
        if (webTestAnnotation == null) {
            webTestAnnotation = method.getActualMethod().getDeclaringClass().getAnnotation(WebTest.class);
        }
        // Setting the browser value
        this.browser = getLocalConfigProperty(ConfigProperty.BROWSER);
        if (webTestAnnotation != null) {
            if (StringUtils.isNotBlank(webTestAnnotation.browser())) {
                this.browser = webTestAnnotation.browser();
            }
            if (webTestAnnotation.browserHeight() > 0 && webTestAnnotation.browserWidth() > 0) {
                this.browserHeight = webTestAnnotation.browserHeight();
                this.browserWidth = webTestAnnotation.browserWidth();
            } else {
                warnUserOfInvalidBrowserDimensions(webTestAnnotation);
            }

            initializeAdditionalCapabilities(webTestAnnotation.additionalCapabilities(), method);
        }
    }

    private void warnUserOfInvalidBrowserDimensions(WebTest webTestAnnotation) {
        if (webTestAnnotation.browserHeight() < 0 && webTestAnnotation.browserWidth() < 0) {
            logger.info("The parameters provided in WebTest annotation are less than zero. Ignoring them.");
        }
        if (webTestAnnotation.browserHeight() == 0 && webTestAnnotation.browserWidth() == 0) {
            logger.fine("No parameters for browser dimensions were provided.");
        } else if (webTestAnnotation.browserHeight() == 0) {
            logger.info("The height was not provided ignoring width parameter.");
        } else if (webTestAnnotation.browserWidth() == 0) {
            logger.info("The width was not provided ignoring height parameter.");
        }
    }

    /**
     * @return the browser configured for the test method
     */
    public final String getBrowser() {
        logger.entering();
        // By now we would have already set the browser flavor from the local config as part of the
        // initializeTestSession
        // method. So lets check if its still blank and if yes, we default it to the global config value.
        if (StringUtils.isBlank(this.browser)) {
            this.browser = Config.getConfigProperty(ConfigProperty.BROWSER);
        }
        // All of our browser values need to start with the magic char "*"
        if (!StringUtils.startsWith(this.browser, "*")) {
            this.browser = "*".concat(this.browser);
        }
        logger.exiting(this.browser);
        return this.browser;
    }

    /**
     * @return the height of the Browser window that will be spawned
     */
    public final int getBrowserHeight() {
        if (this.browserHeight == 0 || this.browserWidth == 0) {
            String height = getLocalConfigProperty(ConfigProperty.BROWSER_HEIGHT);
            if (StringUtils.isNotBlank(height)) {
                this.browserHeight = Integer.parseInt(height);
            }
        }
        return (this.browserHeight);

    }

    /**
     * @return the width of the browser window that will be spawned
     */
    public final int getBrowserWidth() {
        if (this.browserHeight == 0 || this.browserWidth == 0) {
            String width = getLocalConfigProperty(ConfigProperty.BROWSER_WIDTH);
            if (StringUtils.isNotBlank(width)) {
                this.browserWidth = Integer.parseInt(width);
            }
        }
        return (this.browserWidth);
    }

    private boolean runLocally() {
        return Boolean.parseBoolean(Config.getConfigProperty(ConfigProperty.SELENIUM_RUN_LOCALLY));
    }

    private void createSession() {
        logger.entering();
        BrowserFlavors flavor = BrowserFlavors.getBrowser(getBrowser());
        RemoteWebDriver driver = WebDriverFactory.createInstance(flavor);

        if (!runLocally()) {
            String hostName = Config.getConfigProperty(ConfigProperty.SELENIUM_HOST);
            int port = Integer.parseInt(Config.getConfigProperty(ConfigProperty.SELENIUM_PORT));
            RemoteNodeInformation node = Grid.getRemoteNodeInfo(hostName, port, driver.getSessionId());
            if (node != null) {
                logger.info(node.toString());
            }
        }
        Grid.getThreadLocalWebDriver().set(driver);
        logger.exiting();
    }

    @Override
    public void startSesion() {
        createSession();
        setStarted(true);
    }

    @Override
    public WebDriverPlatform getPlatform() {
        return WebDriverPlatform.WEB;
    }

}
