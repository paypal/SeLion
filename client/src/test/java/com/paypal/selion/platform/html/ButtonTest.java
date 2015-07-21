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
 * This class test the Button class methods
 * 
 */
public class ButtonTest {

    Button submitButton = new Button(TestObjectRepository.BUTTON_SUBMIT_LOCATOR.getValue());
    

    // Refer :
    // http://code.google.com/p/selenium/wiki/InternetExplorerDriver#Clicking_%3Coption%3E_Elements_or_Submitting_Forms_and_alert%28%29
    // Running tests on Button for IE is going to trigger false alarms
    @Test(groups = { "browser-tests", "ie-broken-test", "phantomjs-broken-test" })
    @WebTest
    public void btnTestClick() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        submitButton.click(new Object[] {});
        Alert alert = Grid.driver().switchTo().alert();
        assertTrue(alert.getText().matches("onsubmit called"), "Validated Click method");
        AlertHandler.flushAllAlerts();
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void btnTestClickonly() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        submitButton.clickonly();
        Alert alert = Grid.driver().switchTo().alert();
        assertTrue(alert.getText().matches("onsubmit called"), "Validate ClickOnly method");
        AlertHandler.flushAllAlerts();
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { InvalidElementStateException.class })
    @WebTest
    public void btnTestClickAndWaitNegativeTest() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Button submitButton = new Button(TestObjectRepository.BUTTON_SUBMIT_LOCATOR.getValue(), "submitButton");
        String locatorToWaitFor = TestObjectRepository.LINK_LOCATOR.getValue();
        try {
            submitButton.click(locatorToWaitFor);
        } finally {
            AlertHandler.flushAllAlerts();
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void btnTestClickAndWait() {
        AlertHandler.flushAllAlerts();
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Button submitButton = new Button(TestObjectRepository.CHROME_BUTTON_SUBMIT_LOCATOR.getValue());
        String locatorToWaitFor = TestObjectRepository.SUCCESS_PAGE_TEXT.getValue();
        submitButton.click(locatorToWaitFor);
        String title = Grid.driver().getTitle();
        assertTrue(title.matches("Success"), "Validate Click(Object...Expected) method");
        AlertHandler.flushAllAlerts();
    }

}
