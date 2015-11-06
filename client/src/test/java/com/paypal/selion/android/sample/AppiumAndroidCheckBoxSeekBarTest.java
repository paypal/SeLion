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

    private static final String PAGE_OBJECTS_APP_PATH = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String DEVICE_NAME = "android:19";
    private static final String ACTION_BUTTON_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/action_button";
    private static final String SEEK_BAR_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/seekBar";
    private static final String LONG_PRESS_BUTTON_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/long_press_button";
    private static final String TEXT_VIEW_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/seekBar_textview";
    // Check box
    private static final String CHECKBOX_ANDROID_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/android_checkbox";
    private static final String CHECKBOX_IOS_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/ios_checkbox";
    private static final String VALUE_TEXTVIEW_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/checkbox_textview";

    private UiButton menuButton;
    private UiObject seekBar;
    private UiTextView seekBarTextView;
    private UiTextView checkBoxTextView;
    private UiObject iosCheckBox;
    private UiObject androidCheckBox;

    @BeforeClass
    public void initElements() {
        menuButton = new UiButton(ACTION_BUTTON_LOCATOR);
        seekBar = new UiObject(SEEK_BAR_LOCATOR);
        seekBarTextView = new UiTextView(TEXT_VIEW_LOCATOR);
        iosCheckBox = new UiObject(CHECKBOX_IOS_LOCATOR);
        androidCheckBox = new UiObject(CHECKBOX_ANDROID_LOCATOR);
        checkBoxTextView = new UiTextView(VALUE_TEXTVIEW_LOCATOR);
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH, device = DEVICE_NAME)
    public void testCheckBox() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(ACTION_BUTTON_LOCATOR);
        menuButton.click(LONG_PRESS_BUTTON_LOCATOR);
        WebDriverWaitUtils.waitUntilElementIsVisible(LONG_PRESS_BUTTON_LOCATOR);
        menuButton.click(androidCheckBox);
        androidCheckBox.click();
        Assert.assertEquals(checkBoxTextView.getText(), "Android");
        iosCheckBox.click();
        Assert.assertEquals(checkBoxTextView.getText(), "iOS");
        iosCheckBox.click();
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH, device = DEVICE_NAME)
    public void testSeekBar() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(ACTION_BUTTON_LOCATOR);
        menuButton.click();
        WebDriverWaitUtils.waitUntilElementIsVisible(LONG_PRESS_BUTTON_LOCATOR);
        menuButton.click(seekBar);
        seekBar.swipeRight();
        Assert.assertEquals(seekBarTextView.getText(), "Value: 100", "Seek Bar swipe right value does not match");
        seekBar = new UiObject(SEEK_BAR_LOCATOR);
        seekBar.swipeLeft();
        Assert.assertEquals(seekBarTextView.getText(), "Value: 0", "Seek Bar swipe right value does not match");
    }

}
