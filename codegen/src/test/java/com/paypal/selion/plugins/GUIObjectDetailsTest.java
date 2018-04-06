/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.elements.HtmlSeLionElementSet;
import com.paypal.selion.elements.IOSSeLionElementSet;

import static org.testng.Assert.assertTrue;

public class GUIObjectDetailsTest {

    @BeforeClass
    public void before() {
        CodeGeneratorLoggerFactory.setLogger(new CodeGeneratorSimpleLogger());
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

        HtmlSeLionElementSet.getInstance().add("com.paypal.test.CustomElement");
        HtmlSeLionElementSet.getInstance().add("com.paypal.test.AnotherElement");

        DataReader reader = new DataReader(dataFile);
        TestPlatform currentPlatform = reader.platform();
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, currentPlatform);
    }

    @Test
    public void validateCustomMobileElements() throws Exception {
        String dataFile = "src/test/resources/CustomIOSElementPage.yaml";

        IOSSeLionElementSet.getInstance().add("com.paypal.test.CustomElement");
        IOSSeLionElementSet.getInstance().add("com.paypal.test.AnotherElement");

        DataReader reader = new DataReader(dataFile);
        TestPlatform currentPlatform = reader.platform();
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, currentPlatform);
    }

    @Test
    public void validateCustomElementInContainer() throws Exception {
        String dataFile = "src/test/resources/CustomElementInContainerPage.yaml";
        HtmlSeLionElementSet.getInstance().add("com.paypal.test.CustomElement");

        DataReader reader = new DataReader(dataFile);
        TestPlatform currentPlatform = reader.platform();
        GUIObjectDetails.validateKeysInDataFile(reader.getKeys(), dataFile, currentPlatform);

        String expectedContainerName = "foo" + "Container";
        String expectedCustomName = expectedContainerName + "#exampleCustomElement";

        assertTrue(reader.getKeys().contains(expectedContainerName));
        assertTrue(reader.getKeys().contains(expectedCustomName));

        // get the GUI customElement and verify parent is the container
        GUIObjectDetails customGuiElement = GUIObjectDetails.transformKeys(reader.getKeys(), currentPlatform).get(1);
        assertTrue(customGuiElement.getParent().contains(expectedContainerName));
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
