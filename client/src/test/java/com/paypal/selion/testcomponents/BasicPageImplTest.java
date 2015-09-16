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

package com.paypal.selion.testcomponents;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.asserts.SeLionAsserts;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.Container;
import com.paypal.selion.platform.utilities.WebDriverWaitUtils;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class BasicPageImplTest {

    private TestPage page;

    @BeforeClass(groups = { "functional", "unit" })
    public void beforeClass() {
        page = new TestPage();
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testLoadObjectMapFromMap() throws InterruptedException, IOException {
        page = new TestPage();
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        SeLionAsserts.assertEquals(page.getFieldXTextField().getValue(), "Congratulations, You have found fieldX",
                "YamlV1 TextField value retrieved successfully");
        SeLionAsserts.assertEquals(page.getContinueButton().getValue(), "Continue",
                "Button value retrieved successfully");

        SeLionAsserts.assertFalse(page.getHiddenButton().isVisible(), "Yaml Hidden button is actually hidden");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testWaitUntilPageIsValidated() throws InterruptedException, IOException {
        page = new TestPage();
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        WebDriverWaitUtils.waitUntilPageIsValidated(page);
    }

    @Test(groups = { "functional" }, expectedExceptions = { TimeoutException.class })
    @WebTest
    public void testWaitUntilPageIsValidated_Neg() throws InterruptedException, IOException {
        page = new TestPage("US", "TestWrongValidatorPage");
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        WebDriverWaitUtils.waitUntilPageIsValidated(page);
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testContainer() throws InterruptedException, IOException {
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        TestPage page = new TestPage("US");

        SeLionAsserts.assertEquals(page.getSelionContainer().getContainerButton().getValue(), "Button 1",
                "Yaml Button from Container at index 0 retrieved succesfully");
        SeLionAsserts.assertEquals(page.getSelionContainer(1).getContainerButton().getValue(), "Button 2",
                "Yaml Button from Container at index 1 retrieved succesfully");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testPageTitle() throws InterruptedException, IOException {
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        TestPage page = new TestPage("US");
        SeLionAsserts.assertTrue(page.hasExpectedPageTitle());
        SeLionAsserts.assertEquals(Grid.driver().getTitle(), page.getExpectedPageTitle(),
                "PageTitle Yaml value retrieved successfully");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testFallBackLocale() throws InterruptedException, IOException {

        TestPage page = new TestPage("FR");

        SeLionAsserts.assertEquals(page.getFieldXTextField().getLocator(), "//input[@id='fieldXId_FR']",
                "Yaml FR locator returned by SeLion");
        SeLionAsserts.assertEquals(page.getContinueButton().getLocator(), "//input[@id='submit.x']",
                "Yaml US locator returned by SeLion because FR isn't set");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testPageValidator() throws InterruptedException, IOException {
        Grid.open("about:blank");
        String script = getScript();
        Grid.driver().executeScript(script);
        Thread.sleep(4000);

        TestPage page = new TestPage("US");
        TestPage pageNotOpened = new TestPage("US", "TestWrongValidatorPage");
        TestPage pageTitleValidation = new TestPage("US", "PageTitleValidationPage");

        SeLionAsserts.assertEquals(page.isPageValidated(), true, "Page is opened in the browser");
        SeLionAsserts.assertEquals(pageNotOpened.isPageValidated(), false, "Page is not opened in the browser");
        // Validate the page by pageTitle, which is the fallback if there are no pageValidators provided.
        SeLionAsserts.assertEquals(pageTitleValidation.isPageValidated(), true, "Page is opened in the browser");

        pageTitleValidation.setPageTitle("Incorrect page title");
        SeLionAsserts.assertEquals(pageTitleValidation.isPageValidated(), false, "Page is not opened in the browser");

        pageTitleValidation.setPageTitle("* JavaScript");
        SeLionAsserts.assertEquals(pageTitleValidation.isPageValidated(), true, "Page is opened in the browser");

        pageTitleValidation.setPageTitle("* title");
        SeLionAsserts.assertEquals(pageTitleValidation.isPageValidated(), false, "Page is not opened in the browser");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testLoadHtmlObjectsWithContainer() {
        TestInitializeElementsPage testInitPage = new TestInitializeElementsPage();

        // Validations to verify valid parent types and elements are resolved as a result of initialization
        SeLionAsserts.assertTrue(testInitPage.getHeaderContainer() != null, "Verify Container is loaded properly");
        SeLionAsserts.assertTrue(
                testInitPage.getPreLoginButton().getParent().getClass().getSuperclass().equals(BasicPageImpl.class),
                "Verify if a page is assigned for element outside container");
        SeLionAsserts.assertTrue(testInitPage.getHeaderContainer().getSomeLink().getParent().getClass().getSuperclass()
                .equals(Container.class), "Verify if a Container is assigned for element inside container");
    }

    private String getScript() throws IOException {
        File scriptFile = new File("src/test/resources/testdata/InsertHtmlElements.js");
        return FileUtils.readFileToString(scriptFile);
    }
}
