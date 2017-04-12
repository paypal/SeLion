/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016-2017 PayPal                                                                                     |
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

import com.paypal.selion.elements.SeLionElement;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for code generation goals
 */
abstract class AbstractBaseCodeGeneratorMojo extends AbstractMojo {
    /**
     * Project the plugin is called from.
     */
    @Parameter(property = "project", defaultValue = "${project}", required = true)
    protected MavenProject project;

    /**
     * Represents the base package used for generated java classes.
     */
    @Parameter(property = "basePackage", defaultValue = "com.paypal.selion.testcomponents", required = true)
    protected String basePackage;

    /**
     * Represents the base folder used for reading page asset files such as PageYAML.
     */
    @Parameter(property = "baseFolder", defaultValue = "GUIData", required = true)
    protected String baseFolder;

    /**
     * List of "domains" to exclude during code generation.
     */
    @Parameter(property = "excludeDomains")
    protected List<String> excludeDomains;

    /**
     * List of "html" custom elements to be included during code generation.
     */
    @Parameter(property = "htmlCustomElements")
    protected List<String> htmlCustomElements;

    /**
     * List of "mobile" custom elements to be included during code generation.
     */
    @Parameter(property = "mobileCustomElements")
    protected List<String> mobileCustomElements;

    /**
     * List of "ios" custom elements to be included during code generation.
     */
    @Parameter(property = "iosCustomElements")
    protected List<String> iosCustomElements;

    /**
     * List of "android" custom elements to be included during code generation.
     */
    @Parameter(property = "androidCustomElements")
    protected List<String> androidCustomElements;

    /**
     * Represents the location for the code generator plug-in to create a SeLionPageDetails.txt file. This file will
     * contain the file path of every resource file processed.
     */
    @Parameter(property = "detailedTextOutputLocation", defaultValue = "${project.build.directory}", required = true)
    protected File detailedTextOutputLocation;

    /**
     * Directory to write generated code to.
     */
    @Parameter(property = "outputDirectory")
    protected File outputDirectory;

    /**
     * @return the source directory for classes which might depend on generated code (E.g. {PageObject}Ext classes)
     */
    abstract String sourceDir();

    /**
     * @return the generated source directory to output to
     */
    abstract String generatedSourcesDir();

    /**
     * @return the resources directory which contains the {@link #baseFolder}
     */
    abstract String resourcesDir();

    @Override
    public CodeGeneratorMojoLogger getLog() {
        final CodeGeneratorMojoLogger log = new CodeGeneratorMojoLogger(super.getLog());
        return log;
    }

    private List<SeLionElement> toSeLionElementList(List<String> elements) {
        List<SeLionElement> newList = Collections.emptyList();
        for (String element : elements) {
            newList.add(new SeLionElement(element));
        }
        return newList;
    }

    private CodeGeneratorConfig toCodeGeneratorConfig() {
        final CodeGeneratorConfig config = new CodeGeneratorConfig();

        config.baseFolder = baseFolder;
        config.basePackage = basePackage;
        config.excludeDomains = (excludeDomains == null) ? config.excludeDomains : excludeDomains;
        config.workingDir = project.getBasedir().getAbsolutePath();
        config.resourcesDir = resourcesDir();
        config.sourceDir = sourceDir();
        config.generatedSourcesDir = generatedSourcesDir();
        config.androidCustomElements = (androidCustomElements == null) ?
            config.androidCustomElements : toSeLionElementList(androidCustomElements);
        config.iosCustomElements = (iosCustomElements == null) ?
            config.iosCustomElements : toSeLionElementList(iosCustomElements);
        config.htmlCustomElements = (htmlCustomElements == null) ?
            config.htmlCustomElements : toSeLionElementList(htmlCustomElements);
        config.mobileCustomElements = (mobileCustomElements == null) ?
            config.mobileCustomElements : toSeLionElementList(mobileCustomElements);
        config.detailedTextOutputLocation = detailedTextOutputLocation.getAbsolutePath();

        return config;
    }

    public void execute() throws MojoExecutionException {
        // make sure the logger is initialized
        CodeGeneratorLoggerFactory.setLogger(getLog());

        // iff baseFolder is null or empty, reset it to the default value
        baseFolder = StringUtils.defaultIfEmpty(baseFolder, "GUIData");

        // create the helper with our config
        CodeGeneratorHelper helper = new CodeGeneratorHelper(toCodeGeneratorConfig());

        if (CodeGeneratorLoggerFactory.getLogger().isDebugEnabled()) {
            helper.displayProjectInformation();
        }

        // register all custom element classes
        helper.registerCustomElements();

        // create the generated page details file
        helper.createSeLionPageDetailsFile();

        // process the files.
        helper.processFiles();
    }
}
