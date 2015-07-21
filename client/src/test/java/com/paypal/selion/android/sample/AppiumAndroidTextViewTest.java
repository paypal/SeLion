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

    private static final String pageObjectsAppPath = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String deviceName = "android:19";
    private static String texViewLocator = "com.paypal.selion.pageobjectsdemoapp:id/page_objects_text_view";
    
    @Test
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testTextView() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(texViewLocator);
        UiObject uiObject = new UiObject(texViewLocator);
        Assert.assertEquals(uiObject.getText(), "Page Objects Demo", "Text View value does not match");
    }

    @Test
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testTextViewEnabled() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(texViewLocator);
        UiObject uiObject = new UiObject(texViewLocator);
        Assert.assertEquals(uiObject.isEnabled(), true, "Text View is not enabled");
    }

}
