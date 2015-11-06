/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.selion.reports.runtime.SeLionReporter;

/*
 * DEVNOTE Tests in this class exist primarily for demonstration purposes and as a basic sanity checks.
 */
public class AndroidTest {
    private static final String APP_FOLDER = "/apps";

    @BeforeClass
    public void setup() {
        URL url = AndroidTest.class.getResource(APP_FOLDER);
        Config.setConfigProperty(Config.ConfigProperty.MOBILE_APP_FOLDER, (new File(url.getPath()).getAbsolutePath()));
    }

    @Test(groups = {"functional-mobile, selendroid"})
    @MobileTest(appName = "android", device = "android:19")
    public void testLaunch() throws Exception {
        RemoteWebDriver driver = Grid.driver();
        assertNotNull(driver);
        // And now use this to visit Google
        driver.get("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        SeLionReporter.log("cheese!", true, true);

    }
    
    @Test(groups = {"functional-mobile, selendroid"})
    @MobileTest(appName = "io.selendroid.testapp:0.15.0", device = "android:19")
    public void testNative() {
        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("//EditText[@id='my_text_field']");
        WebElement inputField = driver.findElement(By.xpath("//EditText[@id='my_text_field']"));
        assertEquals(inputField.getAttribute("enabled"), "true");
        inputField.sendKeys("Selendroid");
        assertEquals(inputField.getText(), "Selendroid");
    }
}
