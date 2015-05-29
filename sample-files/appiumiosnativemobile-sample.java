package com.mycompany.test;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.MobileReporter;

/**
 * This test demonstrates how to use SeLion for running tests against a Native 
 * iOS app using Appium. An Appium instance/server should be locally installed 
 * and running. Configure SeLion to this server using any of the following options:
 * Through the JVM arguments -DSELION_SELENIUM_HOST and -DSELION_SELENIUM_PORT 
 * (or) Through suite file <parameter name="seleniumhost" value=""/> and
 * <parameter name="seleniumport" value=""/> 
 * For setting up Appium iOS refer 
 * http://appium.io/slate/en/master/?java#system-setup-(ios).
 * 
 * The app InternationalMountains.app to be tested should be placed in the
 * current working directory (src/test/resources) and its appPath should be 
 * specified accordingly.
 */
public class AppiumIOSNativeAppMobileDemo {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/InternationalMountains.app",
    device = "iphone:8.1", deviceType = "iPhone Simulator")
    public void testNativeAppAllInternationalMountains()
            throws InterruptedException {
        MobileReporter.log("My Screenshot 1", true);
        List<WebElement> cells = Grid.driver().findElements(
                By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());
        // get the 1st mountain
        WebElement first = cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);
        // take a screenshot
        MobileReporter.log("My Screenshot 2", true);
    }
}
