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

package com.paypal.selion.platform.grid;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.Wait.WaitTimedOutException;

public class SeLionSeleniumTest {

    final String badLocator = "//wrong locator or text or page title";
    final String locator = "//input[@id='lst-ib']";
    final String disappearElement = "btnI";
    final String pageTitle = "Google";
    final String text = "Maps";
    final String url = "http://www.google.com";

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilPageTitlePresentPos() {
        Grid.driver().get(url);
        try {
            WebDriverWaitUtils.waitUntilPageTitleContains(pageTitle);
            assertTrue(true);
        } catch (WaitTimedOutException e) {
            fail(e.getMessage());
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementVisiblePos() {
        Grid.driver().get(url);
        try {
            WebDriverWaitUtils.waitUntilElementIsVisible(locator);
            assertTrue(true);
        } catch (WaitTimedOutException e) {
            fail(e.getMessage());
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilTextPresentPos() {
        Grid.driver().get(url);
        try {
            WebDriverWaitUtils.waitUntilTextPresent(text);
            assertTrue(true);
        } catch (WaitTimedOutException e) {
            fail(e.getMessage());
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementDisapearPos() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilPageTitleContains(pageTitle);
        Button btn = new Button(disappearElement);
        btn.click();
        try {
            WebDriverWaitUtils.waitUntilElementIsInvisible(disappearElement);
            assertTrue(true);
        } catch (WaitTimedOutException e) {
            fail(e.getMessage());
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementPresentPos() {
        Grid.driver().get(url);
        try {
            WebDriverWaitUtils.waitUntilElementIsPresent(locator);
            assertTrue(true);
        } catch (WaitTimedOutException e) {
            fail(e.getMessage());
        }
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { WaitTimedOutException.class })
    @WebTest
    public void testWaitUntilPageTitlePresentNeg() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilPageTitleContains(badLocator);
        fail("Wait Timeout Exception was not thrown.");
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { WaitTimedOutException.class })
    @WebTest
    public void testWaitUntilElementVisibleNeg() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilElementIsVisible(badLocator);
        fail("Wait Timeout Exception was not thrown.");
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilTextPresentNeg() {
        Grid.driver().get(url);
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            WebDriverWaitUtils.waitUntilTextPresent(badLocator);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
        fail("Timeout Exception was not thrown.");
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { RuntimeException.class })
    @WebTest
    public void testWaitUntilElementDisapearNeg() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilElementIsInvisible(disappearElement);
        fail("Runtime Exception was not thrown.");
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilElementPresentNeg() {
        Grid.driver().get(url);
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            WebDriverWaitUtils.waitUntilElementIsPresent(badLocator);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
        fail("Timeout Exception was not thrown.");
    }
}
