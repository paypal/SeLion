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

import java.util.Arrays;
import java.util.Collection;

/**
 * The class represents the elements that can be given in a page object data source and are recognized by iOS platform
 */
public class IOSSeLionElementSet extends BaseMobileSeLionElementSet {
    private static final String ELEMENT_PACKAGE = "com.paypal.selion.platform.mobile.ios";

    private static final IOSSeLionElementSet INSTANCE =
        new IOSSeLionElementSet(Arrays.asList(IOSSeLionElement.UIA_BUTTON, IOSSeLionElement.UIA_ALERT,
            IOSSeLionElement.UIA_NAVIGATION_BAR, IOSSeLionElement.UIA_PICKER,
            IOSSeLionElement.UIA_SLIDER, IOSSeLionElement.UIA_STATIC_TEXT,
            IOSSeLionElement.UIA_SWITCH, IOSSeLionElement.UIA_TABLE_VIEW,
            IOSSeLionElement.UIA_TEXT_FIELD, IOSSeLionElement.UIA_ELEMENT,
            IOSSeLionElement.BASE_CLASS));

    public static final class IOSSeLionElement extends SeLionElement {

        public static final SeLionElement UIA_BUTTON =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIAButton", true);
        public static final SeLionElement UIA_ALERT =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIAAlert", true);
        public static final SeLionElement UIA_NAVIGATION_BAR =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIANavigationBar", true);
        public static final SeLionElement UIA_PICKER =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIAPicker", true);
        public static final SeLionElement UIA_SLIDER =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIASlider", true);
        public static final SeLionElement UIA_STATIC_TEXT =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIAStaticText", true);
        public static final SeLionElement UIA_SWITCH =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIASwitch", true);
        public static final SeLionElement UIA_TABLE_VIEW =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIATableView", true);
        public static final SeLionElement UIA_TEXT_FIELD =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIATextField", true);
        public static final SeLionElement UIA_ELEMENT =
            new IOSSeLionElement(ELEMENT_PACKAGE, "UIAElement", true);
        public static final SeLionElement BASE_CLASS =
            new IOSSeLionElement(null, "baseClass", false);

        public IOSSeLionElement(String elementPackage, String elementClass, boolean uiElement) {
            super(elementPackage, elementClass, uiElement);
        }
    }

    IOSSeLionElementSet(Collection<SeLionElement> elements) {
        super(elements);
    }

    public static IOSSeLionElementSet getInstance() {
        return INSTANCE;
    }
}
