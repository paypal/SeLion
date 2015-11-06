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
public class SelendroidScrollTest {

    private static final String APP_FOLDER = "/apps";

    @BeforeClass
    public void setup() {
        URL url = AndroidTest.class.getResource(APP_FOLDER);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testVisibleCellClick() {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        UiButton uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ActionMenuItemView[@name='Touch']");
        uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ActionMenuItemView[@name='Scroll']");
        uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//TextView[@value='Cell 3']");
        UiObject textView = new UiObject("id=TextView3");
        textView.click("xpath=//TextView[@id='message']");
        UiObject messageBoxText = new UiObject("id=message");
        Assert.assertEquals(messageBoxText.getText(), "Cell 3", "Message does not match the clicked cell");
        UiObject messageBoxButton = new UiObject("id=button1");
        messageBoxButton.click();
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testScrolledCellClick() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        UiButton uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ActionMenuItemView[@name='Touch']");
        uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ActionMenuItemView[@name='Scroll']");
        uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ScrollView[@id='scroll_view']");
        
        UiObject scrollView = new UiObject("id=scroll_view");
        scrollView.swipeUp();
        Thread.sleep(5 * 1000);
        UiObject textView = new UiObject("id=TextView13");
        textView.click("xpath=//TextView[@id='message']");
        UiObject messageBoxText = new UiObject("id=message");
        Assert.assertEquals(messageBoxText.getText(), "Cell 13", "Message does not match the clicked cell");
        UiObject messageBoxButton = new UiObject("id=button1");
        messageBoxButton.click();
    }

}
