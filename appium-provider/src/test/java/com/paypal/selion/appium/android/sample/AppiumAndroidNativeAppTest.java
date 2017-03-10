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
import com.paypal.selion.reports.runtime.SeLionReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidNativeAppTest {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/selendroid-test-app-0.15.0.apk", device = "android:5.1", deviceType = "Android Emulator", mobileNodeType = "appium")
    public void testWithNativeApp() {

        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
        SeLionReporter.log("Main page", true, true);
        WebElement textField = driver.findElement(By.id("io.selendroid.testapp:id/my_text_field"));
        assertEquals("true", textField.getAttribute("enabled"));
        textField.sendKeys("Appium Android Native Test");
        SeLionReporter.log("Entered text", true, true);
        assertEquals("Appium Android Native Test", textField.getText());

    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "Either you have provided both appPath and appName or you have specified nothing. Please specify either appPath or appName")
    @MobileTest(appPath = "src/test/resources/apps/selendroid-test-app-0.15.0.apk", appName = "io.selendroid.testapp:0.14.0", device = "android:5.0.1", deviceType = "Android Emulator")
    public void testWithNativeAppBothAppNameAndAppPathProvided() {

        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
        WebElement textField = driver.findElement(By.id("io.selendroid.testapp:id/my_text_field"));
        assertEquals("true", textField.getAttribute("enabled"));
        textField.sendKeys("Appium Android Native Test");
        assertEquals("Appium Android Native Test", textField.getText());

    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "Either you have provided both appPath and appName or you have specified nothing. Please specify either appPath or appName")
    @MobileTest(appPath = "", device = "android:5.0.1", deviceType = "Android Emulator")
    public void testWithNativeAppBothAppNameAndAppPathNotProvided() {

        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
        WebElement textField = driver.findElement(By.id("io.selendroid.testapp:id/my_text_field"));
        assertEquals("true", textField.getAttribute("enabled"));
        textField.sendKeys("Appium Android Native Test");
        assertEquals("Appium Android Native Test", textField.getText());

    }

}
