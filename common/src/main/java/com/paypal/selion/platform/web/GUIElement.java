/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

package com.paypal.selion.platform.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This page represents the Web elements on a {@link Page}
 */
public final class GUIElement {
    private Map<String, String> locators = new HashMap<>();
    private Map<String, String> ios = new HashMap<>();
    private Map<String, String> android = new HashMap<>();
    private Map<String, HtmlContainerElement> containerElements = new HashMap<>();

    public Map<String, String> getLocators() {
        return Collections.unmodifiableMap(locators);
    }

    public void setLocators(Map<String, String> locators) {
        this.locators = new HashMap<>(locators);
    }

    public Map<String, String> getIos() {
        return Collections.unmodifiableMap(ios);
    }

    public void setIos(Map<String, String> ios) {
        this.ios = ios;
    }

    public Map<String, String> getAndroid() {
        return Collections.unmodifiableMap(android);
    }

    public void setAndroid(Map<String, String> android) {
        this.android = android;
    }

    public Map<String, HtmlContainerElement> getContainerElements() {
        return Collections.unmodifiableMap(containerElements);
    }

    public void setContainerElements(Map<String, HtmlContainerElement> containerElements) {
        this.containerElements = new HashMap<>(containerElements);
    }
}
