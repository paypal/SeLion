/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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
import java.util.Collection;
import java.util.List;

import com.paypal.selion.plugins.GUIObjectDetails;

/**
 * This class represents the set of elements that can occur in a page object data source which are recognized by SeLion.
 */
public class HtmlSeLionElementSet extends SeLionElementSet {
    private static final String ELEMENT_PACKAGE = "com.paypal.selion.platform.html";

    // DO NOT alter the order of RADIO_BUTTON and BUTTON because of the following reason:
    // consider a field in the yaml file which looks like this : fxBankRadioButton
    // if we have Button ahead of RadioButton, then our code would end up matching this above key with
    // Button instead of matching it against RadioButton. This is the ONLY case wherein the order is very important
    private static final HtmlSeLionElementSet INSTANCE = new HtmlSeLionElementSet(
        Arrays.asList(HtmlSeLionElement.TEXT_FIELD, HtmlSeLionElement.TABLE, HtmlSeLionElement.SELECT_LIST,
            HtmlSeLionElement.RADIO_BUTTON, HtmlSeLionElement.BUTTON, HtmlSeLionElement.LINK, HtmlSeLionElement.LABEL,
            HtmlSeLionElement.IMAGE, HtmlSeLionElement.FORM, HtmlSeLionElement.DATE_PICKER, HtmlSeLionElement.CHECK_BOX,
            HtmlSeLionElement.CONTAINER, HtmlSeLionElement.BASE_CLASS, HtmlSeLionElement.PAGE_TITLE));

    public static final class HtmlSeLionElement extends SeLionElement {

        public static final SeLionElement TEXT_FIELD =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "TextField", true, true);
        public static final SeLionElement TABLE =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "Table", true, true);
        public static final SeLionElement SELECT_LIST =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "SelectList", true, true);
        public static final SeLionElement RADIO_BUTTON =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "RadioButton", true, true);
        public static final SeLionElement BUTTON =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "Button", true, true);
        public static final SeLionElement LINK =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "Link", true, true);
        public static final SeLionElement LABEL =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "Label", true, true);
        public static final SeLionElement IMAGE =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "Image", true, true);
        public static final SeLionElement FORM =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "Form", true, true);
        public static final SeLionElement DATE_PICKER =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "DatePicker", true, true);
        public static final SeLionElement CHECK_BOX =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "CheckBox", true, true);
        public static final SeLionElement CONTAINER =
            new HtmlSeLionElement(ELEMENT_PACKAGE, "Container", true, false);
        public static final SeLionElement BASE_CLASS =
            new HtmlSeLionElement(null, "baseClass", false, false);
        public static final SeLionElement PAGE_TITLE =
            new HtmlSeLionElement(null, "pageTitle", false, false);

        private final boolean isParentAllowed;

        public HtmlSeLionElement(String element) {
            super(element);
            isParentAllowed = true;
        }

        public HtmlSeLionElement(String elementPackage, String elementClass, boolean uiElement, boolean parentAllowed) {
            super(elementPackage, elementClass, uiElement);
            isParentAllowed = parentAllowed;
        }

        /**
         * @return <code>true</code> if the current element can have a parent to it.
         */
        public boolean canHaveParent() {
            return isParentAllowed;
        }

        /**
         * This method returns <code>true</code> if the element key name that was given ends with the textual value of one
         * of the elements that are part of {@link HtmlSeLionElementSet}
         *
         * @param elementKey The string that needs to be checked.
         * @return <code>true</code> if there was a match.
         */
        @Override
        public boolean looksLike(String elementKey) {
            boolean returnValue = super.looksLike(elementKey);
            // Only with SelectLists we have to check if the key either ends with List (or) SelectList
            if (this == SELECT_LIST) {
                returnValue = returnValue || elementKey.endsWith("List");
            }
            return returnValue;
        }

        @Override
        public String toString() {
            return String.format("%s, isParentAllowed=%s", super.toString(), isParentAllowed);
        }
    }

    HtmlSeLionElementSet(Collection<SeLionElement> elements) {
        super(elements);
    }

    public static HtmlSeLionElementSet getInstance() {
        return INSTANCE;
    }

    public boolean add(String element) {
        return super.add(new HtmlSeLionElement(element));
    }

    /**
     * Method to obtain a {@link List} of {@link GUIObjectDetails} containing {@link HtmlSeLionElementSet}
     *
     * @param keys keys for which {@link GUIObjectDetails} is to be created.
     * @return the {@link List} of {@link GUIObjectDetails}
     */
    public List<GUIObjectDetails> getGUIObjectList(List<String> keys) {
        String DELIMITER = "#";
        List<GUIObjectDetails> htmlObjectDetailsList = new ArrayList<>();

        for (String key : keys) {
            String parent = null;
            // If the key contains a delimiter, then html object has a parent
            if (key.contains(DELIMITER)) {
                parent = key.split(DELIMITER)[0];
                key = key.split(DELIMITER)[1];
            }

            HtmlSeLionElement element = (HtmlSeLionElement) findMatch(key);
            if (element != null && element.isUIElement()) {
                GUIObjectDetails htmlObjectDetails =
                    new GUIObjectDetails(element.getElementClass(), key, element.getElementPackage());
                if (element.canHaveParent()) {
                    htmlObjectDetails = new GUIObjectDetails(element.getElementClass(), key,
                        element.getElementPackage(), parent);
                }
                htmlObjectDetailsList.add(htmlObjectDetails);
            }
        }
        return htmlObjectDetailsList;
    }

}
