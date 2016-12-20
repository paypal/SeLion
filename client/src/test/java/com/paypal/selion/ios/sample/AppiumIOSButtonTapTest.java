/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-16 PayPal                                                                                       |
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

package com.paypal.selion.ios.sample;

import com.paypal.selion.platform.mobile.elements.MobileButton;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumIOSButtonTapTest {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app", deviceType = "iPhone Simulator")
    public void testSingleTap() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton("//UIAApplication[1]/UIAWindow[1]/UIAButton[2]");
        MobileButton singleTapButton = new MobileButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[2]");
        singleTapButton.tap();
        MobileTextField singleTapResponseTextField = new MobileTextField(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIATextField[2]");
        Assert.assertEquals(singleTapResponseTextField.getValue(), "Tap Count: 1", "Single tap count does not match");
    }

/*TODO double tap issue
    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app", deviceType = "iPhone Simulator")
    public void testDoubleTap() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton(new MobileButton(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[1]"));
        MobileButton doubleTapButton = new MobileButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[1]");
        doubleTapButton.doubleTap();
        MobileTextField singleTapResponseTextField = new MobileTextField(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIATextField[1]");
        Assert.assertEquals(singleTapResponseTextField.getValue(), "Tap Count: 2", "Double tap count does not match");
    }
*/

    @Test(expectedExceptions = WebDriverException.class)
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app", deviceType = "iPhone Simulator")
    public void testInvalidXpath() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
        Thread.sleep(500);
        MobileButton singleTapButton = new MobileButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[]");
        singleTapButton.click();
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app", deviceType = "iPhone Simulator")
    public void testInvalidLocator() throws InterruptedException {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton();
        Thread.sleep(500);
        MobileButton singleTapButton = new MobileButton("xpath=//UIAApplication[1]/UIAWindow[1]/UIAButton[3]");
        singleTapButton.click();
    }

}
