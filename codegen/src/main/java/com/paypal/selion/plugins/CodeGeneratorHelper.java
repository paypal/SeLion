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

import com.google.common.annotations.VisibleForTesting;
import com.paypal.selion.elements.AndroidSeLionElementSet;
import com.paypal.selion.elements.HtmlSeLionElementSet;
import com.paypal.selion.elements.IOSSeLionElementSet;
import com.paypal.selion.elements.MobileSeLionElementSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeGeneratorHelper {
    @VisibleForTesting
    CodeGeneratorConfig config = new CodeGeneratorConfig();
    private static CodeGeneratorLogger logger = CodeGeneratorLoggerFactory.getLogger();
    private final List<File> allDataFiles;

    protected CodeGeneratorHelper(CodeGeneratorConfig config) {
        this.config = config;

        final String resourceDir = config.resourcesDir + File.separator + config.baseFolder;
        this.allDataFiles = loadFiles(new File(resourceDir));
    }

    public void processFiles() {
        final CodeGenerator generator = new CodeGenerator(config.generatedSourcesDir);

        for (File dataFile : allDataFiles) {
            try {
                String folder = pathToFolder(dataFile);
                String domain = folder.replace(File.separator, "/");
                folder = folder.replace(File.separator, ".");

                if (config.excludeDomains.contains(domain)) {
                    logger.info("Excluded code generation for YAML file [" + dataFile.getName()
                        + "] as the corresponding domain [" + domain + "] is available in excludeDomains list");
                    continue;
                }

                String relativePath = relativePath(folder);

                File extendedFile = extendedFileLoc(config.sourceDir, relativePath, dataFile);
                File baseFile = baseFileLoc(config.generatedSourcesDir, relativePath, dataFile);
                if (generateJavaCode(baseFile, dataFile, extendedFile)) {
                    logger.info("Generating java file for YAML file [" + dataFile.getName() + "] in domain ["
                        + domain + "]");

                    String tempPackage = config.basePackage;
                    // add the folder, iff it contains a value
                    if (!folder.isEmpty()) {
                        tempPackage += tempPackage.isEmpty() ? folder : "." + folder;
                    }

                    generator.generateNewCode(dataFile, relativePath, tempPackage, domain);
                }

            } catch (Exception e) {
                String errorMsg = " \n SeLion code generator failed when generating code for "
                    + dataFile.getAbsolutePath()
                    + "\n" + "Root Cause : \n" + e.toString();
                throw new CodeGeneratorException(errorMsg, e);
            }
        }
    }

    public void displayProjectInformation() {
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.debug("base directory of project: " + new File(config.workingDir).getAbsolutePath());
        logger.debug("sources directory: " + config.sourceDir);
        logger.debug("generated sources directory: " + config.generatedSourcesDir);
        logger.debug("resources directory: " + config.resourcesDir);
        logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    public void registerCustomElements() {
        HtmlSeLionElementSet.getInstance().addAll(config.htmlCustomElements);
        IOSSeLionElementSet.getInstance().addAll(config.iosCustomElements);
        AndroidSeLionElementSet.getInstance().addAll(config.androidCustomElements);
        MobileSeLionElementSet.getInstance().addAll(config.mobileCustomElements);
    }

    public void createSeLionPageDetailsFile() {
        String outputDir = new File(config.detailedTextOutputLocation).getAbsolutePath();
        File outputFile = new File(outputDir + File.separator + "SeLionPageDetails.txt");
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (IOException e) {
            CodeGeneratorLoggerFactory.getLogger().debug(e);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String prefix = new File(config.resourcesDir).getAbsolutePath();
            for (File eachFile : allDataFiles) {
                String filePath = eachFile.getAbsolutePath();
                // Convert the absolute file path to a relative file path which doesn't include the base
                // location.
                writer.write(filePath.substring(prefix.length() + 1));
                writer.newLine();
            }
        } catch (IOException e) {
            CodeGeneratorLoggerFactory.getLogger().debug(e);
        }
    }

    private String pathToFolder(File file) {
        String path = file.getAbsolutePath();
        try {
            return path.substring(path.indexOf(config.baseFolder) + config.baseFolder.length() + 1, path.lastIndexOf(File.separator))
                .trim();
        } catch (StringIndexOutOfBoundsException ex) {
            return "";
        }
    }

    private String relativePath(String folder) {
        String[] dirs = (config.basePackage + "." + folder).split("\\Q.\\E");
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
