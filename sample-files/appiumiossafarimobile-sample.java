package com.mycompany.test;

import static org.testng.Assert.assertNotNull;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.SeLionReporter;

/**
 * This test demonstrates how to use SeLion for running tests against iOS 
 * safari using Appium.  An Appium instance/server should be locally
 * installed and running. Configure SeLion to this server using any of the
 * following options: 
 * Through the JVM arguments -DSELION_SELENIUM_HOST and -DSELION_SELENIUM_PORT
 * (or) Through suite file <parameter name="seleniumhost" value=""/> and 
 * <parameter name="seleniumport" value=""/> parameters.
 * For setting up Appium iOS refer
 * http://appium.io/slate/en/master/?java#system-setup-(ios)
 */
public class AppiumIOSSafariMobileDemo {

  @Test
  @MobileTest(appName = "safari", device = "iphone:8.1",
  deviceType = "iPhone Simulator")
  public void testWithSafari() {
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
