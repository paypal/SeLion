/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import com.paypal.selion.plugins.GUIObjectDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MobileSeLionElementList extends AbstractSeLionElementList {
    public static final String UIAUTOMATION_ELEMENT_CLASS = "com.paypal.selion.platform.mobile.elements";

    public static final MobileSeLionElementList Button =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "Button", true);
    public static final MobileSeLionElementList Element =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "Element", true);
    public static final MobileSeLionElementList Image =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "Image", true);
    public static final MobileSeLionElementList Label =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "Label", true);
    public static final MobileSeLionElementList List =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "List", true);
    public static final MobileSeLionElementList Picker =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "Picker", true);
    public static final MobileSeLionElementList Slider =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "Slider", true);
    public static final MobileSeLionElementList Switch =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "Switch", true);
    public static final MobileSeLionElementList TextField =
            new MobileSeLionElementList(UIAUTOMATION_ELEMENT_CLASS, "TextField", true);

    public static final MobileSeLionElementList BASE_CLASS =
            new MobileSeLionElementList(null, "baseClass", false);

    private static MobileSeLionElementList[] values = { Button, Element, Image, Label, List, Picker, Slider, Switch, TextField, BASE_CLASS };

    protected MobileSeLionElementList(String elementPackage, String element, boolean uiElement) {
        super(elementPackage, element, uiElement);
    }

    /**
     * By providing the qualified name of a custom element we can register it to the element array. Custom elements are
     * inserted before SeLion elements, if you use the same name it will overwrite the existing element.
     *
     * @param element
     *            string of the qualified class
     */
    public static void registerElement(String element) {
        List<MobileSeLionElementList> temp = new ArrayList<>(Arrays.asList(values));

        temp.add(0, new MobileSeLionElementList(HtmlElementUtils.getPackage(element), HtmlElementUtils.getClass(element),
                        true));

        values = temp.toArray(new MobileSeLionElementList[temp.size()]);
    }

    /**
     * @param rawType
     *            The String using which an attempt to find a matching {@link MobileSeLionElementList} is to be
     *            performed.
     * @return A {@link MobileSeLionElementList} if the type ends with one of the values of
     *         {@link MobileSeLionElementList} that were passed as android elements (or) <code>null</code> if there
     *         were no matches.
     */
    public static MobileSeLionElementList findMatch(String rawType) {
        return (MobileSeLionElementList) findMatch(values, rawType);
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
     *            The element that needs to be tested for being a valid {@link MobileSeLionElementList} and whose
     *            {@link MobileSeLionElementList#isUIElement()} returns true.
     * @return <code>true</code> if there was a match and <code>false</code> otherwise.
     */
    public static boolean isValidUIElement(String element) {
        return isValidUIElement(values, element);

    }

    public static List<GUIObjectDetails> getGUIObjectList(List<String> keys) {
        List<GUIObjectDetails> mobileObjectDetailsList = new ArrayList<>();

        for (String key : keys) {
            MobileSeLionElementList element = MobileSeLionElementList.findMatch(key);
            if (element != null && element.isUIElement()) {
                GUIObjectDetails mobileObjectDetails;
                if (element.getElementPackage().equals(UIAUTOMATION_ELEMENT_CLASS)) {
                    mobileObjectDetails = new GUIObjectDetails("Mobile" + element.stringify(), key, element.getElementPackage(), element.stringify());
                } else {
                    mobileObjectDetails = new GUIObjectDetails(element.stringify(), key, element.getElementPackage());
                }
                mobileObjectDetailsList.add(mobileObjectDetails);
            }
        }
        return mobileObjectDetailsList;
    }

}
