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
import com.paypal.selion.elements.*;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

public class CodeGeneratorHelperTest {
    @BeforeClass
    public void before() {
        CodeGeneratorLoggerFactory.setLogger(new CodeGeneratorSimpleLogger());
    }


    @Test
    public void testRegisterCustomElements() {
        CodeGeneratorConfig config = new CodeGeneratorConfig();
        String[] args = {
            "-androidCustomElements", "foo.android.A,foo.android.B",
            "-iosCustomElements", "foo.ios.A,foo.ios.B",
            "-htmlCustomElements", "foo.html.A,foo.html.B",
            "-mobileCustomElements", "foo.mobile.A,foo.mobile.B"
        };
        new JCommander(config).parse(args);

        CodeGeneratorHelper helper = new CodeGeneratorHelper(config);
        helper.registerCustomElements();

        assertTrue(AndroidSeLionElementSet.getInstance().containsAll(
            Arrays.asList(
                new SeLionElement("foo.android.A"),
                new SeLionElement("foo.android.B")
            )));
        assertTrue(IOSSeLionElementSet.getInstance().containsAll(
            Arrays.asList(
                new SeLionElement("foo.ios.A"),
                new SeLionElement("foo.ios.B")
            )));
        assertTrue(HtmlSeLionElementSet.getInstance().containsAll(
            Arrays.asList(
                new SeLionElement("foo.html.A"),
                new SeLionElement("foo.html.B")
            )));
        assertTrue(MobileSeLionElementSet.getInstance().containsAll(
            Arrays.asList(
                new SeLionElement("foo.mobile.A"),
                new SeLionElement("foo.mobile.B")
            )));
    }

    @Test
    public void testCreateSeLionPageDetailsFile() {
        CodeGeneratorConfig config = new CodeGeneratorConfig();
        config.detailedTextOutputLocation = System.getProperty("java.io.tmpdir", "/tmp");
        CodeGeneratorHelper helper = new CodeGeneratorHelper(config);
        helper.createSeLionPageDetailsFile();

        String outputDir = new File(config.detailedTextOutputLocation).getAbsolutePath();
        File outputFile = new File(outputDir + File.separator + "SeLionPageDetails.txt");

        assertTrue(outputFile.exists());
        outputFile.delete();
        assertFalse(outputFile.exists());
    }

    @Test
    public void testProcessFiles() throws Exception {
        CodeGeneratorConfig config = new CodeGeneratorConfig();
        // generate a helper, using the default files
        CodeGeneratorHelper helper = new CodeGeneratorHelper(config);
        helper.processFiles();

        File generatedDir = new File(config.generatedSourcesDir);
        assertTrue(generatedDir.exists());

        File packageOutputDir = new File(generatedDir.getAbsolutePath() + File.separator
            + config.basePackage.replace(".", File.separator));

        String[] generatedFiles = {
            "sample/MyAppHomePage.java",
            "sample/NativeAppTestPage.java"
        };

        for (String file : generatedFiles) {
            File f = new File(packageOutputDir.getAbsolutePath() + File.separator + file);
            assertTrue(f.exists());
            f.delete();
        }

        FileUtils.forceDelete(generatedDir);
    }

    @Test
    public void testConstructor() {
        CodeGeneratorConfig config = new CodeGeneratorConfig();
        String[] args = {
            "-baseFolder", "Foo"
        };
        new JCommander(config).parse(args);

        CodeGeneratorHelper helper = new CodeGeneratorHelper(config);
        assertNotNull(helper.config);
        assertSame(helper.config, config);
    }
}
