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
 * This test demonstrates how to use SeLion for running tests against Android
 * browser using Appium. An Appium instance/server should be installed and 
 * running where selenium host and port should be pointed to this instance.
 * For setting up Appium Android refer 
 * http://appium.io/slate/en/master/?java#system-setup-(android)
 */
public class AppiumAndroidBrowserMobileDemo  {

  @Test
  @MobileTest(appName = "browser", device = "android:4.4", 
  deviceType = "Android Emulator")
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
