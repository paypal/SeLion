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

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class SeLionElementTest {
    @Test
    public void testConstructor() {
        SeLionElement a = new SeLionElement("com.foo.Bar");
        assertEquals(a.getElementClass(), "Bar");
        assertEquals(a.getElementPackage(), "com.foo");
        assertTrue(a.isUIElement());

        SeLionElement b = new SeLionElement("org.bar.Baz", false);
        assertEquals(b.getElementClass(), "Baz");
        assertEquals(b.getElementPackage(), "org.bar");
        assertFalse(b.isUIElement());

        SeLionElement c = new SeLionElement("net.baz", "Foo", true);
        assertEquals(c.getElementClass(), "Foo");
        assertEquals(c.getElementPackage(), "net.baz");
        assertTrue(c.isUIElement());
    }

    @Test
    public void testIsExactMatch() {
        SeLionElement a = new SeLionElement("com.foo.Bar");
        assertTrue(a.isExactMatch("Bar"));
        assertFalse(a.isExactMatch("bar"));
        assertFalse(a.isExactMatch(null));
        assertFalse(a.isExactMatch(" "));
        assertFalse(a.isExactMatch("foo.Bar"));
        assertFalse(a.isExactMatch("com.foo"));
        assertFalse(a.isExactMatch("com.foo.bar"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorWithNullParamsThrowsException() {
        new SeLionElement(null);
    }

    @Test
    public void testLooksLike() {
        SeLionElement a = new SeLionElement("com.foo.Bar");
        assertTrue(a.looksLike("MyBar"));
        assertTrue(a.looksLike("com.foo.Bar"));
        assertTrue(a.looksLike(".Bar"));
        assertFalse(a.looksLike(null));
        assertFalse(a.looksLike(" "));
        assertFalse(a.looksLike("mybar"));
    }
}
