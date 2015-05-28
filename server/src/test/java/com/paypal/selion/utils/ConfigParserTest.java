/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.utils;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ConfigParserTest {

    @Test
    public void testSetConfig() {
        // Mock the location of the config file
        ConfigParser.setConfigFile("src/test/resources/config/DummySeLionConfig.json");
        ConfigParser parser = ConfigParser.getInstance();
        int key1 = parser.getInt("Key1");
        String key2 = parser.getString("Key2");
        long key3 = parser.getLong("Key3");
        assertEquals(1000, key1);
        assertEquals("Sample", key2);
        assertEquals(250000000, key3);
    }
}