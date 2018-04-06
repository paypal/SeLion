/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2017 PayPal                                                                                          |
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

package com.paypal.selion.elements;

import com.paypal.selion.elements.HtmlSeLionElementSet.HtmlSeLionElement;
import com.paypal.selion.plugins.CodeGeneratorLoggerFactory;
import com.paypal.selion.plugins.CodeGeneratorSimpleLogger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.*;

public class HtmlSeLionElementSetTest {
    @BeforeClass
    public void before() {
        CodeGeneratorLoggerFactory.setLogger(new CodeGeneratorSimpleLogger());
    }

    @Test
    public void testGet() {
        SeLionElementSet elementSet = HtmlSeLionElementSet.getInstance();
        assertNotNull(elementSet);
        // check for gte due to other tests adding custom elements to the set
        assertTrue(elementSet.size() >= 14);
        assertTrue(elementSet.contains(HtmlSeLionElement.BUTTON));
    }

    @Test
    public void testAdd() {
        SeLionElementSet elementSet = HtmlSeLionElementSet.getInstance();
        elementSet.add("com.foo.HtmlSelionElementSetTest");

        Optional<SeLionElement> first = elementSet.stream().findFirst();
        assertEquals(first.orElse(null).getElementClass(), "HtmlSelionElementSetTest",
            "the custom added element is not first");
    }
}
