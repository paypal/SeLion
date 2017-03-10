/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2017 PayPal                                                                                     |
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

package com.paypal.selion.appium.android.sample;


import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.android.UiButton;
import com.paypal.selion.platform.mobile.android.UiObject;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidScrollTest {

    private static final String PAGE_OBJECTS_APP_PATH = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String MESSGAE_BOX_LOCATOR = "android:id/message";
    private static final String MENU_LOCATOR ="com.paypal.selion.pageobjectsdemoapp:id/btnNext";
    private static final String LIST_VIEW_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/tableListView";

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
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH)
    public void testSwipeActions() {
        UiObject sampleCell = new UiObject("android:id/text1");
        UiButton sampleButton = new UiButton("android:id/button1");
        WebDriverWaitUtils.waitUntilElementIsVisible(MENU_LOCATOR);
        menuButton.click(MENU_LOCATOR);
        menuButton.click(MENU_LOCATOR);
        menuButton.click(MENU_LOCATOR);
        menuButton.click(scrollView);
        scrollView.swipeUp();
        sampleCell.click(sampleButton);
        Assert.assertNotEquals("You clicked :One", messageBox.getText());
        sampleButton.click();
        scrollView.swipeDown();
        sampleCell.click(sampleButton);
        Assert.assertEquals("You clicked :One", messageBox.getText());
        sampleButton.click();
    }

}
