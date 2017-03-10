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

package com.paypal.selion.iosdriver.ios.sample;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.mobile.UIOperationFailedException;
import com.paypal.selion.platform.mobile.ios.UIANavigationBar;
import com.paypal.selion.platform.mobile.ios.UIAPicker;
import com.paypal.selion.platform.mobile.ios.UIAStaticText;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class IOSDriverPickerTest {

    private static final String appFolder = "/apps";

    @BeforeClass
    public void setup() {
        URL url = IOSDriverPickerTest.class.getResource(appFolder);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testPickerValues() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 5; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        UIAPicker picker = new UIAPicker("xpath=//UIAApplication[1]/UIAWindow[1]/UIAPicker[1]");
        List<String> values = picker.getValuesOfWheelAtIndex(0);
        Assert.assertEquals(values.toString(), "[One, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten]",
                "Picker wheel values do not match");
    }

    @MobileTest(appName = "PageObjects")
    @Test
    public void testSetPickerValue() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 5; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        UIAPicker picker = new UIAPicker("xpath=//UIAApplication[1]/UIAWindow[1]/UIAPicker[1]");
        picker.setValueOfWheelAtIndex(0, "Nine");
        UIAStaticText pickerLabel = new UIAStaticText("xpath=//UIAApplication[1]/UIAWindow[1]/UIAStaticText[1]");
        Assert.assertEquals(pickerLabel.getValue(), "Nine", "Value set to the picker wheel does not match");
    }

    @Test(expectedExceptions = InvalidSelectorException.class)
    @MobileTest(appName = "PageObjects")
    public void testInvalidXPath() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 5; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        UIAPicker picker = new UIAPicker("xpath=//UIAApplication[1]/UIAWindow[1]/UIAPicker[]");
        picker.getValuesOfWheelAtIndex(1);
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    @MobileTest(appName = "PageObjects")
    public void testInvalidPickerLocator() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 5; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        UIAPicker picker = new UIAPicker("xpath=//UIAApplication[1]/UIAWindow[1]/UIAPicker[2]");
        picker.getValuesOfWheelAtIndex(0);
    }

    @Test(expectedExceptions = UIOperationFailedException.class)
    @MobileTest(appName = "PageObjects")
    public void testInvalidWheelAccess() throws InterruptedException {
        UIANavigationBar navigationBar = null;
        for (int i = 0; i < 5; i++) {
            navigationBar = new UIANavigationBar("xpath=//UIAApplication[1]/UIAWindow[1]/UIANavigationBar[1]");
            navigationBar.clickRightButton();
            Thread.sleep(500);
        }
        UIAPicker picker = new UIAPicker("xpath=//UIAApplication[1]/UIAWindow[1]/UIAPicker[1]");
        picker.getValuesOfWheelAtIndex(1);
    }

    @AfterClass
    public void teardown() {
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER,
                Config.ConfigProperty.MOBILE_APP_FOLDER.getDefaultValue());
    }

}
