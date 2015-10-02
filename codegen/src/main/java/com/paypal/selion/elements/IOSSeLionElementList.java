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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.paypal.selion.plugins.GUIObjectDetails;

/**
 * The class represents the elements that can be given in a page object data source and are recognized by IOS Platform
 * 
 */
public class IOSSeLionElementList extends AbstractSeLionElementList {
    public static final String UIAUTOMATION_ELEMENT_CLASS = "com.paypal.selion.platform.mobile.ios";

    public static IOSSeLionElementList UIAButton =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIAButton", true);
    public static IOSSeLionElementList UIAAlert =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIAAlert", true);
    public static IOSSeLionElementList UIANavigationBar =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIANavigationBar", true);
    public static IOSSeLionElementList UIAPicker =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIAPicker", true);
    public static IOSSeLionElementList UIASlider =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIASlider", true);
    public static IOSSeLionElementList UIAStaticText =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIAStaticText", true);
    public static IOSSeLionElementList UIASwitch =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIASwitch", true);
    public static IOSSeLionElementList UIATableView =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIATableView", true);
    public static IOSSeLionElementList UIATextField =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIATextField", true);
    public static IOSSeLionElementList UIAElement =
            new IOSSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "UIAElement", true);
    public static IOSSeLionElementList BASE_CLASS =
            new IOSSeLionElementList(null, "baseClass", false);

    private static IOSSeLionElementList[] values = { UIAButton, UIAAlert, UIANavigationBar, UIAPicker, UIASlider,
            UIAStaticText, UIASwitch, UIATableView, UIATextField, UIAElement, BASE_CLASS };

    private IOSSeLionElementList(String elementPackage, String element, boolean isHtmlType) {
        super(elementPackage, element, isHtmlType);
    }

    /**
     * By providing the qualified name of a custom element we can register it to the element array. Custom elements are
     * inserted before SeLion elements, if you use the same name it will overwrite the existing element.
     * 
     * @param element
     *            string of the qualified class
     */
    public static void registerElement(String element) {
        List<IOSSeLionElementList> temp = new ArrayList<IOSSeLionElementList>(Arrays.asList(values));

        temp.add(0, new IOSSeLionElementList(HtmlElementUtils.getPackage(element), HtmlElementUtils.getClass(element),
                true));

        values = temp.toArray(new IOSSeLionElementList[temp.size()]);
    }

    /**
     * @param rawType
     *            The String using which an attempt to find a matching {@link IOSSeLionElementList} is to be performed.
     * @return A {@link IOSSeLionElementList} if the type ends with one of the values of {@link IOSSeLionElementList}
     *         enum (or) <code>null</code> if there were no matches.
     */
    public static IOSSeLionElementList findMatch(String rawType) {
        return (IOSSeLionElementList) findMatch(values, rawType);
    }

    /**
     * @param element
     *            The element that needs to be tested for being a valid {@link IOSSeLionElementList}
     * @return <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValid(String element) {
        return isValid(values, element);
    }

    /**
     * @param element
     *            The element that needs to be searched.
     * @return <code>true</code> if the element was found in the set of elements provided.
     */
    public static boolean isExactMatch(String element) {
        return isExactMatch(values, element);
    }

    /**
     * @param element
     *            The element that needs to be tested for being a valid {@link IOSSeLionElementList} and whose
     *            {@link IOSSeLionElementList#isUIElement()} returns true.
     * @return <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValidUIElement(String element) {
        return isValidUIElement(values, element);

    }

    /**
     * Method to obtain a {@link List} of {@link GUIObjectDetails} containing {@link IOSSeLionElementList}
     * 
     * @param keys
     *            keys for which {@link GUIObjectDetails} is to be created.
     * @return the {@link List} of {@link GUIObjectDetails}
     */
    public static List<GUIObjectDetails> getGUIObjectList(List<String> keys) {
        List<GUIObjectDetails> htmlObjectDetailsList = new ArrayList<GUIObjectDetails>();

        for (String key : keys) {
            IOSSeLionElementList element = IOSSeLionElementList.findMatch(key);
            if (element != null && element.isUIElement()) {
                GUIObjectDetails htmlObjectDetails = null;
                htmlObjectDetails = new GUIObjectDetails(element.stringify(), key, element.getElementPackage());
                htmlObjectDetailsList.add(htmlObjectDetails);
            }
        }
        return htmlObjectDetailsList;
    }

}
