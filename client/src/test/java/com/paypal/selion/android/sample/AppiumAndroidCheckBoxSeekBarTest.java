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
import com.paypal.selion.platform.mobile.android.UiTextView;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidCheckBoxSeekBarTest {

    private static final String pageObjectsAppPath = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String deviceName = "android:19";
    private final String actionButtonLocator = "com.paypal.selion.pageobjectsdemoapp:id/action_button";
    private final String seekBarLocator = "com.paypal.selion.pageobjectsdemoapp:id/seekBar";
    private final String longPressButtonLocator = "com.paypal.selion.pageobjectsdemoapp:id/long_press_button";
    private final String textViewLocator = "com.paypal.selion.pageobjectsdemoapp:id/seekBar_textview";
    // Check box
    private final String checkBoxAndroidLocator = "com.paypal.selion.pageobjectsdemoapp:id/android_checkbox";
    private final String checkBoxiOSLocator = "com.paypal.selion.pageobjectsdemoapp:id/ios_checkbox";
    private final String valueTextViewLocator = "com.paypal.selion.pageobjectsdemoapp:id/checkbox_textview";

    private UiButton menuButton = null;
    private UiObject seekBar = null;
    private UiTextView seekBarTextView = null;
    private UiTextView checkBoxTextView = null;
    private UiObject iosCheckBox = null;
    private UiObject androidCheckBox = null;

    @BeforeClass
    public void initElements() {
        menuButton = new UiButton(actionButtonLocator);
        seekBar = new UiObject(seekBarLocator);
        seekBarTextView = new UiTextView(textViewLocator);
        iosCheckBox = new UiObject(checkBoxiOSLocator);
        androidCheckBox = new UiObject(checkBoxAndroidLocator);
        checkBoxTextView = new UiTextView(valueTextViewLocator);
    }

    @Test
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testCheckBox() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(actionButtonLocator);
        menuButton.click(longPressButtonLocator);
        WebDriverWaitUtils.waitUntilElementIsVisible(longPressButtonLocator);
        menuButton.click(androidCheckBox);
        androidCheckBox.click();
        Assert.assertEquals(checkBoxTextView.getText(), "Android");
        iosCheckBox.click();
        Assert.assertEquals(checkBoxTextView.getText(), "iOS");
        iosCheckBox.click();
    }

    @Test
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testSeekBar() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(actionButtonLocator);
        menuButton.click();
        WebDriverWaitUtils.waitUntilElementIsVisible(longPressButtonLocator);
        menuButton.click(seekBar);
        seekBar.swipeRight();
        Assert.assertEquals(seekBarTextView.getText(), "Value: 100", "Seek Bar swipe right value does not match");
        seekBar = new UiObject(seekBarLocator);
        seekBar.swipeLeft();
        Assert.assertEquals(seekBarTextView.getText(), "Value: 0", "Seek Bar swipe right value does not match");
    }

}
