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

package ${package}.sample.selion;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.testng.Reporter;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.SeLionReporter;

/**
 * This class has test cases that demonstrates how to use SeLion for running tests against IOS apps using appium.
 */
public class AppiumIOSDemoTest{

    /**
     * This test demonstrates how to use SeLion for running tests against a Native IOS app using appium.
     * <ul>
     * <li>
     * An appium instance/server should be locally installed and running and point SeLion to this server using any of the following options. 
     * <ol>
     * <li>Through the JVM arguments -DSELION_SELENIUM_HOST and -DSELION_SELENIUM_PORT </li> (or)
     * <li>Through suite file &lt;parameter name="seleniumhost" value=""/&gt; and &lt;parameter name="seleniumport" value=""/&gt;</li>
     * </ol></li>
     * For setting up Appium iOS refer http://appium.io/slate/en/master/?ruby#system-setup-(ios)
     * </li>
     * <li>
     * The app InternationalMountains.app to be tested should be placed in the 
     * Current Working directory(src/test/resources).</li>
     * </ul>
     */
    @MobileTest(appPath = "src/test/resources/apps/InternationalMountains.app", device = "iphone:8.1",
        deviceType = "iPhone Simulator")
    @Test
    public void testWithNativeApp() throws InterruptedException {
        //Log a screenshot to the report and label it "My Screenshot 1"
        SeLionReporter.log("My Screenshot 1", true);
        //To gain access to the IOSRemoteWebDriver, we use the thread safe helper method Grid.driver() which provides
        //an instance of IOSRemoteWebDriver for the current Test method. 
        List<WebElement> cells = Grid.driver().findElements(By.className("UIATableCell"));
        assertEquals(9, cells.size());
        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);
        //Log a screenshot to the report and label it "My Screenshot 2"
        SeLionReporter.log("My Screenshot 2", true);
        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.driver().findElement(selector);
        Reporter.log(text.getAttribute("name"),true);
    }
    
    /**
     * This test demonstrates how to use SeLion for running tests against IOS safari using appium.
     * <ul>
     * <li>
     * An appium instance/server should be installed and running where selenium host and port should be 
     * configured to the same appium instance.</li> 
     * <li>
     * For setting up Appium iOS refer http://appium.io/slate/en/master/?ruby#system-setup-(ios)
     * </li>
     * </ul>
     */
    @Test
    @MobileTest(appName = "safari", device = "iphone:8.1", deviceType = "iPhone Simulator")
    public void testWithSafari() {
        //To gain access to the IOSRemoteWebDriver, we use the thread safe helper method Grid.driver() which provides
        //an instance of IOSRemoteWebDriver for the current Test method. 
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
        // take a screenshot
        SeLionReporter.log("cheese!", true);
    }
}
