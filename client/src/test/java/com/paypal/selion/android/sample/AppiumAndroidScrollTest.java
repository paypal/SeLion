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

    private static final String pageObjectsAppPath = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String deviceName = "android:19";
    private String messageBoxLocator = "android:id/message";
    private String menuLocator ="com.paypal.selion.pageobjectsdemoapp:id/action_button";
    private String listViewLocator = "com.paypal.selion.pageobjectsdemoapp:id/scroll_view";
    
    private UiButton menuButton;
    private UiObject scrollView;
    private UiObject messageBox;

    @BeforeClass
    public void initElements(){
        menuButton = new UiButton(menuLocator);
        scrollView = new UiObject(listViewLocator);
        messageBox = new UiObject(messageBoxLocator);
    }
    
    @Test
    @MobileTest(appPath = pageObjectsAppPath, device = deviceName)
    public void testSwipeActions() {
        UiObject sampleCell = new UiObject("com.paypal.selion.pageobjectsdemoapp:id/TextView13");
        UiButton sampleButton = new UiButton("android:id/button1");
        WebDriverWaitUtils.waitUntilElementIsVisible(menuLocator);
        menuButton.click(menuLocator);
        menuButton.click(menuLocator);
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
