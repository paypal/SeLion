/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2017 PayPal                                                                                     |
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

package com.paypal.selion.iosdriver.ios.sample;

import java.io.File;
import java.net.URL;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import com.paypal.selion.platform.mobile.ios.UIAStaticText;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class IOSDriverLabelAndNavigationTest {

    private static final String appFolder = "/apps";

    @BeforeClass
    public void setup() {
        URL url = IOSDriverLabelAndNavigationTest.class.getResource(appFolder);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testLabelValue() throws InterruptedException {
        UIAStaticText label = new UIAStaticText("xpath=//UIAApplication[1]/UIAWindow[1]/UIAStaticText[1]");
        Assert.assertEquals(label.getValue(), "Page Objects Demo", "Label value does not match");
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testNavigationBarName() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        Assert.assertEquals(navigationBar.getName(), "Sample", "Navigation bar name does not match");
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testNavigationRightClick() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        Assert.assertEquals(navigationBar.getName(), "Tap", "Navigation right button not functioning properly");
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testNavigationLeftClick() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickLeftButton();
        Assert.assertEquals(navigationBar.getName(), "Sample", "Navigation left button not functioning properly");
    }

    @MobileTest(appName = "PageObjects")
    @Test(expectedExceptions = WebDriverException.class)
    public void testInvalidLeftClick() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickLeftButton();
    }

    @MobileTest(appName = "PageObjects")
    @Test(expectedExceptions = UIOperationFailedException.class)
    public void testInvalidRightClick() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 5; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
    }

    @AfterClass
    public void teardown() {
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER,
                Config.ConfigProperty.MOBILE_APP_FOLDER.getDefaultValue());
    }

}
