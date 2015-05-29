package com.mycompany.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.MobileReporter;

/*
 * A Selendroid test case example for Native App using SeLion.
 */
public class AndroidMobileDemo {

  @Test
  @MobileTest(appName = "io.selendroid.testapp", device = "android:19", 
    mobileNodeType = "selendroid")
  public void testMethod() throws Exception {
    MobileReporter.log("My Screenshot 1", true);
    WebElement inputField = Grid.driver().findElement(
        By.id("my_text_field"));

    // verify TextField is enabled
    Assert.assertEquals("true", inputField.getAttribute("enabled"));
    MobileReporter.log("My Screenshot 2", true);
    inputField.sendKeys("Selendroid");

    // verify content in the TextField
    Assert.assertEquals("Selendroid", inputField.getText());
  }
}
