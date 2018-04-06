/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016-2017 PayPal                                                                                     |
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
import java.util.Collection;
import java.util.List;

/**
 * The class represents the elements that can be given in a page object data source and are common to both the iOS and
 * Android platforms
 */
public class MobileSeLionElementSet extends BaseMobileSeLionElementSet {
    private static final String ELEMENT_PACKAGE = "com.paypal.selion.platform.mobile.elements";

    private static final MobileSeLionElementSet INSTANCE =
        new MobileSeLionElementSet(Arrays.asList(MobileSeLionElement.BUTTON, MobileSeLionElement.ELEMENT,
            MobileSeLionElement.IMAGE, MobileSeLionElement.LABEL,
            MobileSeLionElement.LIST, MobileSeLionElement.PICKER,
            MobileSeLionElement.SLIDER, MobileSeLionElement.SWITCH,
            MobileSeLionElement.TEXTFIELD, MobileSeLionElement.BASE_CLASS));

    public static final class MobileSeLionElement extends SeLionElement {

        public static final SeLionElement BUTTON =
            new MobileSeLionElement(ELEMENT_PACKAGE, "Button", true);
        public static final SeLionElement ELEMENT =
            new MobileSeLionElement(ELEMENT_PACKAGE, "Element", true);
        public static final SeLionElement IMAGE =
            new MobileSeLionElement(ELEMENT_PACKAGE, "Image", true);
        public static final SeLionElement LABEL =
            new MobileSeLionElement(ELEMENT_PACKAGE, "Label", true);
        public static final SeLionElement LIST =
            new MobileSeLionElement(ELEMENT_PACKAGE, "List", true);
        public static final SeLionElement PICKER =
            new MobileSeLionElement(ELEMENT_PACKAGE, "Picker", true);
        public static final SeLionElement SLIDER =
            new MobileSeLionElement(ELEMENT_PACKAGE, "Slider", true);
        public static final SeLionElement SWITCH =
            new MobileSeLionElement(ELEMENT_PACKAGE, "Switch", true);
        public static final SeLionElement TEXTFIELD =
            new MobileSeLionElement(ELEMENT_PACKAGE, "TextField", true);
        public static final SeLionElement BASE_CLASS =
            new MobileSeLionElement(null, "baseClass", false);

        public MobileSeLionElement(String elementPackage, String elementClass, boolean uiElement) {
            super(elementPackage, elementClass, uiElement);
        }
    }

    MobileSeLionElementSet(Collection<SeLionElement> elements) {
        super(elements);
    }

    public static MobileSeLionElementSet getInstance() {
        return INSTANCE;
    }

    @Override
    public List<GUIObjectDetails> getGUIObjectList(List<String> keys) {
        List<GUIObjectDetails> mobileObjectDetailsList = new ArrayList<>();

        for (String key : keys) {
            SeLionElement element = findMatch(key);
            if (element != null && element.isUIElement()) {
                GUIObjectDetails mobileObjectDetails =
                    new GUIObjectDetails(element.getElementClass(), key, element.getElementPackage());
                if (element.getElementPackage().equals(ELEMENT_PACKAGE)) {
                    mobileObjectDetails = new GUIObjectDetails("Mobile" + element.getElementClass(), key,
                        element.getElementPackage(), element.getElementClass());
                }
                mobileObjectDetailsList.add(mobileObjectDetails);
            }
        }
        return mobileObjectDetailsList;
    }

}
