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

package com.paypal.selion.logger;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.paypal.selion.logger.SeLionLogger.SeLionLoggerSettings;
import com.paypal.test.utilities.logging.SimpleLogger;

public class SeLionLoggerTest {

    @Test(groups = { "unit" })
    public void testSeLionLogger() {
        SimpleLogger logger = SeLionLogger.getLogger();
        assertTrue(logger != null, "Could not get SeLion logger.");
    }

    @Test(groups = { "unit" })
    public void testSeLionLoggerSetting() {
        SeLionLoggerSettings setting = new SeLionLoggerSettings();
        assertTrue(setting != null, "Could not get SeLion logger Settings.");
    }

}
