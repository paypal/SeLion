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
 * This class test the Image class methods
 */
public class ImageTest {
    Image testImage = new Image(TestObjectRepository.IMAGE_TEST.getValue());
    

    @Test(groups = { "browser-tests" })
    @WebTest
    public void imageTestClick() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        testImage.click(new Object[] {});
        Alert alert = Grid.driver().switchTo().alert();
        assertTrue(alert.getText().matches("onsubmit called"), "Validate Click method");
        alert.accept();
    }

    @Test(groups = { "browser-tests" }, expectedExceptions = { InvalidElementStateException.class })
    @WebTest
    public void imageTestClickAndWaitNegativeTest() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Image testImage = new Image(TestObjectRepository.IMAGE_TEST.getValue());
        String locatorToWaitFor = TestObjectRepository.LINK_LOCATOR.getValue();
        try {
            testImage.click(locatorToWaitFor);
        } finally {
            AlertHandler.flushAllAlerts();
        }
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void imageTestClickAndWait() {
        AlertHandler.flushAllAlerts();
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Image testImage = new Image(TestObjectRepository.CHROME_IMAGE_TEST.getValue());
        String locatorToWaitFor = TestObjectRepository.SUCCESS_PAGE_TEXT.getValue();
        testImage.click(locatorToWaitFor);
        String title = Grid.driver().getTitle();
        assertTrue(title.matches("Success"), "Validate Click(Object...Expected) method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void imageTestGetHeight() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue((testImage.getHeight() == 41), "Validated GetHeight method");
    }

    @Test(groups = { "browser-tests" })
    @WebTest
    public void imageTestGetWidth() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        assertTrue((testImage.getWidth() == 41), "Validated GetWidth method");
        AlertHandler.flushAllAlerts();
    }

    
    @Test(groups = { "browser-tests" })
    @WebTest
    public void testImageConstructor() {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        Image testImage = new Image(TestObjectRepository.IMAGE_TEST.getValue(), "Earth");

        assertTrue((testImage != null), "Validated Image(locator, controlName) method");
    }
}
