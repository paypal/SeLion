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
import com.paypal.selion.platform.mobile.android.UiTextView;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class SelendroidCheckBoxSeekBarTest {

    private static final String APP_FOLDER = "/apps";

    @BeforeClass
    public void setup() {
        URL url = AndroidTest.class.getResource(APP_FOLDER);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testCheckBox() {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        UiButton uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ActionMenuItemView[@name='Touch']");
        uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//TintCheckBox[@value='Android']");
        UiObject checkBox = new UiObject("id=android_checkbox");
        checkBox.click();
        UiTextView checkBoxOutput = new UiTextView("id=checkbox_textview");
        Assert.assertEquals(checkBoxOutput.getText(), "Android");
        checkBox = new UiObject("id=ios_checkbox");
        checkBox.click();
        checkBoxOutput = new UiTextView("id=checkbox_textview");
        Assert.assertEquals(checkBoxOutput.getText(), "iOS");
        checkBox = new UiObject("id=ios_checkbox");
        checkBox.click();
        checkBoxOutput = new UiTextView("id=checkbox_textview");
        Assert.assertEquals(checkBoxOutput.getText(), "");
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testSeekBar() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        UiButton uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ActionMenuItemView[@name='Touch']");
        uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//SeekBar[@id='seekBar']");
        UiObject seekBar = new UiObject("id=seekBar");
        seekBar.swipeRight();
        UiObject seekBarOutput = new UiObject("id=seekBar_textview");
        Assert.assertEquals(seekBarOutput.getText(), "Value: 100", "Seek Bar swipe right value does not match");
        seekBar = new UiObject("id=seekBar");
        seekBar.swipeLeft();
        seekBarOutput = new UiObject("id=seekBar_textview");
        Assert.assertEquals(seekBarOutput.getText(), "Value: 0", "Seek Bar swipe right value does not match");
    }

}
