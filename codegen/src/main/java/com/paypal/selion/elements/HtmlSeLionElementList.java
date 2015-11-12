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
 * This class represents the set of elements that can occur in a page object data source which are recognized by SeLion.
 *
 */
public class HtmlSeLionElementList extends AbstractSeLionElementList {
    public static final String SELION_ELEMENT_CLASS = "com.paypal.selion.platform.html";
    
    public static final HtmlSeLionElementList TEXT_FIELD = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "TextField", true, true);
    public static final HtmlSeLionElementList TABLE = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "Table", true, true);
    public static final HtmlSeLionElementList SELECT_LIST = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "SelectList", true, true);
    public static final HtmlSeLionElementList RADIO_BUTTON = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "RadioButton", true, true);
    public static final HtmlSeLionElementList BUTTON = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "Button", true, true);
    public static final HtmlSeLionElementList LINK = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "Link", true, true);
    public static final HtmlSeLionElementList LABEL = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "Label", true, true);
    public static final HtmlSeLionElementList IMAGE = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "Image", true, true);
    public static final HtmlSeLionElementList FORM = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "Form", true, true);
    public static final HtmlSeLionElementList DATE_PICKER = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "DatePicker", true, true);
    public static final HtmlSeLionElementList CHECK_BOX = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "CheckBox", true, true);
    public static final HtmlSeLionElementList CONTAINER = new HtmlSeLionElementList(SELION_ELEMENT_CLASS, "Container", true, false);
    public static final HtmlSeLionElementList BASE_CLASS = new HtmlSeLionElementList(null, "baseClass", false, false);
    public static final HtmlSeLionElementList PAGE_TITLE = new HtmlSeLionElementList(null, "pageTitle", false, false);

    //DONOT alter the order of RADIO_BUTTON and BUTTON because of the following reason:
    //consider a field in the yaml file which looks like this : fxBankRadioButton
    //if we have Button ahead of RadioButton, then our code would end up matching this above key with
    //Button instead of matching it against RadioButton. This is the ONLY case wherein the order is very important

    private static HtmlSeLionElementList[] values = {TEXT_FIELD, TABLE, SELECT_LIST, RADIO_BUTTON, BUTTON, LINK, LABEL,
            IMAGE, FORM, DATE_PICKER, CHECK_BOX, CONTAINER, BASE_CLASS, PAGE_TITLE
    };

    @Override
    public String toString() {
        return String.format("%s, canHaveParent=%s",super.toString(), canHaveParent);
    }
    
    /**
     * By providing the qualified name of a custom element we can register it to the element array.
     * Custom elements are inserted before SeLion elements, if you use the same name it will overwrite the existing element.
     * 
     * @param element string of the qualified class
     */
    public static void registerElement(String element) {
        List<HtmlSeLionElementList> temp = new ArrayList<HtmlSeLionElementList>(Arrays.asList(values));
        
        temp.add(0, new HtmlSeLionElementList(HtmlElementUtils.getPackage(element), HtmlElementUtils.getClass(element), true, false));
        
        values = temp.toArray(new HtmlSeLionElementList[temp.size()]);
    }

    /**
     * @param rawType The String using which an attempt to find a matching {@link HtmlSeLionElementList} is to be performed.
     * @return A {@link HtmlSeLionElementList} if the type ends with one of the values of {@link HtmlSeLionElementList} enum (or)
     * <code>null</code> if there were no matches.
     */
    public static HtmlSeLionElementList findMatch(String rawType) {
        return (HtmlSeLionElementList) findMatch(values, rawType);
    }

    /**
     * @param element The element that needs to be tested for being a valid {@link HtmlSeLionElementList}
     * @return <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValid(String element) {
        return isValid(values, element);
    }
    
    /**
     * @param element The element that needs to be searched.
     * @return <code>true</code> if the element was found in the set of elements provided.
     */
    public static boolean isExactMatch(String element) {
        return isExactMatch(values, element);
    }

    /**
     * @param element The element that needs to be tested for being a valid {@link HtmlSeLionElementList} and whose
     * {@link HtmlSeLionElementList#isUIElement()} returns true.
     * @return <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValidHtmlElement(String element) {
        return isValidUIElement(values, element);
    }

    private final boolean canHaveParent;

    private HtmlSeLionElementList(String elementPackage, String element, boolean isHtmlType, boolean canHaveParent) {
        super(elementPackage, element, isHtmlType);
        this.canHaveParent = canHaveParent;
    }

    /**
     * @return <code>true</code> if the current element can have a parent to it.
     */
    public boolean canHaveParent() {
        return canHaveParent;
    }

    /**
     * This method returns <code>true</code> if the key that was given ends with the textual value of one of the 
     * elements that are part of {@link HtmlSeLionElementList}
     * @param key The string that needs to be checked.
     * @return <code>true</code> if there was a match.
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
     * Method to obtain a {@link List} of {@link GUIObjectDetails} containing {@link HtmlSeLionElementList}
     * 
     * @param keys
     *            keys for which {@link GUIObjectDetails} is to be created.
     * @return the {@link List} of {@link GUIObjectDetails}
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

            HtmlSeLionElementList element = HtmlSeLionElementList.findMatch(key);
            if (element != null && element.isUIElement()) {
                GUIObjectDetails htmlObjectDetails = null;
                if (element.canHaveParent()) {
                    htmlObjectDetails = new GUIObjectDetails(element.stringify(), key, element.getElementPackage(), parent);
                } else {
                    htmlObjectDetails = new GUIObjectDetails(element.stringify(), key, element.getElementPackage());
                }
                htmlObjectDetailsList.add(htmlObjectDetails);
            }
        }
        return htmlObjectDetailsList;
    }

}
