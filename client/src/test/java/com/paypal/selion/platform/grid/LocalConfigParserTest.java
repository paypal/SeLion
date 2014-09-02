/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.grid;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

import org.json.JSONException;
import org.testng.annotations.Test;

import com.paypal.selion.platform.grid.LocalGridConfigFileParser;

public class LocalConfigParserTest {
    @Test(groups = "unit")
    public void testConfigParsingAbilities() throws JSONException {
        LocalGridConfigFileParser parser = new LocalGridConfigFileParser();
        assertNotNull(parser.getRequest());
        assertEquals(5, parser.getRequest().getJSONObject("configuration").getInt("maxSession"));
        assertEquals(5555, parser.getPort());
    }
}
