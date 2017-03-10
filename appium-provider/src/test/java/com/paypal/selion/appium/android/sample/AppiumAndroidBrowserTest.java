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
import com.paypal.selion.reports.runtime.SeLionReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumAndroidBrowserTest {

    /**
     * This test case tested against the chrome browser in android emulator. Because of the chromedriver dependency,
     * this test case is expected to run on the latest chromedriver(2.14.313457) and android (platform 5.0.1, api -21)
     * versions.
     */
    @Test
    @MobileTest(appName = "Chrome", device = "android:5.1", deviceType = "Android Emulator")
    public void testWithChrome() {
        RemoteWebDriver driver = Grid.driver();
        assertNotNull(driver);

        // And now use this to visit Google
        driver.get("http://www.google.com");

        // Find the text input element by its Id
        WebElement element = driver.findElement(By.id("lst-ib"));
        // Enter something to search for
        element.sendKeys("Cheese!");
        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        SeLionReporter.log("cheese!", true);
    }

}
