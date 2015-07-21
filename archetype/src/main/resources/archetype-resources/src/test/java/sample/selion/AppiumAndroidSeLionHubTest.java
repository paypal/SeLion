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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/**
 * This class has test cases that demonstrates how to use SeLion for running tests against mobile apps using appium 
 * and apps placed in selion hub storage.
 */
public class AppiumAndroidSeLionHubTest {

    /**
     * This test demonstrates how to use SeLion for running tests against a Native ANDROID app using appium 
     * and apps placed in selion hub storage. 
     * <ul>
     * <li>
     * An instance of selion grid should be spawned and the appium instance should be registered to this grid.</li>
     * <li>
     * The app selendroid-test-app-0.14.0.apk to be tested should be uploaded to SeLion hub storage 
     * provided by SeLion grid.</li>
     * <li>
     * The format of the appPath for the apps in the SeLion hub storage is as follows 
     * selion-hub-storage:{user-guid}:{folderName}:{file} where user-guid is a random unique guid and it is mandatory 
     * while uploading a file. foldername is an optional parameter and this test case is configured without folder name.</li>
     * </ul>
     */
    @Test
    @MobileTest(appPath = "selion-hub-storage:guid:selendroid-test-app-0.15.0.apk",
        device = "android:5.0.1", deviceType = "Android Emulator")
    public void testWithNativeAppWithSelionHub() {
        RemoteWebDriver driver = Grid.driver();
        WebDriverWaitUtils.waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
        WebElement textField = driver.findElement(By.id("io.selendroid.testapp:id/my_text_field"));
        assertEquals("true", textField.getAttribute("enabled"));
        textField.sendKeys("Appium Android Native Test");
        assertEquals("Appium Android Native Test", textField.getText());
    }

}
