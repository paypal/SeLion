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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.TestObjectRepository;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;

public class DriverTest {

    private static final int NEW_BROWSER_HEIGHT = 500;
    private static final int NEW_BROWSER_WIDTH = 700;

    @BeforeClass(groups = { "functional" })
    public void setUp() {
        Config.setConfigProperty(ConfigProperty.BROWSER_HEIGHT, String.valueOf(NEW_BROWSER_HEIGHT));
        Config.setConfigProperty(ConfigProperty.BROWSER_WIDTH, String.valueOf(NEW_BROWSER_WIDTH));
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testBrowserWindowSizeConfig() {
        Grid.open(TestObjectRepository.EMPTY_PAGE_URL.getValue());
        Dimension browserSize = Grid.driver().manage().window().getSize();
        assertEquals(browserSize.height, NEW_BROWSER_HEIGHT, "verify the height of the browser window is "
                + NEW_BROWSER_HEIGHT);
        assertEquals(browserSize.width, NEW_BROWSER_WIDTH, "verify the width of the browser window is "
                + NEW_BROWSER_WIDTH);
    }

    @Test(groups = { "functional" })
    @WebTest(browserHeight = 300)
    public void testBrowserWindowSizeMissingWidth() {
        Grid.open(TestObjectRepository.EMPTY_PAGE_URL.getValue());
        Dimension browserSize = Grid.driver().manage().window().getSize();
        assertEquals(browserSize.height, NEW_BROWSER_HEIGHT, "verify the height of the browser window is "
                + NEW_BROWSER_HEIGHT);
        assertEquals(browserSize.width, NEW_BROWSER_WIDTH, "verify the width of the browser window is "
                + NEW_BROWSER_WIDTH);
    }

    @Test(groups = { "functional" })
    @WebTest(browserWidth = 300)
    public void testBrowserWindowSizeMissingHeight() {
        Grid.open(TestObjectRepository.EMPTY_PAGE_URL.getValue());
        Dimension browserSize = Grid.driver().manage().window().getSize();
        assertEquals(browserSize.height, NEW_BROWSER_HEIGHT, "verify the height of the browser window is "
                + NEW_BROWSER_HEIGHT);
        assertEquals(browserSize.width, NEW_BROWSER_WIDTH, "verify the width of the browser window is "
                + NEW_BROWSER_WIDTH);
    }

    @Test(groups = { "functional" })
    @WebTest(browserHeight = 300, browserWidth = 400)
    public void testBrowserWindowSizeWebTestParameters() {
        Grid.open(TestObjectRepository.EMPTY_PAGE_URL.getValue());
        Dimension browserSize = Grid.driver().manage().window().getSize();
        assertEquals(browserSize.height, 300, "verify the height of the browser window is 300");
        assertEquals(browserSize.width, 400, "verify the width of the browser window is 400");
    }

    @Test(groups = { "functional" })
    @WebTest(browserHeight = -300, browserWidth = -400)
    public void testBrowserWindowSizeWebTestNegativeParameters() {
        Grid.open(TestObjectRepository.EMPTY_PAGE_URL.getValue());
        Dimension browserSize = Grid.driver().manage().window().getSize();
        assertEquals(browserSize.height, NEW_BROWSER_HEIGHT, "verify the height of the browser window is "
                + NEW_BROWSER_HEIGHT);
        assertEquals(browserSize.width, NEW_BROWSER_WIDTH, "verify the width of the browser window is "
                + NEW_BROWSER_WIDTH);
    }

    @Test(groups = "functional")
    @WebTest
    public void testGetScreenshotAs() {
        Grid.open(TestObjectRepository.EMPTY_PAGE_URL.getValue());
        byte[] bytes = Grid.driver().getScreenshotAs(OutputType.BYTES);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test(groups = "functional", expectedExceptions = TimeoutException.class)
    @WebTest
    public void testWaitUntilElementDisappearNegative() {
        Grid.open(TestServerUtils.getTestEditableURL());
        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            WebDriverWaitUtils.waitUntilElementIsInvisible(TestObjectRepository.IMAGE_TEST.getValue());
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Config.setConfigProperty(ConfigProperty.EXECUTION_TIMEOUT, "120000");

    }

}
