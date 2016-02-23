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

package com.paypal.selion.resources;

import static org.testng.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.testng.annotations.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * This test class simply ensures that all *.json files in src/main/resources can be parsed.
 */
public class ParseJsonTest {

    @Test
    public void parseJsonFilesTest() throws FileNotFoundException {
        List<File> jsonFileNameList = (List<File>) FileUtils.listFiles(new File("src/main/resources/"),
                new WildcardFileFilter("*.json"), DirectoryFileFilter.DIRECTORY);
        // Parse the json file
        for (File currentFile : jsonFileNameList) {
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(currentFile)));
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reader);
                assertNotNull(element, "JsonElement returned from parsing is null");
            } catch (JsonSyntaxException j) {
                fail("Error in parsing file:" + currentFile.getPath(), j);
            }
        }
    }
}
