/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

package com.paypal.selion.platform.html;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.support.HtmlElementUtils;
import com.paypal.selion.platform.html.support.ParentNotFoundException;
import com.paypal.selion.testcomponents.BasicPageImpl;
import com.paypal.selion.testcomponents.GUIPageExtensionTest.SampleTestPage;

public class AbstractElementTest {

    // TODO this is failing
    @Test(groups = { "functional" })
    @WebTest
    public void validateGetElements() {
        Grid.open(TestServerUtils.getContainerURL());
        ContainerTest.SampleContainer container = (new ContainerTest()).new SampleContainer("css=#base");
        List<WebElement> e = container.getElements();
        assertEquals(e.size(), 2);

        e = container.getCssChild().getElements();
        assertEquals(e.size(), 1);
    }

    // TODO need to analyze if this is a valid test
    @Test(enabled = false, expectedExceptions = { SessionNotFoundException.class }, groups = { "unit" })
    public void testIsElementPresent() {
        TextField txtField = new TextField("foo");
        assertFalse(txtField.isElementPresent());
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testExpectedConditions() throws IOException {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Link confirmLink = new Link(TestObjectRepository.NEW_PAGE_LINK_LOCATOR.getValue());
        confirmLink.clickAndExpect(ExpectedConditions.titleIs("Success"));
        assertEquals(Grid.driver().getTitle(), "Success");
    }

    @Test(groups = { "functional" }, expectedExceptions = { ParentNotFoundException.class })
    @WebTest
    public void testPageNotFoundException() {
        Grid.open("about:blank");
        SampleTestPage testPage = new SampleTestPage();
        testPage.getPersonalLink().click();

    }

    // This test takes care of validating the noSuchElement thrown via a page
    // instead of calling the locateElement
    // directly
    @Test(groups = { "functional" }, expectedExceptions = { NoSuchElementException.class })
    @WebTest
    public void testNoSuchElementWithMessage() {
        Grid.open(TestObjectRepository.EMPTY_PAGE_URL.getValue());
        SampleTestPage myPage = new SampleTestPage();
        myPage.getTestButton().click();
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testClickAndExpectOneOfExpectedConditions() throws IOException {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Link confirmLink = new Link(TestObjectRepository.NEW_PAGE_LINK_LOCATOR.getValue());

        ExpectedCondition<?> success = ExpectedConditions.titleIs("Success");
        ExpectedCondition<?> failure = ExpectedConditions.titleIs("Failure");
        ExpectedCondition<?> alerts = ExpectedConditions.alertIsPresent();
        By buttonBy = HtmlElementUtils.getFindElementType(TestObjectRepository.BUTTON_SUBMIT_LOCATOR.getValue());
        ExpectedCondition<?> button = ExpectedConditions.presenceOfElementLocated(buttonBy);

        List<ExpectedCondition<?>> conditions = new ArrayList<>();
        conditions.add(button);
        conditions.add(failure);
        conditions.add(alerts);
        conditions.add(success);

        ExpectedCondition<?> expected = confirmLink.clickAndExpectOneOf(conditions);
        assertTrue(expected != null);
        assertTrue(expected.equals(success)); // success page should be found
    }

    @Test(groups = { "functional" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testClickAndExpectOneOfExpectedConditionsNegativeTest() throws IOException {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        // TestPageJavaScript.openTestPageWithJavascript(TestPageJavaScript.MOVE_TO_NEW_PAGE);
        Link confirmLink = new Link(TestObjectRepository.NEW_PAGE_LINK_LOCATOR.getValue());

        ExpectedCondition<?> failure = ExpectedConditions.titleIs("Failure");
        List<ExpectedCondition<?>> conditions = new ArrayList<>();
        conditions.add(failure);

        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            confirmLink.clickAndExpectOneOf(conditions);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testClickAndExpectOneOf() throws IOException, InterruptedException {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Thread.sleep(5000);
        Button submitButton = new Button(TestObjectRepository.CHROME_BUTTON_SUBMIT_LOCATOR.getValue());
        String fakeLocator = "//h1[contains(text(),'Fake Page')]";
        SampleSuccessPage testPage = new SampleSuccessPage();
        SampleSuccessInMemoryPage inMemoryPage = new SampleSuccessInMemoryPage();
        String locatorToWaitFor = TestObjectRepository.SUCCESS_PAGE_TEXT.getValue();

        Object expected = submitButton.clickAndExpectOneOf(fakeLocator, locatorToWaitFor, testPage, inMemoryPage);
        assertTrue(expected != null);
        assertTrue(expected.equals(locatorToWaitFor));

        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Object expected2 = submitButton.clickAndExpectOneOf(fakeLocator, testPage);
        assertTrue(expected2 != null);
        assertTrue(expected2.equals(testPage));

        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Object expected3 = submitButton.clickAndExpectOneOf(fakeLocator, inMemoryPage, testPage);
        assertTrue(expected3 != null);
        assertTrue(expected3.equals(inMemoryPage));
    }

    @Test(groups = { "functional" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testClickAndExpectOneOfNegativeTest() throws IOException {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Button submitButton = new Button(TestObjectRepository.CHROME_BUTTON_SUBMIT_LOCATOR.getValue());
        String fakeLocator = "//h1[contains(text(),'Fake Page')]";
        TextField textField = new TextField(TestObjectRepository.TEXTFIELD_LOCATOR.getValue());

        String origTimeout = Config.getConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT);
        try {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "20000");
            submitButton.clickAndExpectOneOf(textField, fakeLocator);
        } finally {
            Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, origTimeout);
        }
    }

    private static class SampleSuccessPage extends BasicPageImpl {
        public SampleSuccessPage() {
            super();
            super.initPage("paypal", "SampleSuccessPage");
        }

        @Override
        public SampleSuccessPage getPage() {
            return this;
        }
    }

    private class SampleSuccessInMemoryPage extends BasicPageImpl {
        private final HashMap<String, String> oMap = new HashMap<String, String>();

        SampleSuccessInMemoryPage() {
            super();
            getPage();
        }

        public SampleSuccessInMemoryPage getPage() {
            if (!isInitialized()) {
                initObjectMap();
                loadObjectMap(oMap);
            }
            return this;
        }

        public void initObjectMap() {
            oMap.put("pageTitle", "Success|Sucessful Page|Some Page");
        }
    }

    // This test case seems unnecessary
    @Test(groups = { "functional" })
    @WebTest
    public void testClickAndExpectOneOfWebElementButton() throws IOException {
        Grid.open(TestServerUtils.getTestEditableURL());
        RadioButton baseRadioButton = new RadioButton(TestObjectRepository.RADIOBUTTON_SPUD_LOCATOR.getValue());

        Button submitButton = new Button(TestObjectRepository.CHROME_BUTTON_SUBMIT_LOCATOR.getValue());
        String fakeLocator = "//h1[contains(text(),'Fake Page')]";
        String successPageText = TestObjectRepository.SUCCESS_PAGE_TEXT.getValue();

        Object expected = baseRadioButton.clickAndExpectOneOf(submitButton, fakeLocator, successPageText);
        assertTrue(expected.equals(submitButton)); // button should be found
    }

    // This test case seems unnecessary
    @Test(groups = { "functional" })
    @WebTest
    public void testClickAndExpectOneOfButton() {
        Grid.open(TestServerUtils.getTestEditableURL());
        RadioButton baseRadioButton = new RadioButton(TestObjectRepository.RADIOBUTTON_SPUD_LOCATOR.getValue());

        ExpectedCondition<?> success = ExpectedConditions.titleIs("Success");
        ExpectedCondition<?> failure = ExpectedConditions.titleIs("Failure");
        ExpectedCondition<?> alerts = ExpectedConditions.alertIsPresent();
        By buttonBy = HtmlElementUtils.getFindElementType(TestObjectRepository.BUTTON_SUBMIT_LOCATOR.getValue());
        ExpectedCondition<?> button = ExpectedConditions.presenceOfElementLocated(buttonBy);

        List<ExpectedCondition<?>> conditions = new ArrayList<>();
        conditions.add(failure);
        conditions.add(alerts);
        conditions.add(success);
        conditions.add(button);

        ExpectedCondition<?> expected = baseRadioButton.clickAndExpectOneOf(conditions);
        assertTrue(expected != null);
        assertTrue(expected.equals(button)); // button should be found
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testClickExpectedConditionsWildCard() throws IOException {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Link confirmLink = new Link(TestObjectRepository.NEW_PAGE_LINK_LOCATOR.getValue());
        SampleSuccessPage successPage = new SampleSuccessPage();
        confirmLink.click(successPage);
        assertEquals(Grid.driver().getTitle(), "Success");
        assertTrue(successPage.hasExpectedPageTitle());
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testClickExpectedCondition() throws IOException {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Link confirmLink = new Link(TestObjectRepository.NEW_PAGE_LINK_LOCATOR.getValue());
        SampleSuccessInMemoryPage successPage = new SampleSuccessInMemoryPage();
        confirmLink.click(successPage);
        assertEquals(Grid.driver().getTitle(), "Success");
        assertTrue(successPage.hasExpectedPageTitle());
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Config.setConfigProperty(Config.ConfigProperty.EXECUTION_TIMEOUT, "120000");

    }

}
