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
    public static final String UIAUTOMATION_ELEMENT_CLASS = "org.uiautomation.ios.UIAModels";

    public static IOSSeLionElement UIAButton = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAButton", true);
    public static IOSSeLionElement UIAActivityIndicator = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAActivityIndicator", true);
    public static IOSSeLionElement UIATableCell = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATableCell", true);
    public static IOSSeLionElement UIAAlert = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAAlert", true);
    public static IOSSeLionElement UIAApplication = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAApplication", true);
    public static IOSSeLionElement UIAImage = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAImage", true);
    public static IOSSeLionElement UIACollectionCell = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIACollectionCell", true);
    public static IOSSeLionElement UIACollectionView = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIACollectionView", true);
    public static IOSSeLionElement UIADriver = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIADriver", true);
    public static IOSSeLionElement UIAKey = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAKey", true);
    public static IOSSeLionElement UIAKeyboard = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAKeyboard", true);
    public static IOSSeLionElement UIALink = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIALink", true);
    public static IOSSeLionElement UIANavigationBar = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIANavigationBar", true);
    public static IOSSeLionElement UIAPicker = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAPicker", true);
    public static IOSSeLionElement UIAPickerWheel = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAPickerWheel", true);
    public static IOSSeLionElement UIAPoint = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAPoint", true);
    public static IOSSeLionElement UIAScrollView = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAScrollView", true);
    public static IOSSeLionElement UIASearchBar = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIASearchBar", true);
    public static IOSSeLionElement UIASecureTextField = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIASecureTextField", true);
    public static IOSSeLionElement UIASlider = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIASlider", true);
    public static IOSSeLionElement UIAStaticText = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAStaticText", true);
    public static IOSSeLionElement UIAStatusBar = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAStatusBar", true);
    public static IOSSeLionElement UIASwitch = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIASwitch", true);
    public static IOSSeLionElement UIATabBar = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATabBar", true);
    public static IOSSeLionElement UIATableGroup = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATableGroup", true);
    public static IOSSeLionElement UIATableView = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATableView", true);
    public static IOSSeLionElement UIATarget = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATarget", true);
    public static IOSSeLionElement UIATextField = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATextField", true);
    public static IOSSeLionElement UIATextView = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATextView", true);
    public static IOSSeLionElement UIAToolbar = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAToolbar", true);
    public static IOSSeLionElement UIATouchScreen = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIATouchScreen", true);
    public static IOSSeLionElement UIAWebView = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAWebView", true);
    public static IOSSeLionElement UIAWindow = new IOSSeLionElement(UIAUTOMATION_ELEMENT_CLASS, "UIAWindow", true);
    public static IOSSeLionElement BASE_CLASS = new IOSSeLionElement(null, "baseClass", false);

    private static IOSSeLionElement[] values = { UIAButton, UIAActivityIndicator, UIATableCell, UIAAlert, UIAApplication,
            UIAImage, UIACollectionCell, UIACollectionView, UIADriver, UIAKey, UIAKeyboard, UIALink, UIANavigationBar,
            UIAPicker, UIAPickerWheel, UIAPoint, UIAScrollView, UIASearchBar, UIASecureTextField, UIASlider,
            UIAStaticText, UIAStatusBar, UIASwitch, UIATabBar, UIATableGroup, UIATableView, UIATarget, UIATextField,
            UIATextView, UIAToolbar, UIATouchScreen, UIAWebView, UIAWindow, BASE_CLASS };

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
