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

package com.paypal.selion.appium.ios.sample;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import com.paypal.selion.platform.mobile.ios.UIAStaticText;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumIOSLabelAndNavigationTest {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testLabelValue() throws InterruptedException {
        UIAStaticText label = new UIAStaticText("xpath=//UIAApplication[1]/UIAWindow[1]/UIAStaticText[1]");
        Assert.assertEquals(label.getValue(), "Page Objects Demo", "Label value does not match");
    }

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testNavigationBarName() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        Assert.assertEquals(navigationBar.getName(), "Sample", "Navigation bar name does not match");
    }

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testNavigationRightClick() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        Assert.assertEquals(navigationBar.getName(), "Tap", "Navigation right button not functioning properly");
    }

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testNavigationLeftClick() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickLeftButton();
        Assert.assertEquals(navigationBar.getName(), "Sample", "Navigation left button not functioning properly");
    }

    //disable this case cause can't see why this should throw exception while the above one works.
    @Test(expectedExceptions = WebDriverException.class, enabled=false)
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testInvalidLeftClick() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickLeftButton();
    }

    @Test(expectedExceptions = UIOperationFailedException.class)
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
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

}
