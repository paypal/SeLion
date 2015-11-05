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

package com.paypal.selion.android.sample;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.android.UiButton;
import com.paypal.selion.platform.mobile.android.UiObject;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidEditTextTest {
    
    private static final String pageObjectsAppPath = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String deviceName = "android:19";
    private String actionButtonLocator = "com.paypal.selion.pageobjectsdemoapp:id/action_button";
    private String textFieldLocator = "com.paypal.selion.pageobjectsdemoapp:id/edit_text";

    private UiButton menuButton;
    private UiObject textField;
    
    @BeforeClass
    public void initElements(){
        menuButton = new UiButton(actionButtonLocator);
        textField = new UiObject(textFieldLocator);
    }
    
    @Test
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testSetText() {
        WebDriverWaitUtils.waitUntilElementIsVisible(actionButtonLocator);
        menuButton.click(textField);
        textField.setText("SeLion");
        Assert.assertEquals(textField.getText(), "SeLion", "Set edit text value does not match");
    }
    
    @Test
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testSetClearText() {
        WebDriverWaitUtils.waitUntilElementIsVisible(actionButtonLocator);
        menuButton.click(textField);
        textField.setText("SeLion");
        Assert.assertEquals(textField.getText(), "SeLion", "Set value of edit-text does not match");
        textField.clearText();
        Assert.assertEquals(textField.getText(), "", "Clear on edit-text failed");
        textField.setText("SeLion");
        textField.setText("Selendroid");
        Assert.assertEquals(textField.getText(), "Selendroid", "Reset edit-text value does not match");
    }
}
