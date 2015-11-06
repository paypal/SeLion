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

package com.paypal.selion.internal.platform.pageyaml;

import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.web.Page;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This is an abstract representation of a typical yaml reader.
 */
abstract class AbstractYamlReader implements GuiMapReader, ProcessGuiMap {
    private Yaml yaml;
    private String defaultLocale = DEFAULT_LOCALE;
    private Page pageYaml;
    protected static final SimpleLogger logger = SeLionLogger.getLogger();
    private boolean processed;

    private final List<Object> allObjects = new ArrayList<Object>();

    final String getDefaultLocale() {
        return defaultLocale;
    }

    final void setDefaultLocale(String locale) {
        this.defaultLocale = locale;
    }

    final Page getPageYaml() {
        return pageYaml;
    }

    final Yaml getYaml() {
        if (this.yaml == null) {
            this.yaml = new Yaml();
        }
        return this.yaml;
    }

    final void setPage(Page page) {
        this.pageYaml = page;
    }

    final void setAllObjects(List<Object> allObjects) {
        this.allObjects.addAll(allObjects);
    }

    final void appendObject(Object obj) {
        allObjects.add(obj);
    }

    final List<Object> getAllObjects() {
        List<Object> objects = new ArrayList<Object>();
        objects.addAll(this.allObjects);
        return objects;
    }

    /**
     * Returns a list of elements that can be used for validating pages.
     * 
     * @return list of elements
     */
    @Override
    public List<String> getPageValidators() {
        if (getPageYaml() != null) {
            return getPageYaml().getPageValidators();
        }

        return new ArrayList<String>();
    }

    @Override
    public boolean processed() {
        return processed;
    }

    final void setProcessed(boolean flag) {
        processed = flag;
    }

}
