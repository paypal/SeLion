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

import java.io.File;
import java.net.URL;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.mobile.android.UiButton;
import com.paypal.selion.platform.mobile.android.UiObject;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class SelendroidEditTextTest {

    private static final String APP_FOLDER = "/apps";

    @BeforeClass
    public void setup() {
        URL url = AndroidTest.class.getResource(APP_FOLDER);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testSetText() {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        UiButton uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//TintEditText[@id='edit_text']");
        UiObject uiEditText = new UiObject("id=edit_text");
        uiEditText.setText("SeLion");
        uiEditText = new UiObject("id=edit_text");
        Assert.assertEquals(uiEditText.getText(), "SeLion", "Set edit text value does not match");
    }
    
    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testSetClearText() {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        UiButton uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//TintEditText[@id='edit_text']");
        UiObject uiEditText = new UiObject("id=edit_text");
        uiEditText.setText("SeLion");
        uiEditText = new UiObject("id=edit_text");
        Assert.assertEquals(uiEditText.getText(), "SeLion", "Set value of edit-text does not match");
        uiEditText = new UiObject("id=edit_text");
        uiEditText.clearText();
        Assert.assertEquals(uiEditText.getText(), "", "Clear on edit-text failed");
        uiEditText.setText("SeLion");
        uiEditText.setText("Selendroid");
        Assert.assertEquals(uiEditText.getText(), "Selendroid", "Reset edit-text value does not match");
    }
}
