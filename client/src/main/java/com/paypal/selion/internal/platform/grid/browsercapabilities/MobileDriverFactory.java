/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.selendroid.client.SelendroidCommandExecutor;
import io.selendroid.client.SelendroidDriver;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;

import com.paypal.selion.internal.platform.grid.MobileNodeType;
import com.paypal.selion.internal.platform.grid.MobileTestSession;
import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.grid.EventFiringCommandExecutor;
import com.paypal.selion.platform.grid.EventListener;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.SeLionAppiumAndroidDriver;
import com.paypal.selion.platform.grid.SeLionAppiumIOSDriver;
import com.paypal.selion.platform.grid.SeLionSelendroidDriver;
import com.paypal.selion.platform.grid.SelionRemoteIOSDriver;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This factory class is responsible for providing the framework with a {@link RemoteWebDriver} instance based on the
 * Mobile Node type.
 * 
 */
public final class MobileDriverFactory {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    /**
     * @return A {@link RemoteWebDriver} instance based on the mobile node type
     */
    public static RemoteWebDriver createInstance() {

        DesiredCapabilities capability = new MobileCapabilitiesBuilder().createCapabilities();
        capability = new UserCapabilitiesBuilder().getCapabilities(capability);
        logger.log(Level.FINE, "Spawning a mobile with the following capabilitiesgit: "
                + DriverFactoryHelper.showCapabilities(capability));

        RemoteWebDriver driver = null;
        MobileTestSession mobileSession = Grid.getMobileTestSession();
        MobileNodeType mobileNodeType = mobileSession.getMobileNodeType();
        if (mobileNodeType == MobileNodeType.APPIUM) {
            if (mobileSession.getPlatform() == WebDriverPlatform.ANDROID) {
                driver = createAppiumAndroidInstance(DriverFactoryHelper.getURL(), capability);
            }
            if (mobileSession.getPlatform() == WebDriverPlatform.IOS) {
                driver = createAppiumIOSInstance(DriverFactoryHelper.getURL(), capability);
            }
        } else if (mobileNodeType == MobileNodeType.IOS_DRIVER) {
            driver = createIOSDriverInstance(DriverFactoryHelper.getURL(), capability);
        } else if (mobileNodeType == MobileNodeType.SELENDROID) {
            driver = createSelendroidInstance(DriverFactoryHelper.getURL(), capability);
        }

        DriverFactoryHelper.printDebugInfoForUser(driver);
        return driver;
    }

    private static AndroidDriver<?> createAppiumAndroidInstance(URL url, DesiredCapabilities capability) {
        try {
            List<EventListener> listeners = DriverFactoryHelper.getSeLionEventListeners();

            if (listeners.size() > 0) {
                return new SeLionAppiumAndroidDriver(new EventFiringCommandExecutor(new HttpCommandExecutor(url),
                        listeners), capability, url);
            } else {
                return new SeLionAppiumAndroidDriver(url, capability);
            }
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }

    private static IOSDriver<?> createAppiumIOSInstance(URL url, DesiredCapabilities capability) {
        try {
            List<EventListener> listeners = DriverFactoryHelper.getSeLionEventListeners();

            if (listeners.size() > 0) {
                return new SeLionAppiumIOSDriver(
                        new EventFiringCommandExecutor(new HttpCommandExecutor(url), listeners), capability, url);
            } else {
                return new SeLionAppiumIOSDriver(url, capability);
            }
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }

    private static SelendroidDriver createSelendroidInstance(URL url, DesiredCapabilities capability) {
        try {
            List<EventListener> listeners = DriverFactoryHelper.getSeLionEventListeners();

            if (listeners.size() > 0) {
                return new SeLionSelendroidDriver(new EventFiringCommandExecutor(new SelendroidCommandExecutor(url),
                        listeners), capability);
            } else {
                return new SeLionSelendroidDriver(url, capability);
            }
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }

    private static RemoteIOSDriver createIOSDriverInstance(URL url, DesiredCapabilities capability) {
        List<EventListener> listeners = DriverFactoryHelper.getSeLionEventListeners();
        if (listeners.size() > 0) {
            return new SelionRemoteIOSDriver(new EventFiringCommandExecutor(new HttpCommandExecutor(url), listeners),
                    (IOSCapabilities) capability);
        }
        return new SelionRemoteIOSDriver(url, (IOSCapabilities) capability);

    }
}
