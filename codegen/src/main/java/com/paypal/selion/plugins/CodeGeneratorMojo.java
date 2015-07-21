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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.paypal.selion.elements.AndroidSeLionElementList;
import com.paypal.selion.elements.HtmlElementUtils;
import com.paypal.selion.elements.HtmlSeLionElementList;
import com.paypal.selion.elements.IOSSeLionElementList;

/**
 * Goal used to generate SeLion Page Object code from the data file.
 * 
 * @goal generate
 * 
 * @phase generate-sources
 */
public class CodeGeneratorMojo extends AbstractMojo {
    /**
     * Project the plugin is called from.
     * 
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * Represents the base package used for generated java classes.
     * 
     * @parameter expression="${selion-code-generator.basePackage}" default-value="com.paypal.selion.testcomponents"
     */
    private String basePackage;

    /**
     * Represents the base folder used for reading page asset files such as PageYaml.
     * 
     * @parameter expression="${selion-code-generator.baseFolder}" default-value="GUIData"
     */
    private String baseFolder;

    /**
     * List of "domains" to exclude during code generation.
     * 
     * @parameter expression="${selion-code-generator.excludeDomains}"
     */
    private List<String> excludeDomains;
    
    /**
     * List of "html" custom elements to be included during code generation. 
     * 
     * @parameter expression="${selion-code-generator.htmlCustomElements}"
     */
    private List<String> htmlCustomElements;
    
    /**
     * List of "ios" custom elements to be included during code generation. 
     * 
     * @parameter expression="${selion-code-generator.iosCustomElements}"
     */
    private List<String> iosCustomElements;
    
    /**
     * List of "android" custom elements to be included during code generation. 
     * 
     * @parameter expression="${selion-code-generator.androidCustomElements}"
     */
    private List<String> androidCustomElements;

    /**
     * Represents the location for the code generator plug-in to create a <code>SeLionPageDetails.txt</code> text file. \
     * This text file will contain the file path of every resource file processed.
     * 
     * @parameter expression="${selion-code-generator.detailedTextOutputLocation}" default-value="${project.build.directory}";
     */
    private File detailedTextOutputLocation;
    
    public void setCustomElements(String[] elements) {
        htmlCustomElements = Arrays.asList(elements);
    }
    
    public void setIosCustomElements(String[] elements) {
        iosCustomElements = Arrays.asList(elements);
    }
    
    public void setAndroidCustomElements(String[] elements) {
        androidCustomElements = Arrays.asList(elements);
    }
    
    
    public void setExcludeDomains(String[] excludes) {
        excludeDomains = Arrays.asList(excludes);
    }

    public void setDetailedTextOutputLocation(File location) {
        detailedTextOutputLocation = location;
    }

    public void displayProjectInformation() {
        Log logger = Logger.getLogger();
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.debug("base directory of project " + project.getBasedir().getAbsolutePath());
        logger.debug("src/main/java path : " + sourceDir());
        logger.debug("generated sources path " + generatedSourcesDir());
        logger.debug("src/main/resources path " + resourcesDir());
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    private String sourceDir() {
        return project.getBuild().getSourceDirectory();
    }

    private String generatedSourcesDir() {
        return project.getBuild().getDirectory() + File.separator + "generated-sources";
    }

    private String resourcesDir() {
        List<Resource> res = project.getBuild().getResources();
        if (res != null && res.size() >= 1) {
            return res.get(0).getDirectory();
        }
        return "";
    }

    private String pathToFolder(File file) {
        String path = file.getAbsolutePath();
        try {
            return path.substring(path.indexOf(baseFolder) + baseFolder.length() + 1, path.lastIndexOf(File.separator))
                .trim();
        } catch(StringIndexOutOfBoundsException ex) {
            return "";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
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
        
        for(String htmlElement : htmlCustomElements) {
            String elementName = HtmlElementUtils.getClass(htmlElement);
            if(HtmlSeLionElementList.isExactMatch(elementName)) {
                logger.info("The custom " + elementName + " that will be registered as a valid element is overwriting an existing SeLion element.");
            } else {
                logger.info("The custom " + elementName + " will be registered as a valid element.");
            }
            
            HtmlSeLionElementList.registerElement(htmlElement);
        }
        
        for(String iosElement : iosCustomElements) {
            String elementName = HtmlElementUtils.getClass(iosElement);
            if(IOSSeLionElementList.isExactMatch(elementName)) {
                logger.info("The custom " + elementName + " that will be registered as a valid element is overwriting an existing SeLion element.");
            } else {
                logger.info("The custom " + elementName + " will be registered as a valid element.");
            }
            
            IOSSeLionElementList.registerElement(iosElement);
        }
        
        for(String androidElement : androidCustomElements) {
            //There are no elements declared for android hence proceeding ahead to register the elements
            AndroidSeLionElementList.registerElement(androidElement);
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
                        tempPackage += tempPackage.isEmpty() ?  folder :  "." + folder;
                    }
                    
                    helper.generateNewCode(dataFile, relativePath, tempPackage, domain);
                }

            } catch (Exception e) {
                String errorMsg = " \n SeLion code generator failed when generating code for " + dataFile.getAbsolutePath()
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

    private String relativePath(String folder) {
        String[] dirs = new String(basePackage + "." + folder).split("\\Q.\\E");
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
        List<File> dataFile = new ArrayList<File>();
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
