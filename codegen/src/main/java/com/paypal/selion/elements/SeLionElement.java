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

import org.apache.commons.lang.StringUtils;

public class SeLionElement {

    private final String elementClass;
    private final boolean uiElement;
    private final String elementPackage;

    /**
     * Creates a new UI element via the full path {package + classname}
     *
     * @param element the element name. For example <code>com.foo.Bar</code>
     */
    public SeLionElement(String element) {
        this(element, true);
    }

    /**
     * Create a new element via the full path {package + classname}
     *
     * @param element   the element name. For example <code>com.foo.Bar</code>
     * @param uiElement whether the element is a UI element
     */
    public SeLionElement(String element, boolean uiElement) {
        this(HtmlElementUtils.getPackage(element), HtmlElementUtils.getClass(element), uiElement);
    }

    /**
     * Create a new element
     *
     * @param elementPackage the element package name. For example <code>com.foo</code>
     * @param elementClass   the element class name. For example <code>Bar</code>
     * @param uiElement      whether the element is a UI element
     */
    public SeLionElement(String elementPackage, String elementClass, boolean uiElement) {
        this.elementClass = elementClass;
        this.uiElement = uiElement;
        this.elementPackage = elementPackage;
    }

    @Override
    public String toString() {
        return String.format("%s.%s, uiElement=%s", elementPackage, elementClass, uiElement);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeLionElement)) return false;

        SeLionElement that = (SeLionElement) o;

        if (uiElement != that.uiElement) return false;
        if (elementClass != null ? !elementClass.equals(that.elementClass) : that.elementClass != null) return false;
        return elementPackage != null ? elementPackage.equals(that.elementPackage) : that.elementPackage == null;
    }

    @Override
    public int hashCode() {
        int result = elementClass != null ? elementClass.hashCode() : 0;
        result = 31 * result + (uiElement ? 1 : 0);
        result = 31 * result + (elementPackage != null ? elementPackage.hashCode() : 0);
        return result;
    }

    /**
     * @return <code>true</code> if the current element is a UI element.
     */

    public boolean isUIElement() {
        return uiElement;
    }

    /**
     * @return the element class
     */
    public String getElementClass() {
        return elementClass;
    }

    /**
     * @return the element package
     */

    public String getElementPackage() {
        return elementPackage;
    }

    /**
     * Returns <code>true</code> when the element provided is an exact match for this element class
     *
     * @param element The element to check
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isExactMatch(String element) {
        return !StringUtils.isEmpty(element) && elementClass.equals(element);
    }

    /**
     * This method returns <code>true</code> if the element key name that was given ends with the textual value of the
     * current element.
     *
     * @param elementKey The string that needs to be checked.
     * @return <code>true</code> if there was a match.
     */
    public boolean looksLike(String elementKey) {
        return !StringUtils.isEmpty(elementKey) && elementKey.endsWith(elementClass);
    }
}
