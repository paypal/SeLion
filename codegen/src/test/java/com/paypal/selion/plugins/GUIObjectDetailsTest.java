/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.elements.HtmlSeLionElementList;
import com.paypal.selion.elements.IOSSeLionElementList;

public class GUIObjectDetailsTest {
    class DummyMojo extends AbstractMojo {
        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {
            // Nothing to execute
        }
    }

    @BeforeClass
    public void before() {
        Logger.setLogger(new DummyMojo().getLog());
    }

    @Test
    public void validateWebElements() throws Exception {
        String dataFile = "src/test/resources/SampleV2YamlPage.yaml";
        DataReader reader = new DataReader(dataFile);
        TestPlatform currentPlatform = reader.platform();
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, currentPlatform);
    }

    @Test
    public void validateIOSElements() throws Exception {
        String dataFile = "src/test/resources/IOSInteractionPage.yaml";
        DataReader reader = new DataReader(dataFile);
        TestPlatform currentPlatform = reader.platform();
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, currentPlatform);
    }
    
    @Test
    public void validateMobileElements() throws Exception {
        String dataFile = "src/test/resources/SampleMobilePage.yaml";
        DataReader reader = new DataReader(dataFile);
        TestPlatform currentPlatform = reader.platform();
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, currentPlatform);
    }

    @Test
    public void validateCustomWebElements() throws Exception {
        String dataFile = "src/test/resources/CustomWebElementPage.yaml";
        
        HtmlSeLionElementList.registerElement("com.paypal.test.CustomElement");
        HtmlSeLionElementList.registerElement("com.paypal.test.AnotherElement");
        
        DataReader reader = new DataReader(dataFile);
        TestPlatform currentPlatform = reader.platform();
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, currentPlatform);
    }
    
    @Test
    public void validateCustomMobileElements() throws Exception {
        String dataFile = "src/test/resources/CustomIOSElementPage.yaml";
        
        IOSSeLionElementList.registerElement("com.paypal.test.CustomElement");
        IOSSeLionElementList.registerElement("com.paypal.test.AnotherElement");
        
        DataReader reader = new DataReader(dataFile);
        TestPlatform currentPlatform = reader.platform();
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, currentPlatform);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testInvalidElement() throws Exception {
        String dataFile = "src/test/resources/SampleV2YamlPage.yaml";
        DataReader reader = new DataReader(dataFile);
        // Note: forcing to load a WEaB platform YAML using IOS platform
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, TestPlatform.IOS);

    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testInvalidSeLionElement() {
        List<String> invalidKeyList = new ArrayList<>();
        invalidKeyList.add("invalidButton1");
        GUIObjectDetails.validateKeysInDataFile(invalidKeyList, "DummyPage", TestPlatform.WEB);
    }

}
