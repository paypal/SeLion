/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;

import java.io.File;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@PrepareForTest(ConfigParser.class)
public class ConfigParserTest extends PowerMockTestCase {

    @BeforeClass
    public void before() throws Exception {
        mockStatic(ConfigParser.class);
        doCallRealMethod().when(ConfigParser.class, "setConfigFile", Mockito.anyString());
        when(ConfigParser.parse()).thenCallRealMethod();
        doCallRealMethod().when(ConfigParser.class, "readConfigFileContents");

        // use the mock instance to allow for invocation of setConfigFile
        ConfigParser.setConfigFile(new File(ConfigParserTest.class.getResource("/config/DummySeLionConfig.json")
                .getPath()).getAbsolutePath());
    }

    @Test
    public void testGets() throws Exception {
        ConfigParser config = ConfigParser.parse();

        //access properties that should only exist in the mock
        int i = config.getInt("Key1");
        String s = config.getString("Key2");
        long l = config.getLong("Key3");

        assertEquals(i, 1000);
        assertEquals(s, "Sample");
        assertEquals(l, 250000000L);
    }

    @Test
    public void testGetsWithDefault() throws Exception {
        ConfigParser config = ConfigParser.parse();

        // access properties that do not exist.
        long maxFileSize = config.getLong("along", 10L);
        String managedArtifact = config.getString("astring", "default");
        int sessionCount = config.getInt("anint", 10);

        assertEquals(maxFileSize, 10L);
        assertEquals(managedArtifact, "default");
        assertEquals(sessionCount, 10);
    }
}