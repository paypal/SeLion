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
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.paypal.selion.elements.HtmlSeLionElementList;
import com.paypal.selion.platform.web.GUIElement;
import com.paypal.selion.platform.web.HtmlContainerElement;
import com.paypal.selion.plugins.Logger;
import com.paypal.selion.platform.web.Page;
import com.paypal.selion.platform.web.PageFactory;
import com.paypal.selion.plugins.TestPlatform;

/**
 * Concrete YAML reader that is capable of reading YAML V2 format file.
 */
//TODO Merge this with "clients" version of a class by the same name.. Move merged result to "common"
class YamlV2Reader extends AbstractYamlReader {

    /**
     * This is a public constructor to create an input stream and YAML instance for the input file.
     * 
     * @param fileName
     *            the name of the YAML data file.
     * @throws IOException
     */
    public YamlV2Reader(String fileName) throws IOException {
        super();
        FileSystemResource resource = new FileSystemResource(fileName);
        processPage(resource);
    }

    @Override
    public void processPage(FileSystemResource resource) throws IOException {
        try {
            InputStream is = resource.getInputStream();
            String fileName = resource.getFileName();
            Page page = PageFactory.getPage(is);
            setBaseClassName(page.getBaseClass());
            Logger.getLogger().debug(String.format("++ Attempting to process %s as PageYAML V2", fileName));

            TestPlatform currentPlatform = TestPlatform.identifyPlatform(page.getPlatform());
            if (currentPlatform == null) {
                throw new IllegalArgumentException("Missing or invalid platform specified in " + fileName);
            }

            setPlatform(currentPlatform);

            for (Entry<String, GUIElement> eachElement : page.getElements().entrySet()) {
                if (!eachElement.getKey().isEmpty()) {

                    appendKey(eachElement.getKey());
                    if ((currentPlatform == TestPlatform.WEB)
                            && HtmlSeLionElementList.CONTAINER.looksLike(eachElement.getKey())) {
                        if (!eachElement.getValue().getContainerElements().isEmpty()) {
                            Map<String, HtmlContainerElement> allElements = eachElement.getValue()
                                    .getContainerElements();
                            List<String> elementKeys = parseKeysForContainer(fileName, allElements);
                            for (String elementKey : elementKeys) {
                                // concat parent key separated with # to retain association
                                appendKey(eachElement.getKey() + DELIMITER + elementKey);
                            }
                        }
                    }
                }
            }
            setProcessed(true);
        } catch (Exception e) { // NOSONAR
            // Just log an debug message. The input is probably not a V2 PageYAML
            Logger.getLogger().debug(
                    String.format("Unable to process %s as PageYAML V2.\n\t %s", resource.getFileName(),
                            e.getLocalizedMessage()));
        }
    }
}
