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

import static org.testng.Assert.assertEquals;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PlatformTest {

    class DummyMojo extends AbstractMojo {
        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {
        }
    }

    @BeforeClass
    public void before() {
        Logger.setLogger(new DummyMojo().getLog());
    }

    @Test
    public void testWebPlatform() throws Exception {
        // When no platform is specified the default should be WEB
        TestPlatform currentPlatform = getPlatformToTest("src/test/resources/PayPalAbstractPage.yml");
        assertEquals(currentPlatform, TestPlatform.WEB);
    }

    @Test
    public void testIOSPlatform() throws Exception {
        // For IOS platform, the value must be specified
        TestPlatform currentPlatform = getPlatformToTest("src/test/resources/IOSInteractionPage.yaml");
        assertEquals(currentPlatform, TestPlatform.IOS);
    }

    @Test(expectedExceptions = { CodeGeneratorException.class })
    public void testInvalidPlatform() throws Exception {
        // Testing the datareader for a negative condition with a invalid platform name
        getPlatformToTest("src/test/resources/InvalidBasePage.yaml");
    }

    private TestPlatform getPlatformToTest(String resourceFile) throws Exception {
        DataReader reader = new DataReader(resourceFile);
        return reader.platform();
    }

}
