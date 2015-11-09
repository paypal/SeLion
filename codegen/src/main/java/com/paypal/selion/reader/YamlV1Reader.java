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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.paypal.selion.elements.HtmlSeLionElementList;
import com.paypal.selion.plugins.Logger;
import com.paypal.selion.plugins.TestPlatform;

/**
 * Concrete Yaml reader that is capable of reading Yaml v1 format file.
 */
//TODO Merge this with "clients" version of a class by the same name.. Move merged result to "common"
class YamlV1Reader extends AbstractYamlReader {

    private static final String ELEMENTS = "Elements";

    /**
     * This is a public constructor to create an input stream and YAML instance for the input file.
     * 
     * @param fileName
     *            the name of the YAML data file.
     * @throws IOException
     */
    public YamlV1Reader(String fileName) throws IOException {
        super();
        FileSystemResource resource = new FileSystemResource(fileName);
        processPage(resource);

    }

    @Override
    // TODO PageYAML V1 only needs to support platform = "web". As a result, this method needs re-factoring.
    public void processPage(FileSystemResource resource) throws IOException {
        boolean platformDefined = false;
        String fileName = resource.getFileName();
        InputStream is = resource.getInputStream();
        // Try to load PageYAML v1
        Logger.getLogger().debug(String.format("++ Attempting to process %s as PageYAML V1", fileName));

        Iterable<Object> allObjects = getYaml().loadAll(new BufferedReader(new InputStreamReader(is, "UTF-8")));
        try {
            for (Object data : allObjects) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) data;

                String key = ((String) map.get(KEY)).trim();
                if (key.equals("")) {
                    continue;
                }

                if ("baseClass".equals(map.get(KEY))) {
                    Logger.getLogger().debug(
                            String.format("++ Retrieved [%s] as the base class in [%s] PageYAML V1.", map.get("Value"),
                                    fileName));
                    setBaseClassName((String) map.get("Value"));
                }

                if ("platform".equals(map.get(KEY))) {
                    if (!platformDefined) {
                        TestPlatform currentPlatform = TestPlatform.identifyPlatform((String) map.get("Value"));
                        if (currentPlatform == null) {
                            String dataFile = new File(fileName).getAbsolutePath();
                            throw new IllegalArgumentException("Missing or incorrect platform in Data file:" + dataFile);
                        }
                        setPlatform(currentPlatform);
                        platformDefined = true;
                    }
                }
            }

            // No entry in file? Set platform to default : WEB
            if (!platformDefined) {
                setPlatform(TestPlatform.WEB);
            }

            // If this part is reached its safe to read the platform
            TestPlatform currentPlatform = getPlatform();

            // Continuing to iterate with the map after deciding the platform
            is.close();
            InputStream newStream = resource.getInputStream();
            Iterable<Object> allObjects1 = getYaml().loadAll(
                    new BufferedReader(new InputStreamReader(newStream, "UTF-8")));
            for (Object data : allObjects1) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) data;
                String key = ((String) map.get(KEY)).trim();
                if ("".equals(key)) {
                    continue;
                }
                if (map.get(KEY).equals("baseClass") || map.get(KEY).equals("platform")) {
                    continue;
                }

                appendKey(key);

                // TODO Container support for IOS element still needs to analyzed
                if (canHaveContainers(currentPlatform, key, map)) {
                    @SuppressWarnings("unchecked")
                    ArrayList<Object> allElements = (ArrayList<Object>) map.get(ELEMENTS);
                    List<String> elementKeys = parseKeysForContainer(fileName, allElements);
                    for (String elementKey : elementKeys) {
                        // concat parent key separated with # to retain association
                        appendKey(key + DELIMITER + elementKey);
                    }
                }
            }
            setProcessed(true);
        } catch (Exception e) {// NOSONAR
            // Just log a debug message. The input is probably not a V1 PageYAML
            Logger.getLogger().debug(
                    String.format("Unable to process %s as PageYAML V1.\n\t %s", resource.getFileName(),
                            e.getLocalizedMessage()));
        }
    }
    
    private boolean canHaveContainers(TestPlatform platform, String key, Map<?, ?> map) {
        if (platform != TestPlatform.WEB) {
            return false;
        }
        return HtmlSeLionElementList.CONTAINER.looksLike(key) && map.keySet().contains(ELEMENTS);
    }
}
