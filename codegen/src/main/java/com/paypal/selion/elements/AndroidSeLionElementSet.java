/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2017 PayPal                                                                                     |
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
 * The class represents the elements that can be given in a page object data source and are recognized by Android
 * platform
 */
public class AndroidSeLionElementSet extends BaseMobileSeLionElementSet {
    private static final String ELEMENT_PACKAGE = "com.paypal.selion.platform.mobile.android";

    private static final AndroidSeLionElementSet INSTANCE =
        new AndroidSeLionElementSet(Arrays.asList(AndroidSeLionElement.UI_BUTTON, AndroidSeLionElement.UI_LIST,
            AndroidSeLionElement.UI_SLIDER, AndroidSeLionElement.UI_SWITCH,
            AndroidSeLionElement.UI_TEXT_VIEW, AndroidSeLionElement.UI_OBJECT,
            AndroidSeLionElement.BASE_CLASS));

    public static final class AndroidSeLionElement extends SeLionElement {

        public static final SeLionElement UI_BUTTON =
            new AndroidSeLionElement(ELEMENT_PACKAGE, "UiButton", true);
        public static final SeLionElement UI_LIST =
            new AndroidSeLionElement(ELEMENT_PACKAGE, "UiList", true);
        public static final SeLionElement UI_SLIDER =
            new AndroidSeLionElement(ELEMENT_PACKAGE, "UiSlider", true);
        public static final SeLionElement UI_SWITCH =
            new AndroidSeLionElement(ELEMENT_PACKAGE, "UiSwitch", true);
        public static final SeLionElement UI_TEXT_VIEW =
            new AndroidSeLionElement(ELEMENT_PACKAGE, "UiTextView", true);
        public static final SeLionElement UI_OBJECT =
            new AndroidSeLionElement(ELEMENT_PACKAGE, "UiObject", true);
        public static final SeLionElement BASE_CLASS =
            new AndroidSeLionElement(null, "baseClass", false);

        public AndroidSeLionElement(String elementPackage, String elementClass, boolean uiElement) {
            super(elementPackage, elementClass, uiElement);
        }
    }

    AndroidSeLionElementSet(Collection<SeLionElement> elements) {
        super(elements);
    }

    public static AndroidSeLionElementSet getInstance() {
        return INSTANCE;
    }
}
