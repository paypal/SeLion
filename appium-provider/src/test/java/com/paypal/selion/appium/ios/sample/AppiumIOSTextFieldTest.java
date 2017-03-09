/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import com.paypal.selion.platform.mobile.ios.UIATextField;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumIOSTextFieldTest {

    private static final String TEXT_FIELD_LOCATOR = "xpath=//UIAApplication[1]/UIAWindow[1]/UIATextField[2]";

    @Test
    @MobileTest(appPath = "src/test/resources/apps/PageObjects.app")
    public void testMobileTextField() {
        UIANavigationBar navigationBar = new UIANavigationBar(
                "xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
        navigationBar.clickRightButton("//UIAApplication[1]/UIAWindow[1]/UIAButton[2]");
        MobileTextField textField = new UIATextField(TEXT_FIELD_LOCATOR);
        WebDriverWaitUtils.waitUntilElementIsVisible(TEXT_FIELD_LOCATOR);
        textField.setText("Selion");
        Assert.assertEquals(textField.getValue(), "Selion", "Set edit text value does not match");
        textField.clearText();
        Assert.assertEquals(textField.getValue(), "", "Set edit text value does not match");
        textField.sendKeys("Selion");
        Assert.assertEquals(textField.getValue(), "Selion", "Set edit text value does not match");
        textField.setText("123");
        Assert.assertEquals(textField.getValue(), "123", "Set edit text value does not match");
        textField.sendKeys("456");
        Assert.assertEquals(textField.getValue(), "123456", "Set edit text value does not match");
    }
}
