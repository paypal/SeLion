/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import com.paypal.selion.elements.*;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

    private void displayProjectInformation() {
        Log logger = Logger.getLogger();
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.debug("base directory of project: " + project.getBasedir().getAbsolutePath());
        logger.debug("sources directory: " + sourceDir());
        logger.debug("generated sources directory: " + generatedSourcesDir());
        logger.debug("resources directory: " + resourcesDir());
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    public void execute() throws MojoExecutionException {
        // iff baseFolder is null or empty, reset it to the default value
        baseFolder = StringUtils.defaultIfEmpty(baseFolder, "GUIData");

        Logger.setLogger(getLog());
        Log logger = Logger.getLogger();
        if (logger.isDebugEnabled()) {
            displayProjectInformation();
        }

        String sourceDir = sourceDir();
        String generatedSourceDir = generatedSourcesDir();
        String resourceDir = resourcesDir() + File.separator + baseFolder;

        List<File> allDataFiles = loadFiles(new File(resourceDir));

        createSeLionPageDetailsFile(allDataFiles);
        CodeGenerator helper = new CodeGenerator(generatedSourceDir);

        for (String htmlElement : htmlCustomElements) {
            String elementName = HtmlElementUtils.getClass(htmlElement);
            if (HtmlSeLionElementList.isExactMatch(elementName)) {
                logger.info("The custom " + elementName
                        + " that will be registered as a valid element is overwriting an existing SeLion element.");
            } else {
                logger.info("The custom " + elementName + " will be registered as a valid element.");
            }

            HtmlSeLionElementList.registerElement(htmlElement);
        }

        for (String iosElement : iosCustomElements) {
            String elementName = HtmlElementUtils.getClass(iosElement);
            if (IOSSeLionElementList.isExactMatch(elementName)) {
                logger.info("The custom " + elementName
                        + " that will be registered as a valid element is overwriting an existing SeLion element.");
            } else {
                logger.info("The custom " + elementName + " will be registered as a valid element.");
            }

            IOSSeLionElementList.registerElement(iosElement);
        }

        for (String androidElement : androidCustomElements) {
            String elementName = HtmlElementUtils.getClass(androidElement);
            if (AndroidSeLionElementList.isExactMatch(elementName)) {
                logger.info("The custom " + elementName
                        + " that will be registered as a valid element is overwriting an existing SeLion element.");
            } else {
                logger.info("The custom " + elementName + " will be registered as a valid element.");
            }

            AndroidSeLionElementList.registerElement(androidElement);
        }

        for(String mobileElement : mobileCustomElements) {
            String elementName = HtmlElementUtils.getClass(mobileElement);
            if(MobileSeLionElementList.isExactMatch(elementName)) {
                logger.info("The custom " + elementName
                        + " that will be registered as a valid element is overwriting an existing SeLion element.");
            } else {
                logger.info("The custom " + elementName + " will be registered as a valid element.");
            }

            MobileSeLionElementList.registerElement(mobileElement);
        }

        for (File dataFile : allDataFiles) {
            try {
                String folder = pathToFolder(dataFile);
                String domain = folder.replace(File.separator, "/");
                folder = folder.replace(File.separator, ".");

                if (excludeDomains.contains(domain)) {
                    logger.info("Excluded code generation for YAML file [" + dataFile.getName()
                            + "] as the corresponding domain [" + domain + "] is available in excludeDomains list");
                    continue;
                }

                String relativePath = relativePath(folder);

                File extendedFile = extendedFileLoc(sourceDir, relativePath, dataFile);
                File baseFile = baseFileLoc(generatedSourceDir, relativePath, dataFile);
                if (generateJavaCode(baseFile, dataFile, extendedFile)) {
                    logger.info("Generating java file for YAML file [" + dataFile.getName() + "] in domain ["
                            + domain + "]");

                    String tempPackage = basePackage;
                    // add the folder, iff it contains a value
                    if (!folder.isEmpty()) {
                        tempPackage += tempPackage.isEmpty() ? folder : "." + folder;
                    }

                    helper.generateNewCode(dataFile, relativePath, tempPackage, domain);
                }

            } catch (Exception e) {
                String errorMsg = " \n SeLion code generator failed when generating code for "
                        + dataFile.getAbsolutePath()
                        + "\n" + "Root Cause : \n" + e.toString();
                throw new MojoExecutionException(errorMsg, e);
            }
        }
    }

    private void createSeLionPageDetailsFile(List<File> allDataFiles) {
        String outputDir = detailedTextOutputLocation.getAbsolutePath();
        File outputFile = new File(outputDir + File.separator + "SeLionPageDetails.txt");
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (IOException e) {
            Logger.getLogger().debug(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String prefix = new File(resourcesDir()).getAbsolutePath();
            for (File eachFile : allDataFiles) {
                String filePath = eachFile.getAbsolutePath();
                // Convert the absolute file path to a relative file path which doesn't include the base
                // location.
                writer.write(filePath.substring(prefix.length() + 1));
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.getLogger().debug(e);
        }
    }

    private String pathToFolder(File file) {
        String path = file.getAbsolutePath();
        try {
            return path.substring(path.indexOf(baseFolder) + baseFolder.length() + 1, path.lastIndexOf(File.separator))
                    .trim();
        } catch (StringIndexOutOfBoundsException ex) {
            return "";
        }
    }

    private String relativePath(String folder) {
        String[] dirs = (basePackage + "." + folder).split("\\Q.\\E");
        String relativePath = "";
        for (String eachDir : dirs) {
            relativePath = relativePath + File.separator + eachDir;
        }
        return relativePath;
    }

    private File extendedFileLoc(String sourceDir, String relativePath, File dataFile) {
        String location = pathPrefix(sourceDir, relativePath, dataFile) + "Ext.java";
        return new File(location);
    }

    private String pathPrefix(String sourceDir, String relativePath, File dataFile) {
        return sourceDir + relativePath + File.separator
                + dataFile.getName().substring(0, dataFile.getName().indexOf("."));
    }

    private File baseFileLoc(String generatedSourceDir, String relativePath, File dataFile) {
        String location = pathPrefix(generatedSourceDir, relativePath, dataFile) + ".java";
        return new File(location);
    }

    // Java file for a page is generated only if the below conditions are met
    // 1. Java file does not exist
    // 2. Java file exists and last modified timestamp of the java file is greater than that of the yaml file
    // 3. Last modified timestamp of the extended java file is greater than that of the corresponding yaml file.
    private boolean generateJavaCode(File baseFile, File dataFile, File extendedFile) {
        return (baseFile.lastModified() < dataFile.lastModified() || (extendedFile.exists() && baseFile.lastModified() < extendedFile
                .lastModified()));
    }

    /**
     * This method will return all the data files available in the base directory and return List of {@link File}
     *
     * @param workingDir
     *            - Base directory
     * @return List<File>
     */
    private List<File> loadFiles(File workingDir) {
        List<File> dataFile = new ArrayList<>();
        if (workingDir.exists()) {
            File[] files = workingDir.listFiles();
            for (File eachFile : files) {
                if (eachFile.isDirectory()) {
                    dataFile.addAll(loadFiles(eachFile));
                } else if (eachFile.getName().endsWith(".yaml") || eachFile.getName().endsWith(".yml")) {
                    dataFile.add(eachFile);
                }

            }
        }
        return dataFile;
    }
}
