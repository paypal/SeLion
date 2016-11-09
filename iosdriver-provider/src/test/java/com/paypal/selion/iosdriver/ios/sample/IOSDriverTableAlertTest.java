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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.ios.UIAAlert;
import com.paypal.selion.platform.mobile.ios.UIAButton;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import com.paypal.selion.platform.mobile.ios.UIATableView;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class IOSDriverTableAlertTest {

    private static final String appFolder = "/apps";

    @BeforeClass
    public void setup() {
        URL url = IOSDriverTableAlertTest.class.getResource(appFolder);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testTableAlertOfVisibleElement() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'Touch')]") });
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'State')]") });
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'Table')]") });
        navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new Object[] { new UIAButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]/UIAButton[contains(@name,'Picker')]") });
        UIATableView table = new UIATableView("xpath=//UIAApplication[1]/UIAWindow[1]/UIATableView[1]");
        table.clickCellAtIndex(2);
        Thread.sleep(500);
        UIAAlert alert = new UIAAlert("xpath=//UIAApplication[1]/UIAWindow[4]/UIAAlert[1]");
        alert.clickCancelButton();
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testTableAlertOfNotVisibleElement() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 4; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        UIATableView table = new UIATableView("xpath=//UIAApplication[1]/UIAWindow[1]/UIATableView[1]");
        table.scrollToCellAtIndex(15);
        Thread.sleep(500);
        table.clickCellAtIndex(15);
        UIAAlert alert = new UIAAlert("xpath=//UIAApplication[1]/UIAWindow[4]/UIAAlert[1]");
        alert.clickButtonAtIndex(1);
    }

    @Test(expectedExceptions = UIOperationFailedException.class)
    @MobileTest(appName = "PageObjects")
    public void testInvalidAlertButtonClick() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 4; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        UIATableView table = new UIATableView("xpath=//UIAApplication[1]/UIAWindow[1]/UIATableView[1]");
        table.clickCellAtIndex(2);
        Thread.sleep(500);
        UIAAlert alert = new UIAAlert("xpath=//UIAApplication[1]/UIAWindow[4]/UIAAlert[1]");
        alert.clickButtonAtIndex(2);
    }

    @Test(expectedExceptions = UIOperationFailedException.class)
    @MobileTest(appName = "PageObjects")
    public void testInvalideTableCellClick() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 4; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        UIATableView table = new UIATableView("xpath=//UIAApplication[1]/UIAWindow[1]/UIATableView[1]");
        table.scrollToCellAtIndex(20);
    }

    @AfterClass
    public void teardown() {
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER,
                Config.ConfigProperty.MOBILE_APP_FOLDER.getDefaultValue());
    }

}
