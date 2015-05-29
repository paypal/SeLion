package com.mycompany.test;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

/**
 * This test demonstrates how to use SeLion for running tests against a Native
 * Android app using Appium. An Appium instance/server should be locally
 * installed and running. Configure SeLion to this server using any of the
 * following options: 
 * Through the JVM arguments -DSELION_SELENIUM_HOST and -DSELION_SELENIUM_PORT
 * (or) Through suite file <parameter name="seleniumhost" value=""/> and 
 * <parameter name="seleniumport" value=""/> parameters.
 * For setting up Appium Android refer 
 * http://appium.io/slate/en/master/?java#system-setup-(android)
 */
public class AppiumAndroidNativeAppMobileDemo  {

    @Test
    @MobileTest(appPath = "src/test/resources/apps/selendroid-test-app-0.15.0.apk", 
    device = "android:5.0.1", deviceType = "Android Emulator")
    public void testWithNativeApp() {

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
