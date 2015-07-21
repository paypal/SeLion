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

package com.paypal.selion.platform.html.support;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.By.ByLinkText;
import org.openqa.selenium.By.ByName;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ByIdOrName;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

public class HtmlElementUtilsTest {
	
    @Test(groups = { "functional" })
    @WebTest
    public void validateCssLocationStrategy() throws IOException {
        Grid.open(TestServerUtils.getTestEditableURL());
        WebElement e = HtmlElementUtils.locateElement("css=input[name=normal_text]");
        e.sendKeys("beamdaddy@paypal.com");
        assertTrue(e.getAttribute("value").equals("beamdaddy@paypal.com"));
    }

    @Test(groups = { "unit" })
    public void validateFindElementType() {
        HashMap<String, String> myElements = new HashMap<String, String>();
        myElements.put("id=foo", ById.class.getCanonicalName());
        myElements.put("name=foo", ByName.class.getCanonicalName());
        myElements.put("link=foo", ByLinkText.class.getCanonicalName());
        myElements.put("xpath=foo", ByXPath.class.getCanonicalName());
        myElements.put("/foo", ByXPath.class.getCanonicalName());
        myElements.put("//foo", ByXPath.class.getCanonicalName());
        myElements.put(".//foo", ByXPath.class.getCanonicalName());
        myElements.put("css=foo", ByCssSelector.class.getCanonicalName());
        myElements.put("foo", ByIdOrName.class.getCanonicalName());
        Iterator<String> allElements = myElements.keySet().iterator();
        while (allElements.hasNext()) {
            String eachElement = allElements.next();
            By b = HtmlElementUtils.getFindElementType(eachElement);
            assertTrue(b.getClass().getCanonicalName().equals(myElements.get(eachElement)));
        }
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = { "unit" })
    public void testLocateElementsWithNull() {
        HtmlElementUtils.locateElement(null);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = { "unit" })
    public void testLocateElementsWithEmpty() {
        HtmlElementUtils.locateElement(" ");
    }

    @Test(expectedExceptions = { NoSuchElementException.class }, groups = { "functional" })
    @WebTest
    public void testLocateElementNegativeCondition() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        HtmlElementUtils.locateElement("foo");
    }

    @Test(expectedExceptions = { NoSuchElementException.class }, groups = { "functional" })
    @WebTest
    public void testLocateElementNegativeCondition1() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        HtmlElementUtils.locateElement("name=foo|id=bar");
    }

    @Test(expectedExceptions = { NoSuchElementException.class }, groups = { "functional" })
    @WebTest
    public void testLocateElementsNegativeCondition() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        HtmlElementUtils.locateElements("name=foo");
    }

    @Test(expectedExceptions = { NoSuchElementException.class }, groups = { "functional" })
    @WebTest
    public void testLocateElementsNegativeCondition1() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        HtmlElementUtils.locateElements("name=foo|id=bar");
    }

    @Test(groups = { "functional" })
    @WebTest
    public void testIsElementPresent() {
        Grid.open(TestServerUtils.getTestEditableURL());
        boolean element1 = HtmlElementUtils.isElementPresent("css=input[name=normal_text]");
        assertTrue(element1);

        boolean element2 = HtmlElementUtils.isElementPresent("fakeElement");
        assertTrue(!element2);
    }

}
