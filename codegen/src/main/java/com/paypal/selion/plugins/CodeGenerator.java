/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * This class houses the core logic which is responsible for transforming a yaml file that houses locators into
 * corresponding .java files. It leverages Velocity templates under the hoods to get this done.
 * 
 */
public class CodeGenerator {

    private final String baseDirectory;

    public CodeGenerator(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * 
     * This method will generate .java file based on the a yaml file contents.
     * 
     * @param dataFile
     *            - Path to where the yaml file resides.
     * @param filePath
     *            - the relative file path where the generated .java file needs to be stored.
     * @param packageName
     *            - package name
     * 
     * @throws IOException
     */
    public void generateNewCode(File dataFile, String filePath, String packageName, String domain) throws IOException,
            CodeGeneratorException {
        BufferedReader br = null;
        String newFilePath = baseDirectory + filePath;
        File newFile = new File(newFilePath);
        newFile.mkdirs();

        DataReader dataReader = new DataReader(dataFile.getAbsolutePath());
        try {
            List<String> keys = dataReader.getKeys();

            TestPlatform currentPlatform = dataReader.platform();

            String baseClass = dataReader.getBaseClassName();

            String baseClassName = baseClass.substring(baseClass.lastIndexOf(".") + 1);

            // Validating the keys in data file before proceeding with the code generation
            GUIObjectDetails.validateKeysInDataFile(keys, dataFile.getName(), currentPlatform);

            List<GUIObjectDetails> htmlObjectDetailsList = GUIObjectDetails.transformKeys(keys, currentPlatform);

            Set<String> set = new HashSet<String>();
            for (GUIObjectDetails htmlObjectDetails : htmlObjectDetailsList) {
                set.add(htmlObjectDetails.getMemberPackage() + "." + htmlObjectDetails.getMemberType());
                if (htmlObjectDetails.getMemberType().equals(Container.class.getSimpleName())) {
                    // Adding ParentTraits to the list to avoid using hardcoded import statements in .vm file
                    set.add("com.paypal.selion.platform.html.ParentTraits");
                }
            }
            
            Velocity.init();

            Velocity.setProperty("resource.loader", "class");
            Velocity.setProperty("class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            String fileName = dataFile.getName();
            String className = fileName.substring(0, fileName.indexOf("."));

            VelocityContext context = new VelocityContext();

            context.put("class", className);
            context.put("members", htmlObjectDetailsList);
            context.put("control", set);
            context.put("package", packageName);
            context.put("baseclasspackage", baseClass);
            context.put("domain", domain);

            context.put("baseclass", baseClassName);

            String resourceToLoad = currentPlatform.getVelocityTemplateToUse();
            InputStream is = getClass().getResourceAsStream("/" + resourceToLoad);
            br = new BufferedReader(new InputStreamReader(is));

            BufferedWriter writer = new BufferedWriter(new FileWriter(newFilePath + "/" + className + ".java"));
            Velocity.evaluate(context, writer, "code generator", br);
            writer.flush();
            writer.close();
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }
}
