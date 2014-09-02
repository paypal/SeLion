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

package ${package}.sample.selion;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.testng.Reporter;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.MobileReporter;

/**
 * This test class demonstrates how to use SeLion for running tests against a Native iOS app.
 *  
 */
public class IOSNativeAppDemoTest {

    // Through this annotation we basically let SeLion know that we would be needing an iOS Simulator spawned and made ready.
    @MobileTest(appName = "InternationalMountains")
    @Test
    public void testMethod() throws InterruptedException {
        //If we are looking at taking screenshots that are to be appearing in the SeLion generated reports, this is the way
        //to go about doing it.
        MobileReporter.log("My Screenshot 1", true);
        //To gain access to the IOSRemoteWebDriver, we use the thread safe helper method Grid.iOSDriver() which provides
        //an instance of IOSRemoteWebDriver for the current Test method. 
        List<WebElement> cells = Grid.iOSDriver().findElements(By.className("UIATableCell"));
        assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        MobileReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.iOSDriver().findElement(selector);
        Reporter.log(text.getAttribute("name"),true);
    }
}
