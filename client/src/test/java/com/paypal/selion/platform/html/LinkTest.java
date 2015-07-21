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

import org.openqa.selenium.Alert;
import org.openqa.selenium.InvalidElementStateException;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

/**
 * This class test the Link class methods
 */

public class LinkTest {

	
    Link confirmLink = new Link(TestObjectRepository.LINK_LOCATOR.getValue());

    @Test(groups = { "browser-tests" })
    @WebTest
    public void linkTestClick() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        confirmLink.click(new Object[] {});
        Alert alert = Grid.driver().switchTo().alert();
        assertTrue(alert.getText().matches("You are about to go to a dummy page."), "Validate Click method");
        alert.accept();
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { InvalidElementStateException.class })
    @WebTest
    public void linkTestClickAndWaitNegativeTest() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Link confirmLink = new Link(TestObjectRepository.LINK_LOCATOR.getValue());
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        String locatorToWaitFor = TestObjectRepository.BUTTON_SUBMIT_LOCATOR.getValue();
        try {
            confirmLink.click(locatorToWaitFor);
        } finally {
            AlertHandler.flushAllAlerts();
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void linkTestClickAndWait() {
        AlertHandler.flushAllAlerts();
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Link confirmLink = new Link(TestObjectRepository.NEW_PAGE_LINK_LOCATOR.getValue());
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        String locatorToWaitFor = TestObjectRepository.SUCCESS_PAGE_TEXT.getValue();

        confirmLink.click(locatorToWaitFor);
        String title = Grid.driver().getTitle();
        assertTrue(title.matches("Success"), "Validate Click(Object...Expected) method");
        Grid.driver().navigate().back();
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void linkTestClickOnly() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        confirmLink.clickonly();
        Alert alert = Grid.driver().switchTo().alert();
        assertTrue(alert.getText().matches("You are about to go to a dummy page."), "Validate clickonly method");
        alert.accept();
        AlertHandler.flushAllAlerts();
    }
}
