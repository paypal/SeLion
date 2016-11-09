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

package com.paypal.selion.appium.ios.sample;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.ios.UIAAlert;
import com.paypal.selion.platform.mobile.ios.UIAButton;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import com.paypal.selion.platform.mobile.ios.UIATableView;
import org.testng.annotations.Test;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumIOSTableAlertTest {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
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

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
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
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
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

    @Test(expectedExceptions = UIOperationFailedException.class)
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
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

}
