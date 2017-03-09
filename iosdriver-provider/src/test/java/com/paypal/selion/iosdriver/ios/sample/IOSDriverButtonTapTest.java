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

package com.paypal.selion.iosdriver.ios.sample;

import java.io.File;
import java.net.URL;

import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.mobile.ios.UIAButton;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import com.paypal.selion.platform.mobile.ios.UIATextField;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class IOSDriverButtonTapTest {

    private static final String appFolder = "/apps";

    @BeforeClass
    public void setup() {
        URL url = IOSDriverButtonTapTest.class.getResource(appFolder);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testSingleTap() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { "//UIAApplication[1]/UIAWindow[1]/UIAButton[2]" });
        UIAButton singleTapButton = new UIAButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[2]");
        singleTapButton.tap();
        UIATextField singleTapResponseTextField = new UIATextField(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIATextField[2]");
        Assert.assertEquals(singleTapResponseTextField.getValue(), "Tap Count: 1", "Single tap count does not match");
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testDoubleTap() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[1]") });
        UIAButton doubleTapButton = new UIAButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[1]");
        doubleTapButton.doubleTap(new Object[] { "//UIAApplication[1]/UIAWindow[1]/UIATextField[1]" });
        UIATextField singleTapResponseTextField = new UIATextField(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIATextField[1]");
        Assert.assertEquals(singleTapResponseTextField.getValue(), "Tap Count: 2", "Double tap count does not match");
    }

    @Test(expectedExceptions = InvalidSelectorException.class)
    @MobileTest(appName = "PageObjects")
    public void testInvalidXpath() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
        Thread.sleep(500);
        UIAButton singleTapButton = new UIAButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[]");
        singleTapButton.doubleTap();
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    @MobileTest(appName = "PageObjects")
    public void testInvalidLocator() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
        UIAButton singleTapButton = new UIAButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[3]");
        singleTapButton.doubleTap();
    }

    @AfterClass
    public void teardown() {
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER,
                Config.ConfigProperty.MOBILE_APP_FOLDER.getDefaultValue());
    }

}
