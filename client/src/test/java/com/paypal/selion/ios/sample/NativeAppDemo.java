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

package com.paypal.selion.ios.sample;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.MobileReporter;

public class NativeAppDemo {

    @MobileTest(appName = "InternationalMountains", device = "iphone7.1", deviceSerial = "iPhone4s")
    @Test
    public void testSDKDeviceVariation4s() throws InterruptedException {
        MobileReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.iOSDriver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        MobileReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.iOSDriver().findElement(selector);
        System.out.println(text.getAttribute("name"));
    }

    @MobileTest(appName = "InternationalMountains:1.3", device = "iphone:7.1", deviceType = "iPhone5s")
    @Test
    public void testSDKDeviceVariation5s() throws InterruptedException {
        MobileReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.iOSDriver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        MobileReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.iOSDriver().findElement(selector);
        System.out.println(text.getAttribute("name"));
    }

    @MobileTest(appName = "InternationalMountains", device = "iphone")
    @Test
    public void testIOSDefaults() throws InterruptedException {
        MobileReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.iOSDriver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        MobileReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.iOSDriver().findElement(selector);
        System.out.println(text.getAttribute("name"));
    }

    @MobileTest(appName = "InternationalMountains:1.3", device = "iphone", deviceType = "iPhone6")
    @Test
    public void testIOSDefaultsIphone6() throws InterruptedException {
        MobileReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.iOSDriver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        MobileReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.iOSDriver().findElement(selector);
        System.out.println(text.getAttribute("name"));
    }

    @MobileTest(appName = "Safari", device = "ipad")
    @Test
    public void testIOSDefaultsIpad() throws InterruptedException {
        MobileReporter.log("My Screenshot 1", true);
        Grid.open("http://www.paypal.com");
    }

    @MobileTest(appName = "Safari", device = "ipad:8.0", deviceType = "iPadAir")
    @Test
    public void testIOSDefaultsIpadAir() throws InterruptedException {
        MobileReporter.log("My Screenshot 1", true);
        Grid.open("http://www.paypal.com");
    }
}
