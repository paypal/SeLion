package com.mycompany.test;

import static org.testng.Assert.assertNotNull;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.SeLionReporter;

/*
 * An Appium test case example for Safari using SeLion to run on Sauce Cloud.
 */
public class AppiumIOSSafariSauceCloudMobileDemo {

  @Test
  @MobileTest(appName = "safari", device = "iphone:8.1", 
  deviceType = "iPhone Simulator", mobileNodeType = "appium", additionalCapabilities = {
    "appiumVersion:1.4.13" })
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
    SeLionReporter.log("cheese!", true);
  }

}
