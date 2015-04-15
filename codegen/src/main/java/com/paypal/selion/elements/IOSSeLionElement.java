/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.paypal.selion.plugins.GUIObjectDetails;

/**
 * The class represents the elements that can be given in a page object data source and are recognized by IOS Platform
 * 
 */
public class IOSSeLionElement extends AbstractSeLionElement {
    public static final String UIAUTOMATION_ELEMENT_CLASS = "com.paypal.selion.platform.mobile.ios";

    public static IOSSeLionElement UIAButton = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAButton", true);
    public static IOSSeLionElement UIAAlert = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAAlert", true);
    public static IOSSeLionElement UIANavigationBar = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIANavigationBar", true);
    public static IOSSeLionElement UIAPicker = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAPicker", true);
    public static IOSSeLionElement UIASlider = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIASlider", true);
    public static IOSSeLionElement UIAStaticText = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAStaticText", true);
    public static IOSSeLionElement UIASwitch = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIASwitch", true);
    public static IOSSeLionElement UIATableView = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATableView", true);
    public static IOSSeLionElement UIATextField = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATextField", true);
    public static IOSSeLionElement BASE_CLASS = new IOSSeLionElement(null, "baseClass", false);

    private static IOSSeLionElement[] values = { UIAButton, UIAAlert, UIANavigationBar, UIAPicker, UIASlider,
            UIAStaticText, UIASwitch, UIATableView, UIATextField, BASE_CLASS };

    private IOSSeLionElement(String elementPackage, String element, boolean isHtmlType) {
        super(elementPackage, element, isHtmlType);
    }
    
    /**
     * By providing the qualified name of a custom element we can register it to the element array.
     * Custom elements are inserted before SeLion elements, if you use the same name it will overwrite the existing element.
     * 
     * @param element string of the qualified class
     */
    public static void registerElement(String element) {
        List<IOSSeLionElement> temp = new ArrayList<IOSSeLionElement>(Arrays.asList(values));
        
        temp.add(0, new IOSSeLionElement(HtmlElementUtils.getPackage(element), HtmlElementUtils.getClass(element), true));
        
        values = temp.toArray(new IOSSeLionElement[temp.size()]);
    }

    /**
     * @param rawType
     *            - The String using which an attempt to find a matching {@link IOSSeLionElement} is to be performed.
     * @return - A {@link IOSSeLionElement} if the type ends with one of the values of {@link IOSSeLionElement} enum (or)
     *         <code>null</code> if there were no matches.
     */
    public static IOSSeLionElement findMatch(String rawType) {
        return (IOSSeLionElement) findMatch(values, rawType);
    }

    /**
     * @param element
     *            - The element that needs to be tested for being a valid {@link IOSSeLionElement}
     * @return - <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValid(String element) {
        return isValid(values, element);
    }
    
    /**
     * @param element - The element that needs to be searched.
     * @return - <code>true</code> if the element was found in the set of elements provided.
     */
    public static boolean isExactMatch(String element) {
        return isExactMatch(values, element);
    }

    /**
     * @param element
     *            - The element that needs to be tested for being a valid {@link IOSSeLionElement} and whose
     *            {@link IOSSeLionElement#isUIElement()} returns true.
     * @return - <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValidUIElement(String element) {
        return isValidUIElement(values, element);

    }

    /**
     * Method to obtain a {@link List} of {@link GUIObjectDetails} containing {@link IOSSeLionElement}
     * 
     * @param keys
     *            -keys for which {@link GUIObjectDetails} is to be created.
     * @return
     */
    public static List<GUIObjectDetails> getGUIObjectList(List<String> keys) {
        List<GUIObjectDetails> htmlObjectDetailsList = new ArrayList<GUIObjectDetails>();

        for (String key : keys) {
            IOSSeLionElement element = IOSSeLionElement.findMatch(key);
            if (element != null && element.isUIElement()) {
                GUIObjectDetails htmlObjectDetails = null;
                htmlObjectDetails = new GUIObjectDetails(element.stringify(), key, element.getElementPackage());
                htmlObjectDetailsList.add(htmlObjectDetails);
            }
        }
        return htmlObjectDetailsList;
    }

}
