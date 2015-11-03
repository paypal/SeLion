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

package com.paypal.selion.platform.html;

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;
import static com.paypal.selion.platform.asserts.SeLionAsserts.fail;
import static com.paypal.selion.platform.asserts.SeLionAsserts.verifyEquals;
import static com.paypal.selion.platform.asserts.SeLionAsserts.verifyTrue;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.support.HtmlElementUtils;

public class ContainerTest {
    private static final String name = "name";
    private static final String id = "id";
    private static final String uniqueName = "uniqueName";
    private static final String uniqueId = "uniqueId";
    

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerWithXpathBase() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        String baseLocator = "//*[contains(@class,'base')]";
        testContainerWithLocatorTypes(baseLocator);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerWithCssBase() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        String baseLocator = "css=.base";
        testContainerWithLocatorTypes(baseLocator);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerWithIdBase() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        String baseLocator = "id=base";
        testContainerWithLocatorTypes(baseLocator);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerWithNameBase() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        String baseLocator = "name=base";
        testContainerWithLocatorTypes(baseLocator);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerWithLinkBase() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        String baseLocator = "link=baseLink";
        testContainerWithLocatorTypes(baseLocator);
    }

    private void testContainerWithLocatorTypes(String baseLocator) {

        SampleContainer container = new SampleContainer(baseLocator);
        int totalContainers = container.size();
        verifyEquals(totalContainers, 2);

        for (int i = 0; i <= totalContainers - 1; i++) {

            container.setIndex(i);
            int expectedIndex = i + 1;

            TextField cssChild = container.getCssChild();
            String actualAttributeValue = cssChild.getAttribute(name);
            verifyEquals(actualAttributeValue, uniqueName + expectedIndex, cssChild.getLocator());

            Label idChild = container.getIdChild();
            actualAttributeValue = idChild.getAttribute(name);
            verifyEquals(actualAttributeValue, uniqueName + expectedIndex, idChild.getLocator());

            Image nameChild = container.getNameChild();
            actualAttributeValue = nameChild.getAttribute(id);
            verifyEquals(actualAttributeValue, uniqueId + expectedIndex, nameChild.getLocator());

            // Skip nested links unless an example can be created
            if (!baseLocator.contains("link=")) {
                Link linkChild = container.getLinkChild();
                actualAttributeValue = linkChild.getAttribute(name);
                verifyEquals(actualAttributeValue, uniqueName + expectedIndex, linkChild.getLocator());
            }

            Label xpathChild = container.getXpathChild();
            actualAttributeValue = xpathChild.getAttribute(name);
            verifyEquals(actualAttributeValue, uniqueName + expectedIndex, xpathChild.getLocator());
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainer() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        Container container = new Container("id=base");
        String actualName = container.locateElement(1, "css=.dupId").getAttribute(name);
        verifyEquals(actualName, uniqueName + "2");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerWithSize0() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        SampleContainer container = new SampleContainer("id=doesNotExist");
        verifyTrue(container.size() == 0);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerNoSuchElementExceptionAtIndex() {
        String failureMsg = "Allowing users to attempt getting elements at an unavailable index.";
        Grid.driver().get(TestServerUtils.getContainerURL());

        SampleContainer container = new SampleContainer("id=doesNotExist");
        container.setIndex(1);
        try {
            container.getCssChild().getElement();
            fail(failureMsg);
        } catch (NoSuchElementException e) {
            // NOSONAR
        }

        try {
            container.getCssChild().getElements();
            fail(failureMsg);
        } catch (NoSuchElementException e) {
            // NOSONAR
        }

        container = new SampleContainer("id=base");
        container.setIndex(5);
        try {
            container.getCssChild().getElement();
            fail(failureMsg);
        } catch (NoSuchElementException e) {
            // NOSONAR
        }

        try {
            container.getCssChild().getElements();
            fail(failureMsg);
        } catch (NoSuchElementException e) {
            // NOSONAR
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerBadChildXpathLocator() {
        String failureMsg = "Allowing users to get child element with bad locator: ";
        Grid.driver().get(TestServerUtils.getContainerURL());
        SampleContainer container = new SampleContainer("id=base");
        try {
            container.getBadXpathLocator1().getElement();
            fail(failureMsg + container.getBadXpathLocator1().getLocator());
        } catch (UnsupportedOperationException e) {
            verifyTrue(true);
        }
        try {
            container.getBadXpathLocator2().getElement();
            fail(failureMsg + container.getBadXpathLocator2().getLocator());
        } catch (UnsupportedOperationException e) {
            verifyTrue(true);
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testLocateElementsInContainer() {
        Grid.open(TestServerUtils.getContainerURL());
        List<WebElement> e = HtmlElementUtils.locateElements("css=.dupId");
        assertEquals(e.size(), 6);

        e = HtmlElementUtils.locateElements("css=#base .dupId");
        assertEquals(e.size(), 2);

        ContainerTest.SampleContainer container = (new ContainerTest()).new SampleContainer("css=#base");
        e = HtmlElementUtils.locateElements("css=.dupId", container);
        assertEquals(e.size(), 1);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testContainerGetSize() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        AbstractContainer container = new Container("id=base");
        assertTrue(container.size() > 0);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testLocateChildElement() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        AbstractContainer container = new Container("id=base");
        WebElement childElement = container.locateChildElement("css=.dupId");
        assertTrue(childElement != null);

        List<WebElement> childElements = container.locateChildElements("css=.dupId");
        assertTrue(childElements.size() > 0);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testLocateElement() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        AbstractContainer parentContainer = new Container("id=base");

        WebElement childElement = HtmlElementUtils.locateElement("css=.dupId");
        assertTrue(childElement != null);

        WebElement childElementByParent = HtmlElementUtils.locateElement("css=.dupId", parentContainer);
        assertTrue(childElementByParent != null);

        List<WebElement> childElements = HtmlElementUtils.locateElements("css=.dupId", parentContainer);
        assertTrue(childElements.size() > 0);
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testIsElementPresent() {
        Grid.driver().get(TestServerUtils.getContainerURL());
        Container container = new Container("id=base", "base");
        assertTrue(container.isElementPresent());

        Container childContainer = new Container("css=.dupId", "dupId", container);
        assertTrue(childContainer.isElementPresent());
    }

    class SampleContainer extends Container {
        private final TextField cssChild = new TextField(this, "css=.dupId");
        private final Label idChild = new Label(this, "id=duplicateId");
        private final Image nameChild = new Image(this, "name=duplicateName");
        private final Link linkChild = new Link(this, "link=dupLinkText");
        private final Label xpathChild = new Label(this, ".//*[@id='duplicateId']");
        private final Label badXpathLocator1 = new Label(this, "//*[@class='dupId']");
        private final Label badXpathLocator2 = new Label(this, "xpath=//*[@class='dupId']");

        public SampleContainer(String locator) {
            super(locator);
        }

        public TextField getCssChild() {
            return cssChild;
        }

        public Label getIdChild() {
            return idChild;
        }

        public Image getNameChild() {
            return nameChild;
        }

        public Link getLinkChild() {
            return linkChild;
        }

        public Label getXpathChild() {
            return xpathChild;
        }

        public Label getBadXpathLocator1() {
            return badXpathLocator1;
        }

        public Label getBadXpathLocator2() {
            return badXpathLocator2;
        }
    }
}
