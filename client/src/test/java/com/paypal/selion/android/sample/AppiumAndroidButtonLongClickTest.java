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
public class AppiumAndroidButtonLongClickTest {

    private static final String pageObjectsAppPath = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String deviceName = "android:19";
    private final String actionButtonLocator = "com.paypal.selion.pageobjectsdemoapp:id/action_button";
    private final String longPressButtonLocator = "com.paypal.selion.pageobjectsdemoapp:id/long_press_button";
    private final String textViewLocator = "com.paypal.selion.pageobjectsdemoapp:id/long_press_button_output";

    private UiButton menuButton = null;
    private UiButton longPressButton = null;
    private UiTextView textView = null;

    @BeforeClass
    public void initElements() {
        menuButton = new UiButton(actionButtonLocator);
        longPressButton = new UiButton(longPressButtonLocator);
        textView = new UiTextView(textViewLocator);
    }

    @Test(enabled = true)
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testLongClickButtonProperties() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(actionButtonLocator);
        menuButton.click(longPressButton);
        Assert.assertEquals(longPressButton.isLongClickable(), true, "Button is not long clickable");
    }

    @Test(enabled = true)
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testButtonClick() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(actionButtonLocator);
        menuButton.click(longPressButton);
        longPressButton.longClick(textView);
        String output = textView.getText();
        Assert.assertEquals(output.contains("long press"), true, "Button center click not working properly");
    }

    @Test(enabled = true)
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testButtonClickTopLeft() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(actionButtonLocator);
        menuButton.click(longPressButton);
        longPressButton.longClickTopLeft(textView);
        String output = textView.getText();
        Assert.assertEquals(output.contains("long press"), true, "Button top left click not working properly");
    }

    @Test(enabled = true)
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testButtonClickBottomRight() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(actionButtonLocator);
        menuButton.click(longPressButton);
        WebDriverWaitUtils.waitUntilElementIsVisible(longPressButtonLocator);
        longPressButton.longClickBottomRight(textView);
        String output = textView.getText();
        Assert.assertEquals(output.contains("long press"), true, "Button bottom right click not working properly");
    }
}
