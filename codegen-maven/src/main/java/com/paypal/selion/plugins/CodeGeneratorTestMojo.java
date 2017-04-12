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

import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.List;

/**
 * Goal used to generate SeLion Page Object code from PageYAML. Expects {PageObject}Ext.java classes, and PageYAML files
 * to be in src/test/.
 */
@Mojo(name = "generate-test", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class CodeGeneratorTestMojo extends AbstractBaseCodeGeneratorMojo {

    String sourceDir() {
        return project.getBuild().getTestSourceDirectory();
    }

    String generatedSourcesDir() {
        return (outputDirectory != null) ? outputDirectory.getAbsolutePath() :
                project.getBuild().getDirectory() + File.separator + "generated-test-sources";
    }

    String resourcesDir() {
        List<Resource> res = project.getBuild().getTestResources();
        if (res != null && res.size() >= 1) {
            return res.get(0).getDirectory();
        }
        return "";
    }
}
