/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.html.Button;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.selion.testcomponents.BasicPageImpl;

import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Test;

public class SeLionSeleniumTest {

    final String badLocator = "//wrong locator or text or page title";
    final String locator = "//input[@id='lst-ib']";
    final String pipedLocator = "//input[@id='lst-ib']|SomeOtherLocator";
    final String disappearElement = "btnI";
    final String pageTitle = "Google";
    final String text = "Gmail";
    final String url = "http://www.google.com";

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilPageTitlePresentPos() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilPageTitleContains(pageTitle);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementVisiblePos() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilElementIsVisible(locator);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilTextPresentPos() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilTextPresent(text);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementDisapearPos() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilPageTitleContains(pageTitle);
        Button btn = new Button(disappearElement);
        btn.click();
        WebDriverWaitUtils.waitUntilElementIsInvisible(disappearElement);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementPresentPos() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilElementIsPresent(locator);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWasitUntilElementPipedLocator() {
        Grid.driver().get(url);
        WebDriverWaitUtils.waitUntilElementIsPresent(pipedLocator);
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilWebPageIsValidatedNeg() {
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            Grid.driver().get(url);
            WebDriverWaitUtils.waitUntilPageIsValidated(new BasicPageImpl() {
                @Override
                public BasicPageImpl getPage() {
                    return this;
                }
            });
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilPageTitlePresentNeg() {
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            Grid.driver().get(url);
            WebDriverWaitUtils.waitUntilPageTitleContains(badLocator);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilElementVisibleNeg() {
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            Grid.driver().get(url);
            WebDriverWaitUtils.waitUntilElementIsVisible(badLocator);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
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
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { RuntimeException.class })
    @WebTest
    public void testWaitUntilElementDisapearNeg() {
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            Grid.driver().get(url);
            WebDriverWaitUtils.waitUntilElementIsInvisible(disappearElement);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
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
    }
}
