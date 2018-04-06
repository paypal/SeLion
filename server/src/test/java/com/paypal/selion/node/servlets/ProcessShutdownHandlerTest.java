/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

import static org.powermock.api.mockito.PowerMockito.*;

import com.paypal.selion.pojos.ProcessNames;
import com.paypal.selion.utils.ConfigParser;

import org.apache.commons.lang.SystemUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@PrepareForTest({ ConfigParser.class, ProcessNames.class })
public class ProcessShutdownHandlerTest extends PowerMockTestCase {

    @BeforeClass
    public void setUp() {
        ConfigParser configParser = mock(ConfigParser.class);
        mockStatic(ConfigParser.class);
        when(ConfigParser.parse()).thenReturn(configParser);

        if (SystemUtils.IS_OS_WINDOWS) {
            when(configParser.getString("customProcessHandler")).thenReturn(
                    "com.paypal.selion.utils.process.WindowsProcessHandler");
        } else {
            when(configParser.getString("customProcessHandler")).thenReturn(
                    "com.paypal.selion.utils.process.UnixProcessHandler");
        }

        mockStatic(ProcessNames.class);
        Whitebox.setInternalState(ProcessNames.PHANTOMJS, "unixImageName", "phantomjs");
        Whitebox.setInternalState(ProcessNames.PHANTOMJS, "windowsImageName", "notepad.exe");

        when(ProcessNames.values()).thenReturn(new ProcessNames[] { ProcessNames.PHANTOMJS });
    }

    @Test
    public void testShutdownProcesses() throws Exception {
        // Start phantomJS (it's available in CI)
        String app = SystemUtils.IS_OS_WINDOWS ? "notepad.exe" : "phantomjs";
        Process phantom = Runtime.getRuntime().exec(new String[] { app });
        ProcessShutdownHandler shutdownHandler = new ProcessShutdownHandler();
        shutdownHandler.shutdownProcesses();
        phantom.waitFor();
        // check no phantom child existed (by kill)
        assertTrue(phantom.exitValue() > 0);
        phantom.destroy();
    }
}