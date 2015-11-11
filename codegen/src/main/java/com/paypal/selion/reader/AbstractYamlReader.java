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

package com.paypal.selion.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

import com.paypal.selion.elements.HtmlSeLionElementList;
import com.paypal.selion.platform.web.HtmlContainerElement;
import com.paypal.selion.plugins.TestPlatform;

/**
 * This is an abstract representation of a typical yaml reader.
 */
//TODO Merge this with "clients" version of a class by the same name.. Move merged result to "common"
public abstract class AbstractYamlReader {
    private Yaml yaml;
    private boolean processed;
    private String baseClassName;
    private TestPlatform platform;

    public static final String KEY = "Key";
    public static final String DELIMITER = "#";

    private final List<String> allkeys = new ArrayList<>();

    final Yaml getYaml() {
        if (this.yaml == null) {
            this.yaml = new Yaml();
        }
        return this.yaml;
    }

    final void setBaseClassName(String name) {
        baseClassName = name;
    }

    public String getBaseClassName() {
        return baseClassName;
    }
    
    final void setPlatform(TestPlatform platform){
        this.platform = platform;
    }
    
    public TestPlatform getPlatform(){
        return this.platform;
    }

    final void appendKey(String key) {
        allkeys.add(key);
    }

    public final List<String> getAllKeys() {
        List<String> objects = new ArrayList<>();
        objects.addAll(this.allkeys);
        return objects;
    }

    boolean processed() {
        return processed;
    }

    final void setProcessed(boolean flag) {
        processed = flag;
    }

    protected List<String> parseKeysForContainer(String fileName, List<Object> allElements) {
        List<String> elementKeys = new ArrayList<String>();

        for (Object element : allElements) {
            @SuppressWarnings("unchecked")
            Map<String, Object> elementMap = (Map<String, Object>) element;
            try {
                String elementKey = ((String) elementMap.get(KEY)).trim();
                if ("".equals(elementKey)) {
                    continue;
                }

                if (!(HtmlSeLionElementList.isValid(elementKey))) {
                    throw new IllegalArgumentException(String.format("Detected an invalid key [%s] in data file %s",
                            elementKey, fileName));
                }

                if (HtmlSeLionElementList.CONTAINER.looksLike(elementKey)) {
                    throw new IllegalArgumentException("Cannot define a Container within a Container.");
                }

                elementKeys.add(elementKey);

            } catch (NullPointerException e) {// NOSONAR
                // Gobbling the exception but doing nothing with it.
            }
        }
        return elementKeys;
    }

    protected List<String> parseKeysForContainer(String fileName, Map<String, HtmlContainerElement> allElements) {

        List<String> elementKeys = new ArrayList<String>();

        for (Entry<String, HtmlContainerElement> element : allElements.entrySet()) {
            try {
                String elementKey = element.getKey().trim();

                if (elementKey.equals("")) {
                    continue;
                }

                if (!(HtmlSeLionElementList.isValidHtmlElement(elementKey))) {
                    throw new IllegalArgumentException(String.format("Detected an invalid key [%s] in data file %s",
                            elementKey, fileName));
                }

                if (HtmlSeLionElementList.CONTAINER.looksLike(elementKey)) {
                    throw new IllegalArgumentException("Cannot define a Container within a Container.");
                }

                elementKeys.add(elementKey);

            } catch (NullPointerException e) {// NOSONAR
                // Gobbling the exception but doing nothing with it.
            }
        }
        return elementKeys;
    }

    /**
     * This method processes the contents of a data source (yaml file for e.g.,).
     * @param resource - A {@link FileSystemResource} that represents a particular data source.
     * @throws IOException
     */
    public abstract void processPage(FileSystemResource resource) throws IOException;
}
