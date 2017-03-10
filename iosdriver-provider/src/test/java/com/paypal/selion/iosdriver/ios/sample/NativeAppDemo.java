/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.SeLionReporter;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class NativeAppDemo {

    private static final String appFolder = "/apps";

    @BeforeClass
    public void setup() {
        URL url = NativeAppDemo.class.getResource(appFolder);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @MobileTest(appName = "InternationalMountains", device = "iphone:7.1", deviceType = "iPhone4s")
    @Test
    public void testSDKDeviceVariation4s() throws InterruptedException {
        SeLionReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.driver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        SeLionReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.driver().findElement(selector);
        Assert.assertNotNull(text.getAttribute("name"));
    }

    @MobileTest(appName = "InternationalMountains:1.3", device = "iphone:7.1", deviceType = "iPhone5s")
    @Test
    public void testSDKDeviceVariation5s() throws InterruptedException {
        SeLionReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.driver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        SeLionReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.driver().findElement(selector);
        Assert.assertNotNull(text.getAttribute("name"));
    }

    @MobileTest(appName = "InternationalMountains", device = "iphone")
    @Test
    public void testIOSDefaults() throws InterruptedException {
        SeLionReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.driver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        SeLionReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.driver().findElement(selector);
        Assert.assertNotNull(text.getAttribute("name"));
    }

    @MobileTest(appName = "InternationalMountains:1.3", device = "iphone", deviceType = "iPhone6")
    @Test
    public void testIOSDefaultsIphone6() throws InterruptedException {
        SeLionReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.driver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        SeLionReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.driver().findElement(selector);
        Assert.assertNotNull(text.getAttribute("name"));
    }

    @AfterClass
    public void teardown() {
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER,
                Config.ConfigProperty.MOBILE_APP_FOLDER.getDefaultValue());
    }

}
