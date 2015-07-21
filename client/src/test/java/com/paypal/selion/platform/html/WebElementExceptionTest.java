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

import org.testng.annotations.Test;

import com.paypal.selion.platform.html.WebElementException;

import static org.testng.Assert.assertTrue;

public class WebElementExceptionTest {

    @Test(groups = { "unit" })
    public void testWebElementException() {

        String msg = "Web Element Exception Test";
        WebElementException exception = new WebElementException(msg);
        assertTrue(exception != null, "Could not create WebElementException");
        assertTrue(exception.getMessage().contains(msg));
    }

    @Test(groups = { "unit" })
    public void testWebElementException2() {

        String msg = "Web Element Exception Test";
        Exception ex = new Exception("Test Message");
        WebElementException webElementException = new WebElementException(msg, ex);
        assertTrue(webElementException != null, "Could not create WebElementException");
        assertTrue(webElementException.getMessage().contains(msg));
        assertTrue(webElementException.getCause() != null);

    }
}
