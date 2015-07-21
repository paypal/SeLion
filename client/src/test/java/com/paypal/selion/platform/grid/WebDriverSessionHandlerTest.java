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

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.testng.annotations.Test;

import com.paypal.selion.TestServerUtils;
import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.html.TestObjectRepository;
import com.paypal.selion.platform.html.TextField;

public class WebDriverSessionHandlerTest {

    @Test(groups = "functional", expectedExceptions = IllegalStateException.class, 
            expectedExceptionsMessageRegExp = "WebDriverSessionHandler is already started")
    @WebTest
    public void testStartWebDriverSession() throws ExecutionException {
        WebDriverSessionHandler m = new WebDriverSessionHandler(Grid.driver());
        m.start();
        m.start();
    }

    @Test(groups = "functional", expectedExceptions = IllegalStateException.class, 
            expectedExceptionsMessageRegExp = "\\QPlease call startSession() before calling endSession()\\E")
    @WebTest
    public void testEndWebDriverSession() throws ExecutionException {
        WebDriverSessionHandler m = new WebDriverSessionHandler(Grid.driver());
        m.stop();
    }

    @Test(groups = "functional")
    @WebTest
    public void testWebDriverSessionHandler() throws ExecutionException, InterruptedException, IOException {
        Grid.driver().get(TestServerUtils.getTestEditableURL());
        TextField normalTextField = new TextField(TestObjectRepository.TEXT_AREA_LOCATOR.getValue());
        String sTest = "Testing multi line text for TextArea object";

        WebDriverSessionHandler m = new WebDriverSessionHandler(Grid.driver());
        m.start();
        Thread.sleep(1000 * 60 * 3);
        m.stop();
        normalTextField.type(sTest);
        assertTrue(normalTextField.getText().contains(sTest), "Validate GetText method");
    }

}
