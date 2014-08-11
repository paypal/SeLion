package com.paypal.selion.android.sample;

import static org.testng.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.MobileReporter;

public class AndroidTest {
    @Test
    @MobileTest(appName = "android", device = "android19")
    public void testLaunch() throws Exception {
        RemoteWebDriver driver = Grid.selendroidDriver();
        assertNotNull(driver);

        // And now use this to visit Google
        driver.get("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        MobileReporter.log("cheese!", true);

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
    }

    @Test
    @MobileTest(appName = "com.paypal.here:2.0.0", device = "android19")
    public void testLaunch_PPH() throws Exception {
        RemoteWebDriver driver = Grid.selendroidDriver();
        assertNotNull(driver);
        MobileReporter.log("PPH!", true);
    }
}
