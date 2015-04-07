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

package com.paypal.selion.node.servlets;

import com.paypal.selion.utils.ConfigParser;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertTrue;

@PrepareForTest({ ConfigParser.class })
public class ProcessShutdownHandlerTest extends PowerMockTestCase {

    @BeforeClass
    public void setUp() {
        ConfigParser configParser = PowerMockito.mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.getInstance()).thenReturn(configParser);

        if (System.getProperty("os.name").startsWith("Windows")) {
            when(configParser.getString("customProcessHandler")).thenReturn(
                    "com.paypal.selion.utils.process.WindowsProcessHandler");
        } else {
            when(configParser.getString("customProcessHandler")).thenReturn(
                    "com.paypal.selion.utils.process.NonWindowsProcessHandler");
        }
    }

    @Test
    public void testShutdownProcesses() throws Exception {
        // Start phantomJS (it's available in CI)
        Process phantom = Runtime.getRuntime().exec(new String[] {"phantomjs"} );
        ProcessShutdownHandler shutdownHandler = new ProcessShutdownHandler();
        shutdownHandler.shutdownProcesses();
        phantom.waitFor();
        // check no phantom child existed (by kill)
        assertTrue(phantom.exitValue() > 0);
        phantom.destroy();
    }
}