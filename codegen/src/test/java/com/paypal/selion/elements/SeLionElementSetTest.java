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

import com.paypal.selion.plugins.CodeGeneratorLoggerFactory;
import com.paypal.selion.plugins.CodeGeneratorSimpleLogger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.*;

public class SeLionElementSetTest {

    /**
     * For testing.
     */
    static class DummySeLionElementSet extends SeLionElementSet {
        public boolean add(String element) {
            return super.add(new SeLionElement(element));
        }

        DummySeLionElementSet() {
            super();
        }

        DummySeLionElementSet(Collection<SeLionElement> elements) {
            super(elements);
        }

        DummySeLionElementSet(SeLionElement[] elements) {
            super(elements);
        }
    }

    @BeforeClass
    public void before() {
        CodeGeneratorLoggerFactory.setLogger(new CodeGeneratorSimpleLogger());
    }

    @Test()
    public void testConstructors() {
        SeLionElementSet elementSet = new DummySeLionElementSet();
        assertNotNull(elementSet);
        assertEquals(0, elementSet.size());

        elementSet = new DummySeLionElementSet(new SeLionElement[] {});
        assertNotNull(elementSet);
        assertEquals(0, elementSet.size());

        elementSet = new DummySeLionElementSet(Arrays.asList());
        assertNotNull(elementSet);
        assertEquals(0, elementSet.size());
    }

    @Test
    public void testFindMatch() {
        //test found
        SeLionElement expected = new SeLionElement("com.foo.Bar");
        SeLionElementSet elementSet = new DummySeLionElementSet(new SeLionElement[] { expected });
        SeLionElement match = elementSet.findMatch("Bar");
        assertSame(match, expected);

        //test not found
        match = elementSet.findMatch("Baz");
        assertNull(match);
    }

    @Test
    public void testIsValid() {
        //test found
        SeLionElement element = new SeLionElement("com.foo.Bar");
        SeLionElementSet elementSet = new DummySeLionElementSet(new SeLionElement[] { element });
        assertTrue(elementSet.isValid("Bar"));

        //test not found
        assertFalse(elementSet.isValid("Baz"));
    }

    @Test
    public void testIsValidUIElement() {
        //test found and is a UI element
        SeLionElement element = new SeLionElement("com.foo.Bar");
        SeLionElementSet elementSet = new DummySeLionElementSet(new SeLionElement[] { element });
        assertTrue(elementSet.isValidUIElement("Bar"));

        //test not found
        assertFalse(elementSet.isValidUIElement("Baz"));
    }

    @Test
    public void testIsExactMatch() {
        //test found
        SeLionElement element = new SeLionElement("com.foo.Bar");
        SeLionElementSet elementSet = new DummySeLionElementSet(new SeLionElement[] { element });
        assertTrue(elementSet.isExactMatch("Bar"));

        //test not found
        assertFalse(elementSet.isExactMatch("Baz"));
    }

    @Test
    public void testAdd() {
        SeLionElement element = new SeLionElement("com.foo.Bar");
        SeLionElementSet elementSet = new DummySeLionElementSet();
        assertEquals(elementSet.size(), 0);

        elementSet.add(element);
        assertEquals(elementSet.size(), 1);

        elementSet.add(new SeLionElement("foo.bar.Baz"));
        assertEquals(elementSet.size(), 2);
    }

    @Test
    public void testGetElementList() {
        SeLionElement element = new SeLionElement("com.foo.Bar");
        SeLionElementSet elementSet = new DummySeLionElementSet(new SeLionElement[] { element });
        assertEquals(elementSet.size(), 1);
    }

    @Test
    public void testAddAll() {
        SeLionElementSet elementSet = new DummySeLionElementSet();
        List<SeLionElement> elements = new ArrayList<>();
        elements.add(new SeLionElement("com.foo.Bar"));
        elements.add(new SeLionElement("foo.bar.Baz"));

        elementSet.addAll(elements);
        assertEquals(elementSet.size(), 2);
    }
}
