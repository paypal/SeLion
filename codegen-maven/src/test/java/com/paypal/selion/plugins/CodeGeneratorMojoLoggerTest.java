/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2017 PayPal                                                                                          |
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

import static org.testng.Assert.*;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.testng.annotations.Test;

public class CodeGeneratorMojoLoggerTest {
    @Test
    public void testDefaultConstructor() {
        CodeGeneratorMojoLogger logger = new CodeGeneratorMojoLogger();
        assertTrue(logger.getInstance() instanceof SystemStreamLog);
    }

    @Test
    public void testConstructorWithLog() {
        CodeGeneratorMojoLogger logger = new CodeGeneratorMojoLogger(new DefaultLog(new ConsoleLogger()));
        assertTrue(logger.getInstance() instanceof DefaultLog);
    }

    @Test
    public void testConformsWithCodeGeneratorLoggerType() {
        CodeGeneratorMojoLogger logger = new CodeGeneratorMojoLogger();
        CodeGeneratorLoggerFactory.setLogger(logger);
        assertSame(CodeGeneratorLoggerFactory.getLogger(), logger);
        assertTrue(CodeGeneratorLoggerFactory.getLogger() instanceof CodeGeneratorLogger);
    }
}
