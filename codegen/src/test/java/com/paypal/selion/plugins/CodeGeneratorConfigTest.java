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
import com.beust.jcommander.JCommander;
import com.paypal.selion.elements.SeLionElement;
import org.testng.annotations.Test;

import java.util.Arrays;

public class CodeGeneratorConfigTest {
    @Test
    public void testConfigDefaults()  {
        CodeGeneratorConfig config = new CodeGeneratorConfig();

        assertEquals(config.baseFolder, "GUIData");
        assertEquals(config.basePackage, "com.paypal.selion.testcomponents");
        assertEquals(config.resourcesDir, "src/test/resources");
        assertEquals(config.sourceDir, "src/test/java");
        assertEquals(config.workingDir, "./");
        assertEquals(config.detailedTextOutputLocation, "");
        assertEquals(config.generatedSourcesDir, "target/generated-test-sources");
        assertFalse(config.help);
        assertEquals(config.excludeDomains.size(), 0);
        assertEquals(config.androidCustomElements.size(), 0);
        assertEquals(config.iosCustomElements.size(), 0);
        assertEquals(config.htmlCustomElements.size(), 0);
        assertEquals(config.mobileCustomElements.size(), 0);
    }

    @Test
    public void testConfigParsed() {
        String[] args = {
            "-baseFolder", "Foo",
            "-basePackage", "fooPackage",
            "-resourcesDir", "fooResourcesDir",
            "-sourceDir", "fooSourceDir",
            "-workingDir", "fooWorkingDir",
            "-generatedSourcesDir", "fooGeneratedSourcesDir",
            "-excludeDomains", "foo,bar",
            "-androidCustomElements", "foo.android.A,foo.android.B",
            "-iosCustomElements", "foo.ios.A,foo.ios.B",
            "-htmlCustomElements", "foo.html.A,foo.html.B",
            "-mobileCustomElements", "foo.mobile.A,foo.mobile.B"
        };

        CodeGeneratorConfig config = new CodeGeneratorConfig();
        new JCommander(config).parse(args);

        assertEquals(config.baseFolder, "Foo");
        assertEquals(config.basePackage, "fooPackage");
        assertEquals(config.resourcesDir, "fooResourcesDir");
        assertEquals(config.sourceDir, "fooSourceDir");
        assertEquals(config.workingDir, "fooWorkingDir");
        assertEquals(config.generatedSourcesDir, "fooGeneratedSourcesDir");
        assertEquals(config.excludeDomains.size(), 2);
        assertTrue(config.excludeDomains.containsAll(Arrays.asList("foo", "bar")));
        assertEquals(config.androidCustomElements.size(), 2);
        assertTrue(config.androidCustomElements.containsAll(
            Arrays.asList(
                new SeLionElement("foo.android.A"),
                new SeLionElement("foo.android.B")
            )));
        assertEquals(config.iosCustomElements.size(), 2);
        assertTrue(config.iosCustomElements.containsAll(
            Arrays.asList(
                new SeLionElement("foo.ios.A"),
                new SeLionElement("foo.ios.B")
            )));
        assertEquals(config.htmlCustomElements.size(), 2);
        assertTrue(config.htmlCustomElements.containsAll(
            Arrays.asList(
                new SeLionElement("foo.html.A"),
                new SeLionElement("foo.html.B")
            )));
        assertEquals(config.mobileCustomElements.size(), 2);
        assertTrue(config.mobileCustomElements.containsAll(
            Arrays.asList(
                new SeLionElement("foo.mobile.A"),
                new SeLionElement("foo.mobile.B")
            )));
    }
}
