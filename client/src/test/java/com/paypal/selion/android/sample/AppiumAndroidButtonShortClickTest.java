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
import com.paypal.selion.platform.mobile.android.UiTextView;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidButtonShortClickTest {

    private static final String PAGE_OBJECTS_APP_PATH = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String DEVICE_NAME = "android:19";
    private static final String ACTION_BUTTON_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/action_button";
    private static final String SHORT_PRESS_BUTTON_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/short_press_button";
    private static final String TEXT_VIEW_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/short_press_button_output";

    UiButton menuButton;
    UiButton shortPressButton;
    UiTextView textView;

    @BeforeClass
    public void initElements() {
        menuButton = new UiButton(ACTION_BUTTON_LOCATOR);
        shortPressButton = new UiButton(SHORT_PRESS_BUTTON_LOCATOR);
        textView = new UiTextView(TEXT_VIEW_LOCATOR);
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH, device = DEVICE_NAME)
    public void testButtonClick() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(ACTION_BUTTON_LOCATOR);
        menuButton.click(shortPressButton);
        shortPressButton.click(textView);
        String output = textView.getText();
        Assert.assertEquals(output.contains("short press"), true, "Button center click not working properly");
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH, device = DEVICE_NAME)
    public void testButtonClickTopLeft() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(ACTION_BUTTON_LOCATOR);
        menuButton.click(shortPressButton);
        WebDriverWaitUtils.waitUntilElementIsVisible(SHORT_PRESS_BUTTON_LOCATOR);
        shortPressButton.clickTopLeft(textView);
        String output = textView.getText();
        Assert.assertEquals(output.contains("short press"), true, "Button top left click not working properly");
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH, device = DEVICE_NAME)
    public void testButtonClickBottomRight() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(ACTION_BUTTON_LOCATOR);
        menuButton.click(shortPressButton);
        shortPressButton.clickBottomRight(textView);
        String output = textView.getText();
        Assert.assertEquals(output.contains("short press"), true, "Button bottom right click not working properly");
    }
}
