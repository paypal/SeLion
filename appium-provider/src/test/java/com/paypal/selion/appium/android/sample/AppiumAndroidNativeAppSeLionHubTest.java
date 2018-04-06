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
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidNativeAppSeLionHubTest {

    @Test
    @MobileTest(appPath = "selion-hub-storage:userName:folderName:selendroid-test-app-0.15.0.apk", device = "android:5.0.1", deviceType = "Android Emulator")
    public void testWithNativeAppWithSelionHubAppNameProvidedWithfolderName() {

        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
        WebElement textField = driver.findElement(By.id("io.selendroid.testapp:id/my_text_field"));
        assertEquals("true", textField.getAttribute("enabled"));
        textField.sendKeys("Appium Android Native Test");
        assertEquals("Appium Android Native Test", textField.getText());

    }

    @Test
    @MobileTest(appPath = "selion-hub-storage:userName:selendroid-test-app-0.15.0.apk", device = "android:5.0.1", deviceType = "Android Emulator")
    public void testWithNativeAppWithSelionHubAppNameProvided() {

        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
        WebElement textField = driver.findElement(By.id("io.selendroid.testapp:id/my_text_field"));
        assertEquals("true", textField.getAttribute("enabled"));
        textField.sendKeys("Appium Android Native Test");
        assertEquals("Appium Android Native Test", textField.getText());

    }

}
