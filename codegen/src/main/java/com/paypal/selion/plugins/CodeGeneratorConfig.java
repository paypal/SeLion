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

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.paypal.selion.elements.SeLionElement;

import java.util.Collections;
import java.util.List;

public class CodeGeneratorConfig {
    /**
     * Represents the base package used for generated java classes.
     */
    @Parameter(names = "-basePackage",
        description = "Represents the base package used for generated java classes.")
    public String basePackage = "com.paypal.selion.testcomponents";

    /**
     * Represents the base folder used for reading page asset files such as PageYAML.
     */
    @Parameter(names = "-baseFolder",
        description = "Represents the base folder used for reading page asset files such as PageYAML.")
    public String baseFolder = "GUIData";

    /**
     * List of "domains" to exclude during code generation.
     */
    @Parameter(names = "-excludeDomains",
        description = "Comma separated list of \"domains\" to exclude during code generation. For example: foo,bar,baz")
    public List<String> excludeDomains = Collections.emptyList();

    /**
     * List of "html" custom elements to be included during code generation.
     */
    @Parameter(names = "-htmlCustomElements", converter = SeLionElementConverter.class,
        description = "Comma separated list of \"html\" custom elements to be included during code generation. For example: bar.foo.A,bar.foo.B")
    public List<SeLionElement> htmlCustomElements = Collections.emptyList();

    /**
     * List of "mobile" custom elements to be included during code generation.
     */
    @Parameter(names = "-mobileCustomElements", converter = SeLionElementConverter.class,
        description = "Comma separated list of \"mobile\" custom elements to be included during code generation. For example: bar.foo.A,bar.foo.B")
    public List<SeLionElement> mobileCustomElements = Collections.emptyList();

    /**
     * List of "ios" custom elements to be included during code generation.
     */
    @Parameter(names = "-iosCustomElements", converter = SeLionElementConverter.class,
        description = "Comma separated list of \"ios\" custom elements to be included during code generation. For example: bar.foo.A,bar.foo.B")
    public List<SeLionElement> iosCustomElements = Collections.emptyList();

    /**
     * List of "android" custom elements to be included during code generation.
     */
    @Parameter(names = "-androidCustomElements", converter = SeLionElementConverter.class,
        description = "Comma separated list of \"android\" custom elements to be included during code generation. For example: bar.foo.A,bar.foo.B")
    public List<SeLionElement> androidCustomElements = Collections.emptyList();

    /**
     * The source directory for classes which might depend on generated code (E.g. {PageObject}Ext classes)
     */
    @Parameter(names = "-sourceDir",
        description = "The source directory for classes which might depend on generated code (E.g. {PageObject}Ext classes)")
    public String sourceDir = "src/test/java";

    /**
     * The generated source directory to output to
     */
    @Parameter(names = "-generatedSourcesDir",
        description = "The generated source directory to output to.")
    public String generatedSourcesDir = "target/generated-test-sources";

    /**
     * The resources directory which contains the {@link #baseFolder}
     */
    @Parameter(names = "-resourcesDir",
        description = "The resources directory which contains the -baseFolder.")
    public String resourcesDir = "src/test/resources";

    /**
     * The working directory
     */
    @Parameter(names = "-workingDir",
        description = "The working directory.")
    public String workingDir = "./";

    @Parameter(names = { "-help", "-h" }, help = true, hidden = true)
    public boolean help;

    /**
     * Not a @Parameter for Codegen CLI.
     *
     * Used to specify the location for the code generator to create a SeLionPageDetails.txt file which contains the file
     * path of every resource file processed.
     */
    public String detailedTextOutputLocation = "";

    public static class SeLionElementConverter implements IStringConverter<SeLionElement> {
        @Override
        public SeLionElement convert(String value) {
            return new SeLionElement(value);
        }
    }
}
