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

package com.paypal.selion.ios.sample;

import static org.testng.Assert.assertNotNull;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.SeLionReporter;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AppiumIOSTest {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/InternationalMountains.app", device = "iphone:8.1", deviceType = "iPhone Simulator")
    public void testWithNativeAppAllInternationalMountains() throws InterruptedException {
        SeLionReporter.log("My Screenshot 1", true, true);
        List<WebElement> cells = Grid.driver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot
        SeLionReporter.log("My Screenshot 2", true, true);

    }

    @Test
    @MobileTest(appName = "safari", device = "iphone:8.1", deviceType = "iPhone Simulator")
    public void testWithSafari() {
        RemoteWebDriver driver = Grid.driver();
        assertNotNull(driver);

        // And now use this to visit Google
        driver.get("http://www.google.com");
        SeLionReporter.log("Page loaded", true, true);
        // Find the text input element by its id
        WebElement element = driver.findElement(By.id("lst-ib"));
        // Enter something to search for
        element.sendKeys("Cheese!");
        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();
        // take a screenshot
        SeLionReporter.log("cheese!", true, true);
    }

}
