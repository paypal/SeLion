/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.ios.sample;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.MobileTest;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.reports.runtime.WebReporter;

public class IOSTest {
    // @Test
    @WebTest
    public void bar() {
        Grid.open("http://www.google.com");
        WebReporter.log("screenshot", true, true);
    }

    @Test
    @MobileTest(appName = "Safari")
    public void foo() {
        openURL("https://www.stage2p1382.qa.paypal.com");
        WebReporter.log("screenshot", true, true);

        System.out.println("Page title = " + Grid.driver().getTitle());
    }

    /**
     * This utility method helps in loading https urls in the native safari browser in the iOS simulator. Currently
     * ios-driver has problems when it comes to dealing with secure sites that have certificate issues. directly using
     * Grid.open() (or) Grid.driver().get() is going to stall the simulator preventing the test from proceeding further
     * This utility method is an ugly hack to get past this till a permanent resolution is achieved for the issue
     * https://github.com/ios-driver/ios-driver/issues/96
     * 
     * @param url
     */
    public void openURL(String url) {
        System.err.println(">>>>>Using javascript to load the page url");
        try {
            Grid.iOSDriver().executeScript("window.location=arguments[0]; ", url);
            new WebDriverWait(Grid.iOSDriver(), 10).until(ExpectedConditions.alertIsPresent());
            System.out.println(">>>> Finished waiting for the alert and the alert is present");
            Grid.iOSDriver().switchTo().alert().accept();

            while (true) {
                String readyState = (String) Grid.iOSDriver().executeScript("return document.readyState;");
                try {
                    Thread.sleep(10 * 1000);
                    System.out.println(">>>>>ready state ? " + readyState);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (readyState.equalsIgnoreCase("complete")) {
                    new WebDriverWait(Grid.iOSDriver(), 15).until(new MyWaiter(By.xpath("//img")));
                    break;
                }
            }

            System.out.println("Found the alert we were looking for");
        } catch (NoAlertPresentException e) { // NOSONAR
            // gobble exception but do nothing with it.
            e.printStackTrace();
        }

    }

    class MyWaiter implements ExpectedCondition<List<WebElement>> {
        private By by;

        public MyWaiter(By by) {
            this.by = by;
        }

        @Override
        public List<WebElement> apply(WebDriver driver) {
            List<WebElement> elements = driver.findElements(by);
            if (elements == null) {
                return null;
            }
            for (WebElement element : elements) {
                if (!element.isDisplayed()) {
                    return null;
                }
            }
            return elements.size() > 0 ? elements : null;
        }

    }
}
