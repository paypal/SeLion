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
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidScrollTest {

    private static final String PAGE_OBJECTS_APP_PATH = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String DEVICE_NAME = "android:19";
    private static final String MESSGAE_BOX_LOCATOR = "android:id/message";
    private static final String MENU_LOCATOR ="com.paypal.selion.pageobjectsdemoapp:id/action_button";
    private static final String LIST_VIEW_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/scroll_view";
    
    private UiButton menuButton;
    private UiObject scrollView;
    private UiObject messageBox;

    @BeforeClass
    public void initElements(){
        menuButton = new UiButton(MENU_LOCATOR);
        scrollView = new UiObject(LIST_VIEW_LOCATOR);
        messageBox = new UiObject(MESSGAE_BOX_LOCATOR);
    }
    
    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH, device = DEVICE_NAME)
    public void testSwipeActions() {
        UiObject sampleCell = new UiObject("com.paypal.selion.pageobjectsdemoapp:id/TextView13");
        UiButton sampleButton = new UiButton("android:id/button1");
        WebDriverWaitUtils.waitUntilElementIsVisible(MENU_LOCATOR);
        menuButton.click(MENU_LOCATOR);
        menuButton.click(MENU_LOCATOR);
        menuButton.click(scrollView);
        scrollView.swipeUp();
        sampleCell.click(sampleButton);
        Assert.assertEquals("Cell 13", messageBox.getText());
        sampleButton.click();
        scrollView.swipeDown();
        sampleCell = new UiObject("com.paypal.selion.pageobjectsdemoapp:id/TextView2");
        sampleCell.click(sampleButton);
        Assert.assertEquals("Cell 2", messageBox.getText());
        sampleButton.click();
    }

}
