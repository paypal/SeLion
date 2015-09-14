/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.google.common.collect.Lists;
import com.paypal.selion.platform.dataprovider.impl.FileSystemResource;

/**
 * Concrete Yaml reader that is capable of reading Yaml v1 format file.
 */
class YamlV1Reader extends AbstractYamlReader {

    /**
     * This is a public constructor to create an input stream & Yaml instance for the input file.
     * 
     * @param fileName
     *            the name of the YAML data file.
     * @throws IOException
     */
    public YamlV1Reader(String fileName) throws IOException {
        super();
        logger.entering(fileName);
        FileSystemResource resource = new FileSystemResource(fileName);
        processPage(resource);

        logger.exiting();
    }

    /**
     * The user needs to provide the locale for which data needs to be read. After successfully reading the data from
     * the input stream, it is placed in the hash map and returned to the users.
     * 
     * If the locale provided is not set for a certain element it will fall back to the default locale that is set in
     * the Yaml. If default locale is not provided in the Yaml it will use US.
     * 
     * @param locale
     *            Signifies the language or site language to read.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getGuiMap(String locale) {
        logger.entering(locale);

        Map<String, String> instanceMap = new HashMap<String, String>();
        List<Object> allObj = getAllObjects();

        for (Object temp : allObj) {
            Map<String, String> map = (Map<String, String>) temp;
            if (map == null) {
                logger.log(Level.WARNING, "Kindly remove the Null document from "
                        + "the Yaml file. Ignoring the Null document.");
                continue;
            }
            String value = map.get(locale);
            if (value == null) {
                value = map.get(getDefaultLocale());
            }
            instanceMap.put(map.get(KEY), value);
        }

        logger.exiting(instanceMap);
        return instanceMap;
    }

    /**
     * The user needs to provide the locale for which data needs to be read. After successfully reading the data from
     * the input stream, it is placed in the hash map and returned to the users.
     * 
     * @param containerKey
     *            The containerKey to get values
     * @param locale
     *            Signifies the language or site language to read.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getGuiMapForContainer(String containerKey, String locale) {
        logger.entering(new Object[] { containerKey, locale });

        Map<String, String> instanceMap = new HashMap<String, String>();
        List<Object> allObj = getAllObjects();

        for (Object temp : allObj) {
            Map<String, Object> map = (Map<String, Object>) temp;
            if (map == null) {
                logger.log(Level.WARNING, "Kindly remove the Null document from "
                        + "the Yaml file. Ignoring the Null document.");
                continue;
            }

            if (!map.get(KEY).equals(containerKey)) {
                continue;
            }

            // Add child elements of Container
            if (map.containsKey(ELEMENTS)) {
                List<Map<String, String>> elementList = (ArrayList<Map<String, String>>) map.get(ELEMENTS);
                for (Map<String, String> eachElementMap : elementList) {
                    String value = eachElementMap.get(locale);
                    if (value == null) {
                        value = eachElementMap.get(getDefaultLocale());
                    }
                    instanceMap.put(eachElementMap.get(KEY), value);
                }
            } else if (map.containsKey(ELEMENTSv2)) {
                Map<String, Map<String, String>> elementMap = (Map<String, Map<String, String>>) map.get(ELEMENTSv2);
                for (Entry<String, Map<String, String>> eachElement : elementMap.entrySet()) {
                    String value = eachElement.getValue().get(locale);
                    if (value == null) {
                        value = eachElement.getValue().get(getDefaultLocale());
                    }
                    String key = eachElement.getValue().get(KEY);
                    if (key == null) {
                        key = eachElement.getKey();
                    }
                    instanceMap.put(key, value);
                }
            }

        }
        // can this be changed to put try outside the loop

        logger.exiting(instanceMap);
        return instanceMap;
    }


    @Override
    public void processPage(FileSystemResource resource) throws IOException {
        try (InputStream input = resource.getInputStream()) {
            Iterable<Object> it = getYaml().loadAll(input);
            setAllObjects(Lists.newArrayList(it.iterator()));
            setProcessed(true);
        }
    }

}
