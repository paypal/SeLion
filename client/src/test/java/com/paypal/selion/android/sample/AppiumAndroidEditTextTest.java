/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

import com.paypal.selion.platform.mobile.elements.MobileTextField;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidEditTextTest {
    
    private static final String PAGE_OBJECTS_APP_PATH = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String TEXT_FIELD_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/sampleTxbEditable";

    private MobileTextField textField;

    @BeforeClass
    public void initElements(){
        textField = new MobileTextField(TEXT_FIELD_LOCATOR);
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH)
    public void testMobileTextField() {
        WebDriverWaitUtils.waitUntilElementIsVisible(TEXT_FIELD_LOCATOR);
        textField.setText("Selion");
        Assert.assertEquals(textField.getText(), "Selion", "Set edit text value does not match");
        textField.clear();
        Assert.assertEquals(textField.getText(), "", "Set edit text value does not match");
        textField.sendKeys("Selion");
        Assert.assertEquals(textField.getText(), "Selion", "Set edit text value does not match");
        textField.setText("123");
        Assert.assertEquals(textField.getText(), "123", "Set edit text value does not match");
        textField.sendKeys("456");
        Assert.assertEquals(textField.getText(), "123456", "Set edit text value does not match");
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH)
    public void testSetText() {
        WebDriverWaitUtils.waitUntilElementIsVisible(TEXT_FIELD_LOCATOR);
        textField.setText("SeLion");
        Assert.assertEquals(textField.getText(), "SeLion", "Set edit text value does not match");
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH)
    public void testSetClearText() {
        WebDriverWaitUtils.waitUntilElementIsVisible(TEXT_FIELD_LOCATOR);
        textField.setText("SeLion");
        Assert.assertEquals(textField.getText(), "SeLion", "Set value of edit-text does not match");
        textField.clear();
        Assert.assertEquals(textField.getText(), "", "Clear on edit-text failed");
        textField.setText("SeLion");
        textField.setText("Selendroid");
        Assert.assertEquals(textField.getText(), "Selendroid", "Reset edit-text value does not match");
    }
}
