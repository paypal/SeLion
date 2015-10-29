package com.mycompany.test;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/*
 * An Appium test case example for Native App using SeLion to run on Sauce Cloud.
 */
public class AppiumNativeAppSauceCloudMobileDemo {

  @Test
  @MobileTest(appPath = "sauce-storage:selendroid-test-app-0.15.0.apk", 
  device = "android:4.3", deviceType = "Android Emulator", additionalCapabilities = {
    "appiumVersion:1.4.13" })
  public void nativeAppTestToRunOnSauceCloud() {
    RemoteWebDriver driver = Grid.driver();
    WebDriverWaitUtils
            .waitUntilElementIsVisible("io.selendroid.testapp:id/my_text_field");
    WebElement textField = driver.findElement(By
            .id("io.selendroid.testapp:id/my_text_field"));
    assertEquals("true", textField.getAttribute("enabled"));
    textField.sendKeys("Appium Android Native Test");
    assertEquals("Appium Android Native Test", textField.getText());
  }
}
