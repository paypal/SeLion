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

import java.io.File;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.grid.SeLionSelendroidDriver;
import com.paypal.selion.platform.mobile.android.UiButton;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class SelendroidDriverScrollTest {

    private static final String APP_FOLDER = "/apps";

    @BeforeClass
    public void setup() {
        URL url = AndroidTest.class.getResource(APP_FOLDER);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @Test
    @MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", device = "android:19")
    public void testDriverScroll() throws InterruptedException {
        WebDriverWaitUtils.waitUntilElementIsVisible("id=action_button");
        UiButton uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ActionMenuItemView[@name='Touch']");
        uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ActionMenuItemView[@name='Scroll']");
        uiObject = new UiButton("id=action_button");
        uiObject.click("xpath=//ScrollView[@id='scroll_view']");

        SeLionSelendroidDriver selendroidDriver = ((SeLionSelendroidDriver) Grid.driver());
        selendroidDriver.scrollUp();
        WebElement cell13 = Grid.driver().findElement(By.xpath("//TextView[@id='TextView13']"));
        Assert.assertEquals(cell13.isDisplayed(), true, "Cell 13 is not visible after scroll");
        Thread.sleep(5 * 1000);
        selendroidDriver.scrollDown();
        WebElement cell3 = Grid.driver().findElement(By.xpath("//TextView[@id='TextView13']"));
        Assert.assertEquals(cell3.isDisplayed(), true, "Cell 3 is not visible after scroll");
    }
}
