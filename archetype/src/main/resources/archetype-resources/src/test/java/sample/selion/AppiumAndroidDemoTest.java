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
import static org.testng.Assert.assertNotNull;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.selion.reports.runtime.SeLionReporter;

/**
 * This class has test cases that demonstrates how to use SeLion for running tests against ANDROID apps using appium.
 */
public class AppiumAndroidDemoTest {

    /**
     * This test demonstrates how to use SeLion for running tests against a Native ANDROID app using appium.
     * <ul>
     * <li>
     * An appium instance/server should be locally installed and running and point SeLion to this server using any of the following options. 
     * <ol>
     * <li>Through the JVM arguments -DSELION_SELENIUM_HOST and -DSELION_SELENIUM_PORT </li> (or)
     * <li>Through suite file &lt;parameter name="seleniumhost" value=""/&gt; and &lt;parameter name="seleniumport" value=""/&gt;</li>
     * </ol></li>
     * <li>
     * For setting up Appium Android refer http://appium.io/slate/en/master/?ruby#system-setup-(android)
     * </li>
     * </ul>
     */
    @Test
    @MobileTest(appPath = "src/test/resources/apps/selendroid-test-app-0.15.0.apk", device = "android:5.0.1",
        deviceType = "Android Emulator")
    public void testWithNativeApp() {
        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
        WebElement textField = driver.findElement(By.id("io.selendroid.testapp:id/my_text_field"));
        assertEquals("true", textField.getAttribute("enabled"));
        textField.sendKeys("Appium Android Native Test");
        assertEquals("Appium Android Native Test", textField.getText());
    }

    /**
     * This test demonstrates how to use SeLion for running tests against ANDROID browser using appium.
     * <ul>
     * <li>
     * An appium instance/server should be installed and running where selenium host and port should be 
     * pointed to this instance.</li>
     * <li>
     * For setting up Appium Android refer http://appium.io/slate/en/master/?ruby#system-setup-(android)
     * </li>
     * </ul> 
     */
    @Test
    @MobileTest(appName = "Browser", device = "android:5.0.1", deviceType = "Android Emulator")
    public void testWithBrowser() {
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
        SeLionReporter.log("cheese!", true);
    }
}
