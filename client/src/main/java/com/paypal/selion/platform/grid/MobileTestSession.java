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

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.internal.utils.InvokedMethodInformation;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A class for loading and representing the {@link MobileTest} annotation parameters. Also performs sanity checks.
 * 
 */
// TODO: Should this be moved to an "internal" package ?
public class MobileTestSession extends AbstractTestSession {
    private static SimpleLogger logger = SeLionLogger.getLogger();
    private String appName;
    private String appLocation;
    private String device = "iphone";
    private String appLanguage;
    private String appLocale;
    private String deviceSerial;

    MobileTestSession() {
        super();
        // go ahead and init global-only config properties
        appLocation = Config.getConfigProperty(ConfigProperty.SELENIUM_NATIVE_APP_FOLDER);
    }

    public String getAppLocale() {
        return appLocale;
    }

    public String getAppLanguage() {
        return appLanguage;
    }

    public String getAppName() {
        logger.entering();
        if (StringUtils.isBlank(appName)) {
            throw new IllegalArgumentException(
                    "Please specify the application name either via the @AppTest annotation or via the SeLion configuration parameter");
        }
        logger.exiting(appName);
        return appName;
    }

    public String getdeviceSerial() {
        return deviceSerial;
    }

    public String getAppLocation() {
        return appLocation;
    }

    public WebDriverPlatform getPlatform() {
        WebDriverPlatform platform = WebDriverPlatform.ANDROID;
        if (getDevice().equalsIgnoreCase("iphone") || getDevice().equalsIgnoreCase("ipad")) {
            platform = WebDriverPlatform.IOS;
        }
        return platform;
    }

    public String getDevice() {
        if (device.contains("android") || device.equalsIgnoreCase("iphone") || device.equalsIgnoreCase("ipad")) {
            return device;
        } else {
            throw new IllegalArgumentException("The device should either be provided as 'iphone', 'ipad', 'android16',"
                    + " 'android17', 'android18', or 'android19'.");
        }
    }

    @Override
    public SeLionSession startSesion() {
        logger.entering();
        BrowserFlavors flavor = null;
        try {
            //
            flavor = BrowserFlavors.getBrowser(this.appName);
        } catch (IllegalArgumentException ex) {
            flavor = BrowserFlavors.GENERIC;
        }

        ScreenShotRemoteWebDriver driver = ScreenShotRemoteWebDriver.createInstance(flavor);

        SeLionSession session = new SeLionSession(driver);
        Grid.getThreadLocalWebDriver().set(session.getWebDriver());
        logger.exiting(session);
        return session;
    }

    @Override
    public SeLionSession startSession(Map<String, SeLionSession> sessions) {
        throw new UnsupportedOperationException(
                "Session management is not supported for tests running on devices/simulators.");
    }

    @Override
    public void initializeTestSession(InvokedMethodInformation method, Map<String, SeLionSession> sessionMap) {
        throw new UnsupportedOperationException(
                "Session management is not supported for tests running on devices/simulators.");
    }

    @Override
    public void initializeTestSession(InvokedMethodInformation method) {
        logger.entering(method);
        this.initTestSession(method);
        MobileTest deviceTestAnnotation = method.getAnnotation(MobileTest.class);

        // First load these from the <test> local config
        appLocale = getLocalConfigProperty(ConfigProperty.SELENIUM_NATIVE_APP_LOCALE);
        appLanguage = getLocalConfigProperty(ConfigProperty.SELENIUM_NATIVE_APP_LANGUAGE);
        appName = getLocalConfigProperty(ConfigProperty.SELENIUM_NATIVE_APP_NAME);
        deviceSerial = getLocalConfigProperty(ConfigProperty.SELENDROID_DEVICE_SERIAL);

        // Override values when supplied via the annotation
        if (deviceTestAnnotation != null) {
            if (StringUtils.isNotBlank(deviceTestAnnotation.appName())) {
                this.appName = deviceTestAnnotation.appName();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.language())) {
                this.appLanguage = deviceTestAnnotation.language();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.locale())) {
                this.appLocale = deviceTestAnnotation.locale();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.device())) {
                this.device = deviceTestAnnotation.device();
            }
            if (StringUtils.isNotBlank(deviceTestAnnotation.deviceSerial())) {
                this.deviceSerial = deviceTestAnnotation.deviceSerial();
            }
        }
        logger.exiting();
    }

    @Override
    public void closeCurrentSession(Map<String, SeLionSession> sessionMap, InvokedMethodInformation methodInfo) {
        throw new UnsupportedOperationException(
                "Session management is not supported for tests running on devices/simulators.");
    }

    @Override
    public void closeAllSessions(Map<String, SeLionSession> sessionMap) {
        throw new UnsupportedOperationException(
                "Session management is not supported for tests running on devices/simulators.");
    }

}
