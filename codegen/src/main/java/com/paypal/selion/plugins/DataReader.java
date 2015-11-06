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

package com.paypal.selion.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.paypal.selion.reader.AbstractYamlReader;
import com.paypal.selion.reader.YamlReaderFactory;

/**
 * This class provides basic methods the read the data from the YAML file.
 * 
 */
public class DataReader {
    private final AbstractYamlReader reader;
    private final String fileName;

    public DataReader(String fileName) throws IOException {
        reader = YamlReaderFactory.createInstance(fileName);
        this.fileName = fileName;
    }

    /**
     * Return the base page class name from the PageYaml input
     * 
     * @return A {@link String} representation of the base page class.
     */
    public String getBaseClassName() {
        Logger.getLogger().debug(String.format("Reading base class name from data file [%s]", fileName));
        String baseClass = reader.getBaseClassName();
        if (baseClass == null) {
            String path = new File(fileName).getAbsolutePath();
            throw new IllegalArgumentException("Missing base class details in Data file :" + path);
        }
        return baseClass;
    }
    
    /**
     * Return the platform specified in the PageYaml input
     * 
     * @return {@link TestPlatform}
     */
    public TestPlatform platform() {
        Logger.getLogger().debug(String.format("Specified platform in data file [%s] : [%s] ",fileName,reader.getPlatform()));
        TestPlatform currentPlatform = reader.getPlatform();
        if (currentPlatform == null) {
            String dataFile = new File(fileName).getAbsolutePath();
            throw new IllegalArgumentException("Missing or incorrect platform in Data file:" + dataFile);
        }
        return currentPlatform;
    }

    /**
     * Get the keys defined in the Page meta data <br>
     * <b>Note:</b> Currently, only yaml files are supported.
     * 
     * @return A {@link List} of {@link String} representing the keys found in the meta data. Will return an empty
     *         {@link List} on error or if no keys exist.
     */
    public List<String> getKeys() {
        Logger.getLogger().debug(String.format("Reading keys from data file [%s]", fileName));
        List<String> keys = reader.getAllKeys();
        checkForDuplicateKeys(keys);
        return keys;
    }

    private void checkForDuplicateKeys(List<String> keys) {
        List<String> duplicateKeys = getDuplicateKeys(keys);
        if (!duplicateKeys.isEmpty()) {
            String path = new File(fileName).getAbsolutePath();
            throw new IllegalArgumentException("Data file " + path + " contains duplicate Keys" + duplicateKeys
                    + ". Please fix it.");
        }
    }

    private List<String> getDuplicateKeys(List<String> keys) {
        List<String> result = new ArrayList<>();
        Set<String> keySet = new HashSet<>(keys);

        if (keys.size() == keySet.size()) {
            return result;
        }
        result.addAll(keys);

        for (String temp : keySet) {
            result.remove(temp);
        }
        return result;
    }

}
