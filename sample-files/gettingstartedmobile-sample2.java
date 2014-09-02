package com.mycompany.test;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.MobileReporter;

public class MobileDemo {

    @MobileTest(appName = "InternationalMountains")
    @Test
    public void testMethod() throws InterruptedException {
        MobileReporter.log("My Screenshot 1", true);
        List cells = Grid.iOSDriver().findElements(By.className("UIATableCell"));
        Assert.assertEquals(9, cells.size());

        // get the 1st mountain
        WebElement first = (WebElement) cells.get(0);
        first.click();
        Thread.sleep(10 * 1000);

        // take a screenshot using the normal selenium api.
        MobileReporter.log("My Screenshot 2", true);

        // access the content
        By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
        WebElement text = Grid.iOSDriver().findElement(selector);
        System.out.println(text.getAttribute("name"));
    }
}
