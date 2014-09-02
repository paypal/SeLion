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
import java.util.List;

import com.paypal.selion.plugins.GUIObjectDetails;

/**
 * The class represents the elements that can be given in a page object data source and are recognized by IOS Platform
 * 
 */
public class IOSSeLionElement extends AbstractSeLionElement {

    public static IOSSeLionElement UIAButton = new IOSSeLionElement("UIAButton", true);
    public static IOSSeLionElement UIAActivityIndicator = new IOSSeLionElement("UIAActivityIndicator", true);
    public static IOSSeLionElement UIATableCell = new IOSSeLionElement("UIATableCell", true);
    public static IOSSeLionElement UIAAlert = new IOSSeLionElement("UIAAlert", true);
    public static IOSSeLionElement UIAApplication = new IOSSeLionElement("UIAApplication", true);
    public static IOSSeLionElement UIAImage = new IOSSeLionElement("UIAImage", true);
    public static IOSSeLionElement UIACollectionCell = new IOSSeLionElement("UIACollectionCell", true);
    public static IOSSeLionElement UIACollectionView = new IOSSeLionElement("UIACollectionView", true);
    public static IOSSeLionElement UIADriver = new IOSSeLionElement("UIADriver", true);
    public static IOSSeLionElement UIAKey = new IOSSeLionElement("UIAKey", true);
    public static IOSSeLionElement UIAKeyboard = new IOSSeLionElement("UIAKeyboard", true);
    public static IOSSeLionElement UIALink = new IOSSeLionElement("UIALink", true);
    public static IOSSeLionElement UIANavigationBar = new IOSSeLionElement("UIANavigationBar", true);
    public static IOSSeLionElement UIAPicker = new IOSSeLionElement("UIAPicker", true);
    public static IOSSeLionElement UIAPickerWheel = new IOSSeLionElement("UIAPickerWheel", true);
    public static IOSSeLionElement UIAPoint = new IOSSeLionElement("UIAPoint", true);
    public static IOSSeLionElement UIAScrollView = new IOSSeLionElement("UIAScrollView", true);
    public static IOSSeLionElement UIASearchBar = new IOSSeLionElement("UIASearchBar", true);
    public static IOSSeLionElement UIASecureTextField = new IOSSeLionElement("UIASecureTextField", true);
    public static IOSSeLionElement UIASlider = new IOSSeLionElement("UIASlider", true);
    public static IOSSeLionElement UIAStaticText = new IOSSeLionElement("UIAStaticText", true);
    public static IOSSeLionElement UIAStatusBar = new IOSSeLionElement("UIAStatusBar", true);
    public static IOSSeLionElement UIASwitch = new IOSSeLionElement("UIASwitch", true);
    public static IOSSeLionElement UIATabBar = new IOSSeLionElement("UIATabBar", true);
    public static IOSSeLionElement UIATableGroup = new IOSSeLionElement("UIATableGroup", true);
    public static IOSSeLionElement UIATableView = new IOSSeLionElement("UIATableView", true);
    public static IOSSeLionElement UIATarget = new IOSSeLionElement("UIATarget", true);
    public static IOSSeLionElement UIATextField = new IOSSeLionElement("UIATextField", true);
    public static IOSSeLionElement UIATextView = new IOSSeLionElement("UIATextView", true);
    public static IOSSeLionElement UIAToolbar = new IOSSeLionElement("UIAToolbar", true);
    public static IOSSeLionElement UIATouchScreen = new IOSSeLionElement("UIATouchScreen", true);
    public static IOSSeLionElement UIAWebView = new IOSSeLionElement("UIAWebView", true);
    public static IOSSeLionElement UIAWindow = new IOSSeLionElement("UIAWindow", true);
    public static IOSSeLionElement BASE_CLASS = new IOSSeLionElement("baseClass", false);

    private static IOSSeLionElement[] values = { UIAButton, UIAActivityIndicator, UIATableCell, UIAAlert, UIAApplication,
            UIAImage, UIACollectionCell, UIACollectionView, UIADriver, UIAKey, UIAKeyboard, UIALink, UIANavigationBar,
            UIAPicker, UIAPickerWheel, UIAPoint, UIAScrollView, UIASearchBar, UIASecureTextField, UIASlider,
            UIAStaticText, UIAStatusBar, UIASwitch, UIATabBar, UIATableGroup, UIATableView, UIATarget, UIATextField,
            UIATextView, UIAToolbar, UIATouchScreen, UIAWebView, UIAWindow, BASE_CLASS };

    private IOSSeLionElement(String element, boolean isHtmlType) {
        super(element, isHtmlType);
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
     * @param element
     *            - The element that needs to be tested for being a valid {@link IOSSeLionElement} and whose
     *            {@link IOSSeLionElement#isHtmlElement()} returns true.
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
                htmlObjectDetails = new GUIObjectDetails(element.stringify(), key);
                htmlObjectDetailsList.add(htmlObjectDetails);
            }
        }
        return htmlObjectDetailsList;
    }

}
