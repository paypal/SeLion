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

package com.paypal.selion.android.sample;

import java.io.File;
import java.net.URL;

import com.paypal.selion.platform.mobile.elements.MobileButton;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class SelendroidButtonLongClickTest {

    private static final String APP_FOLDER = "/apps";

    @BeforeClass
    public void setup() {
        URL url = AndroidTest.class.getResource(APP_FOLDER);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testLongClickButtonProperties() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        MobileButton uiObject = new MobileButton("id=action_button");
        uiObject.click(new MobileButton("xpath=//TintButton[@value='Long Press']"));
        MobileButton uiButton = new MobileButton("id=long_press_button");
        Assert.assertEquals(uiButton.isLongClickable(), true, "Button is not long clickable");
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testButtonClick() {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        MobileButton uiObject = new MobileButton("id=action_button");
        uiObject.click("xpath=//TintButton[@value='Long Press']");
        MobileButton uiButton = new MobileButton("id=long_press_button");
        uiButton.longPress(new MobileButton("xpath=//TextView[contains(@value, ', long press')]"));
        MobileTextField uiTextView = new MobileTextField("id=long_press_button_output");
        String output = uiTextView.getText();
        Assert.assertEquals(output.contains("long press"), true, "Button center click not working properly");
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testButtonClickTopLeft() {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        MobileButton uiObject = new MobileButton("id=action_button");
        uiObject.click("xpath=//TintButton[@value='Long Press']");
        MobileButton uiButton = new MobileButton("id=long_press_button");
        uiButton.longPress("xpath=//TextView[contains(@value, ', long press')]");
        MobileTextField uiTextView = new MobileTextField("id=long_press_button_output");
        String output = uiTextView.getText();
        Assert.assertEquals(output.contains("long press"), true, "Button top left click not working properly");
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testButtonClickBottomRight() {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        MobileButton uiObject = new MobileButton("id=action_button");
        uiObject.click("xpath=//TintButton[@value='Long Press']");
        MobileButton uiButton = new MobileButton("id=long_press_button");
        uiButton.longPress("xpath=//TextView[contains(@value, ', long press')]");
        MobileTextField uiTextView = new MobileTextField("id=long_press_button_output");
        String output = uiTextView.getText();
        Assert.assertEquals(output.contains("long press"), true, "Button bottom right click not working properly");
    }
}
