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

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertFalse;
import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.paypal.selion.TestServerListener;
import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.Link;
import com.paypal.selion.platform.html.TestObjectRepository;

/**
 * This class test the ByOrOperator
 */

@Listeners(TestServerListener.class)
public class ByOrOperatorTest {
    Link compltedLink = new Link(TestObjectRepository.COMPLETED_LINK_LOCATOR.getValue());
    Link compltedLinkNeg = new Link(TestObjectRepository.COMPLETED_LINK_LOCATOR_NEG.getValue());

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testValidLocator() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        compltedLink.click();
        String title = Grid.driver().getTitle();
        assertTrue(title.matches("Success"), "Validate valid locator using ByOrOperator");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testInValidLocator() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertFalse(compltedLinkNeg.isElementPresent(), "Validate invalid locator using ByOrOperator");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void testfindElementByOrOperator() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        List<By> bys = new ArrayList<By>();
        bys.add(By.id("NonExistentID"));
        bys.add(By.id("completed1"));
        ByOrOperator byOrOp = new ByOrOperator(bys);

        WebElement element = Grid.driver().findElement(byOrOp);
        assertTrue(element != null, "Could not find an element using ByOrOperator");
    }
}
