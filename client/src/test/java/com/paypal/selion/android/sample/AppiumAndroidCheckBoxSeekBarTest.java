/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

import com.paypal.selion.platform.mobile.elements.AbstractMobileElement;
import com.paypal.selion.platform.mobile.elements.MobileButton;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidCheckBoxSeekBarTest {

    private static final String PAGE_OBJECTS_APP_PATH = "src/test/resources/apps/PageObjectsDemoApp.apk";
    private static final String ACTION_BUTTON_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/btnNext";
    private static final String SEEK_BAR_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/stateSeekBar";
    private static final String TEXT_VIEW_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/stateTextSlider";
    // Check box
    private static final String CHECKBOX_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/stateSwitch";
    private static final String VALUE_TEXTVIEW_LOCATOR = "com.paypal.selion.pageobjectsdemoapp:id/stateTxtSwitch";

    private MobileButton menuButton;
    private AbstractMobileElement seekBar;
    private MobileTextField seekBarTextView;
    private MobileTextField checkBoxTextView;
    private AbstractMobileElement checkBox;

    @BeforeClass
    public void initElements() {
        menuButton = new MobileButton(ACTION_BUTTON_LOCATOR);
        seekBar = new AbstractMobileElement(SEEK_BAR_LOCATOR);
        seekBarTextView = new MobileTextField(TEXT_VIEW_LOCATOR);
        checkBox = new AbstractMobileElement(CHECKBOX_LOCATOR);
        checkBoxTextView = new MobileTextField(VALUE_TEXTVIEW_LOCATOR);
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH)
    public void testCheckBox() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(ACTION_BUTTON_LOCATOR);
        menuButton.click(ACTION_BUTTON_LOCATOR);
        menuButton.click(ACTION_BUTTON_LOCATOR);
        menuButton.click(checkBox);
        checkBox.click();
        Assert.assertEquals(checkBoxTextView.getText(), "Switch is OFF");
        checkBox.click();
        Assert.assertEquals(checkBoxTextView.getText(), "Switch is ON");
        checkBox.click();
    }

    @Test
    @MobileTest(appPath = PAGE_OBJECTS_APP_PATH)
    public void testSeekBar() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible(ACTION_BUTTON_LOCATOR);
        menuButton.click(ACTION_BUTTON_LOCATOR);
        menuButton.click(ACTION_BUTTON_LOCATOR);
        menuButton.click(seekBar);
        seekBar.swipeRight();
        Assert.assertEquals(seekBarTextView.getText(), "1.000000", "Seek Bar swipe right value does not match");
        seekBar = new AbstractMobileElement(SEEK_BAR_LOCATOR);
        seekBar.swipeLeft();
        Assert.assertEquals(seekBarTextView.getText(), "0.000000", "Seek Bar swipe right value does not match");
    }

}
