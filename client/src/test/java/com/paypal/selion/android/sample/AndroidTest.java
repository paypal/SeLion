/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

import static org.testng.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.MobileReporter;

public class AndroidTest {
    @Test
    @MobileTest(appName = "android", device = "android19")
    public void testLaunch() throws Exception {
        RemoteWebDriver driver = Grid.selendroidDriver();
        assertNotNull(driver);

        // And now use this to visit Google
        driver.get("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        MobileReporter.log("cheese!", true);

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
    }

    @Test
    @MobileTest(appName = "com.paypal.here:2.0.0", device = "android19")
    public void testLaunch_PPH() throws Exception {
        RemoteWebDriver driver = Grid.selendroidDriver();
        assertNotNull(driver);
        MobileReporter.log("PPH!", true);
    }
}
