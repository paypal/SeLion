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

package com.paypal.selion.platform.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * This class represents the PageYaml V2 file.</br> </br> <b>Example file:</b>
 * 
 * <pre>
 * baseClass: "com.paypal.selion.testcomponents.BasicPageImpl"
 * pageTitle:
 *   US: "API Page"
 * elements:
 *   submitButton:
 *     US: "//div[@id='apiOption1']/p[3]/a"
 * pageValidators: [submitButton]
 * defaultLocale: US
 * </pre>
 */
public final class Page {
    private Map<String, String> pageTitle = new HashMap<String, String>();
    private String baseClass;
    private String platform = "web";
    private Map<String, GUIElement> elements = new HashMap<String, GUIElement>();
    private String defaultLocale = "US";
    private List<String> pageValidators = new ArrayList<String>();

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getDefaultLocale() {
        return this.defaultLocale;
    }

    public void setPageValidators(List<String> pageValidators) {
        this.pageValidators = new ArrayList<String>();
        this.pageValidators.addAll(pageValidators);
    }

    public List<String> getPageValidators() {
        return Collections.unmodifiableList(this.pageValidators);
    }

    public void setElements(Map<String, GUIElement> elements) {
        this.elements = new HashMap<String, GUIElement>();
        this.elements.putAll(elements);
    }

    public Map<String, GUIElement> getElements() {
        return Collections.unmodifiableMap(this.elements);
    }

    public void setPageTitle(Map<String, String> pageTitle) {
        this.pageTitle = new HashMap<String, String>();
        this.pageTitle.putAll(pageTitle);
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public String getBaseClass() {
        return this.baseClass;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getPlatform() {
        if (StringUtils.isBlank(platform)) {
            throw new IllegalArgumentException("The platform cannot be empty or null. Please specify a valid platform.");
        }
        return this.platform;
    }

    public Map<String, String> getAllPageTitles() {
        return Collections.unmodifiableMap(this.pageTitle);
    }

    String getPageTitle() {
        return getPageTitle(this.defaultLocale);
    }

    String getPageTitle(String locale) {
        return this.pageTitle.get(locale);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("pageTitle: ").append(this.pageTitle.get(this.defaultLocale)).append("\n");
        sb.append("baseClass: ").append(this.baseClass).append("\n");
        sb.append("elements: ").append(this.elements.size()).append("\n");
        sb.append("pageValidators: ").append(this.pageValidators.size()).append("\n");
        sb.append("defaultLocale: ").append(this.defaultLocale).append("\n");
        return sb.toString();
    }
}