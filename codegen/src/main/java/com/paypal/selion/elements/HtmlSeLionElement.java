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
 * This class represents the set of elements that can occur in a page object data source which are recognized by SeLion.
 *
 */
public class HtmlSeLionElement extends AbstractSeLionElement {
    public static HtmlSeLionElement TEXT_FIELD = new HtmlSeLionElement("TextField", true, true);
    public static HtmlSeLionElement TABLE = new HtmlSeLionElement("Table", true, true);
    public static HtmlSeLionElement SELECT_LIST = new HtmlSeLionElement("SelectList", true, true);
    public static HtmlSeLionElement RADIO_BUTTON = new HtmlSeLionElement("RadioButton", true, true);
    public static HtmlSeLionElement BUTTON = new HtmlSeLionElement("Button", true, true);
    public static HtmlSeLionElement LINK = new HtmlSeLionElement("Link", true, true);
    public static HtmlSeLionElement LABEL = new HtmlSeLionElement("Label", true, true);
    public static HtmlSeLionElement IMAGE = new HtmlSeLionElement("Image", true, true);
    public static HtmlSeLionElement FORM = new HtmlSeLionElement("Form", true, true);
    public static HtmlSeLionElement DATE_PICKER = new HtmlSeLionElement("DatePicker", true, true);
    public static HtmlSeLionElement CHECK_BOX = new HtmlSeLionElement("CheckBox", true, true);
    public static HtmlSeLionElement CONTAINER = new HtmlSeLionElement("Container", true, false);
    public static HtmlSeLionElement BASE_CLASS = new HtmlSeLionElement("baseClass", false, false);
    public static HtmlSeLionElement PAGE_TITLE = new HtmlSeLionElement("pageTitle", false, false);

    //DONOT alter the order of RADIO_BUTTON and BUTTON because of the following reason:
    //consider a field in the yaml file which looks like this : fxBankRadioButton
    //if we have Button ahead of RadioButton, then our code would end up matching this above key with
    //Button instead of matching it against RadioButton. This is the ONLY case wherein the order is very important

    private static HtmlSeLionElement[] values = {TEXT_FIELD, TABLE, SELECT_LIST, RADIO_BUTTON, BUTTON, LINK, LABEL,
            IMAGE, FORM, DATE_PICKER, CHECK_BOX, CONTAINER, BASE_CLASS, PAGE_TITLE
    };

    @Override
    public String toString() {
        return String.format("%s, canHaveParent=%s",super.toString(), canHaveParent);
    }

    /**
     * @param rawType - The String using which an attempt to find a matching {@link HtmlSeLionElement} is to be performed.
     * @return - A {@link HtmlSeLionElement} if the type ends with one of the values of {@link HtmlSeLionElement} enum (or)
     * <code>null</code> if there were no matches.
     */
    public static HtmlSeLionElement findMatch(String rawType) {
        return (HtmlSeLionElement) findMatch(values, rawType);
    }

    /**
     * @param element - The element that needs to be tested for being a valid {@link HtmlSeLionElement}
     * @return - <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValid(String element) {
        return isValid(values, element);
    }

    /**
     * @param element - The element that needs to be tested for being a valid {@link HtmlSeLionElement} and whose
     * {@link HtmlSeLionElement#isUIElement()} returns true.
     * @return - <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValidHtmlElement(String element) {
        return isValidUIElement(values, element);
    }

    private boolean canHaveParent;

    private HtmlSeLionElement(String element, boolean isHtmlType, boolean canHaveParent) {
        super(element, isHtmlType);
        this.canHaveParent = canHaveParent;
    }

    /**
     * @return - <code>true</code> if the current element can have a parent to it.
     */
    public boolean canHaveParent() {
        return canHaveParent;
    }

    /**
     * This method returns <code>true</code> if the key that was given ends with the textual value of one of the 
     * elements that are part of {@link HtmlSeLionElement}
     * @param key - The string that needs to be checked.
     * @return - <code>true</code> if there was a match.
     */
    public boolean looksLike(String key) {
        boolean returnValue = super.looksLike(key);
        //Only with SelectLists we have to check if the key either ends with List (or) SelectList
        if (this == SELECT_LIST) {
            returnValue = returnValue || key.endsWith("List");
        }
        return returnValue;
    }

    
    /**
     * Method to obtain a {@link List} of {@link GUIObjectDetails} containing {@link HtmlSeLionElement}
     * 
     * @param keys
     *            -keys for which {@link GUIObjectDetails} is to be created.
     * @return
     */
    public static List<GUIObjectDetails> getGUIObjectList(List<String> keys) {
        String DELIMITER = "#";
        List<GUIObjectDetails> htmlObjectDetailsList = new ArrayList<GUIObjectDetails>();

        for (String key : keys) {
            String parent = null;
            // If the key contains a delimiter, then html object has a parent
            if (key.contains(DELIMITER)) {
                parent = key.split(DELIMITER)[0];
                key = key.split(DELIMITER)[1];
            }

            HtmlSeLionElement element = HtmlSeLionElement.findMatch(key);
            if (element != null && element.isUIElement()) {
                GUIObjectDetails htmlObjectDetails = null;
                if (element.canHaveParent()) {
                    htmlObjectDetails = new GUIObjectDetails(element.stringify(), key, parent);
                } else {
                    htmlObjectDetails = new GUIObjectDetails(element.stringify(), key);
                }
                htmlObjectDetailsList.add(htmlObjectDetails);
            }
        }
        return htmlObjectDetailsList;
    }

}
