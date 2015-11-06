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
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.android.UiObject;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidTextViewTest {

    private static final String PAGE_OBJECTS_APP_PATH = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String DEVICE_NAME = "android:19";
    private static final String TEXT_VIEW_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/page_objects_text_view";
    
    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH, device = DEVICE_NAME)
    public void testTextView() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(TEXT_VIEW_LOCATOR);
        UiObject uiObject = new UiObject(TEXT_VIEW_LOCATOR);
        Assert.assertEquals(uiObject.getText(), "Page Objects Demo", "Text View value does not match");
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH, device = DEVICE_NAME)
    public void testTextViewEnabled() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(TEXT_VIEW_LOCATOR);
        UiObject uiObject = new UiObject(TEXT_VIEW_LOCATOR);
        Assert.assertEquals(uiObject.isEnabled(), true, "Text View is not enabled");
    }

}
