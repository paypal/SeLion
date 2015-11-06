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

package com.paypal.selion.elements;

public abstract class AbstractSeLionElementList {

    private final String element;
    private final boolean uiElement;
    private final String elementPackage;

    protected AbstractSeLionElementList(String elementPackage, String element, boolean uiElement) {
        this.element = element;
        this.uiElement = uiElement;
        this.elementPackage = elementPackage;
    }

    /**
     * @return - <code>true</code> if the current element is a UI element.
     */
    public boolean isUIElement() {
        return uiElement;
    }

    /**
     * @return - A String representation of the current element.
     */
    public String stringify() {
        return element;
    }

    /**
     * @return the elementPackage
     */
    public String getElementPackage() {
        return elementPackage;
    }

    /**
     * This method returns <code>true</code> if the key that was given ends with the textual value
     * of the current element.
     * @param key - The string that needs to be checked.
     * @return - <code>true</code> if there was a match.
     */
    public boolean looksLike(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        boolean returnValue = key.endsWith(element);
        return returnValue;
    }

    @Override
    public String toString() {
        return String.format("%s, IsUIElement=%s", element, uiElement);
    }

    /**
     * @param validSet - An array of {@link AbstractSeLionElementList} that represents the set of elements in which a 
     * match attempt is to be made.
     * @param rawType - The element that needs to be searched.
     * @return - Either the {@link AbstractSeLionElementList} version of the raw element or <code>null</code> if it wasn't found.
     */
    static AbstractSeLionElementList findMatch(AbstractSeLionElementList[] validSet, String rawType) {
        for (AbstractSeLionElementList eachElement : validSet) {
            if (eachElement.looksLike(rawType)) {
                return eachElement;
            }
        }
        return null;
    }

    /**
     * @param validSet - An array of {@link AbstractSeLionElementList} that represents the set of elements in which a 
     * match attempt is to be made.
     * @param element - The element that needs to be searched.
     * @return - <code>true</code> if the element was found in the set of elements provided.
     */
    static boolean isValid(AbstractSeLionElementList[] validSet, String element) {
        if (element == null || element.trim().isEmpty()) {
            return false;
        }
        for (AbstractSeLionElementList each : validSet) {
            if (each.looksLike(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param validSet
     *            - An array of {@link AbstractSeLionElementList} that represents the set of elements in which a match
     *            attempt is to be made.
     * @param element
     *            - The element that needs to be searched.
     * @return- <code>true</code> if the element was found in the set of elements provided and if its
     *          {@link AbstractSeLionElementList#isUIElement()} resulted in <code>true</code>.
     */
    static boolean isValidUIElement(AbstractSeLionElementList[] validSet, String element) {
        if (element == null || element.trim().isEmpty()) {
            return false;
        }
        for (AbstractSeLionElementList each : validSet) {
            if (each.looksLike(element)) {
                return each.isUIElement();
            }
        }
        return false;
    }
    
    /**
     * @param validSet - An array of {@link AbstractSeLionElementList} that represents the set of elements in which an 
     * exact match attempt is to be made.
     * @param element - The element that needs to be searched.
     * @return - <code>true</code> if the element was found in the set of elements provided.
     */
    static boolean isExactMatch(AbstractSeLionElementList[] validSet, String element) {
        if (element == null || element.trim().isEmpty()) {
            return false;
        }
        for (AbstractSeLionElementList each : validSet) {
            if (each.element.equals(element)) {
                return true;
            }
        }
        return false;
    }
}
