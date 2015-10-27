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
package ${package}.sample.selion;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.selion.reports.runtime.SeLionReporter;
/**
 * This class has test cases that demonstrates how to use SeLion for running tests against mobile apps using sauce cloud.
 */
public class AppiumSauceCloudTest {

    /**
     * This test demonstrates how to use SeLion for running tests against a Native ANDROID app in sauce cloud.
     * This selendroid-test-app-0.15.0.apk must be uploaded to sauce temporary storage before running this test case.
     */
    @Test
    @MobileTest(appPath = "sauce-storage:selendroid-test-app-0.15.0.apk", device = "android:4.3",
        deviceType = "Android Emulator", additionalCapabilities = { "appiumVersion:1.4.13" })
    public void testWithNativeAndroidApp() throws Exception {
        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
        WebElement textField = driver.findElement(By.id("io.selendroid.testapp:id/my_text_field"));
        assertEquals("true", textField.getAttribute("enabled"));
        textField.sendKeys("Appium Android Native Test");
        assertEquals("Appium Android Native Test", textField.getText());

    }
    
    /**
     * This test demonstrates how to use SeLion for running tests against a Native IOS app in sauce cloud. 
     * InternationalMountains.app.zip must be uploaded to sauce temporary storage before running this test case.
     */
    @Test
    @MobileTest(appPath = "sauce-storage:InternationalMountains.app.zip", device = "iphone:8.1",
        deviceType = "iPhone Simulator", additionalCapabilities = { "appiumVersion:1.4.13" })
    public void testWithNativeIOSApp() throws InterruptedException {
        SeLionReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.driver().findElements(By.className("UIATableCell"));
        assertEquals(9, cells.size());
        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);
        SeLionReporter.log("My Screenshot 2", true);
    }

}
