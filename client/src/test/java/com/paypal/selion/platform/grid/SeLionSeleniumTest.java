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

    static final String BAD_LOCATOR = "//wrong locator or text or page title";
    static final String LOCATOR = "//input[@id='lst-ib']";
    static final String PIPED_LOCATOR = "//input[@id='lst-ib']|SomeOtherLocator";
    static final String DISAPPEAR_ELEMENT = "btnI";
    static final String PAGE_TITLE = "Google";
    static final String TEXT = "Gmail";
    static final String URL = "http://www.google.com";

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilPageTitlePresentPos() {
        Grid.driver().get(URL);
        WebDriverWaitUtils.waitUntilPageTitleContains(PAGE_TITLE);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementVisiblePos() {
        Grid.driver().get(URL);
        WebDriverWaitUtils.waitUntilElementIsVisible(LOCATOR);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilTextPresentPos() {
        Grid.driver().get(URL);
        WebDriverWaitUtils.waitUntilTextPresent(TEXT);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementDisapearPos() {
        Grid.driver().get(URL);
        WebDriverWaitUtils.waitUntilPageTitleContains(PAGE_TITLE);
        Button btn = new Button(DISAPPEAR_ELEMENT);
        btn.click();
        WebDriverWaitUtils.waitUntilElementIsInvisible(DISAPPEAR_ELEMENT);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWaitUntilElementPresentPos() {
        Grid.driver().get(URL);
        WebDriverWaitUtils.waitUntilElementIsPresent(LOCATOR);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testWasitUntilElementPipedLocator() {
        Grid.driver().get(URL);
        WebDriverWaitUtils.waitUntilElementIsPresent(PIPED_LOCATOR);
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilWebPageIsValidatedNeg() {
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            Grid.driver().get(URL);
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
            Grid.driver().get(URL);
            WebDriverWaitUtils.waitUntilPageTitleContains(BAD_LOCATOR);
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
            Grid.driver().get(URL);
            WebDriverWaitUtils.waitUntilElementIsVisible(BAD_LOCATOR);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilTextPresentNeg() {
        Grid.driver().get(URL);
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            WebDriverWaitUtils.waitUntilTextPresent(BAD_LOCATOR);
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
            Grid.driver().get(URL);
            WebDriverWaitUtils.waitUntilElementIsInvisible(DISAPPEAR_ELEMENT);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilElementPresentNeg() {
        Grid.driver().get(URL);
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            WebDriverWaitUtils.waitUntilElementIsPresent(BAD_LOCATOR);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
    }
}
