/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.node.servlets;

import org.springframework.mock.web.MockHttpServletResponse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class BaseNodeServletTest {
    protected void validateJsonResponse(MockHttpServletResponse response, int statusCode, String expectedResult)
            throws Exception {
        assertEquals(response.getContentType(), "application/json");
        assertEquals(response.getStatus(), statusCode);
        assertEquals(response.getCharacterEncoding(), "UTF-8");
        assertTrue(response.getContentAsString().contains("\"result\":\"" + expectedResult + "\""));
    }
}
