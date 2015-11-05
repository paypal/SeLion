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

package com.paypal.selion.internal.platform.pageyaml;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.internal.platform.pageyaml.YamlReaderFactory;

public class PageDataProviderTest {
    public Map<String, String> myYamlMap;
    public Map<String, String> myLocalizedYamlMap;
    public Map<String, String> myYamlV2Map;
    public Map<String, String> myLocalizedYamlV2Map;
    public Map<String, String> myYamlContainerMap;
    public Map<String, String> myLocalizedYamlContainerMap;
    public Map<String, String> myYamlV2ContainerMap;
    public Map<String, String> myLocalizedYamlV2ContainerMap;

    @BeforeMethod(groups = { "unit" })
    public void setUp() throws IOException {
        
        myYamlMap = YamlReaderFactory.createInstance("PayPalProfilePage.yaml").getGuiMap("US");
        myLocalizedYamlMap = YamlReaderFactory.createInstance("PayPalProfilePage.yaml").getGuiMap("FR");
        myYamlV2Map = YamlReaderFactory.createInstance("SampleV2YamlPage.yaml").getGuiMap("US");
        myLocalizedYamlV2Map = YamlReaderFactory.createInstance("SampleV2YamlPage.yaml").getGuiMap("FR");

        myYamlContainerMap = YamlReaderFactory.createInstance("PayPalProfilePage.yaml").getGuiMapForContainer(
                "myContainer", "US");
        myLocalizedYamlContainerMap = YamlReaderFactory.createInstance("PayPalProfilePage.yaml")
                .getGuiMapForContainer("myContainer", "FR");
        myYamlV2ContainerMap = YamlReaderFactory.createInstance("SampleV2YamlPage.yaml").getGuiMapForContainer(
                "myContainer", "US");
        myLocalizedYamlV2ContainerMap = YamlReaderFactory.createInstance("SampleV2YamlPage.yaml")
                .getGuiMapForContainer("myContainer", "FR");
    }

    @Test(groups = { "unit" })
    public void testLoadGuiMap() {
        assertNotNull(myYamlMap);
        assertNotNull(myLocalizedYamlMap);
        assertNotNull(myYamlV2Map);
        assertNotNull(myLocalizedYamlV2Map);
    }

    @Test(dependsOnMethods = { "testLoadGuiMap" })
    public void testYamlGetValues() {
        String value = myYamlMap.get("BankAccountLink");
        assertEquals(value, "link=Bank Accounts");

        value = myYamlMap.get("myContainer");
        assertEquals(value, "//usContainer");

        value = myYamlContainerMap.get("containerLink");
        assertEquals(value, ".//usContainerLink");
    }

    @Test(dependsOnMethods = { "testLoadGuiMap" })
    public void testYamlLocalizedGetValues() {
        String value = myLocalizedYamlMap.get("BankAccountLink");
        assertEquals(value, "link=French Bank Accounts");

        value = myLocalizedYamlMap.get("myContainer");
        assertEquals(value, "//frContainer");

        value = myLocalizedYamlContainerMap.get("containerLink");
        assertEquals(value, ".//frContainerLink");
    }

    @Test(dependsOnMethods = { "testLoadGuiMap" })
    public void testYamlv2GetValues() {
        String value = myYamlV2Map.get("viewAPICertificateLink");
        assertEquals(value, "//div[@id='apiOption2']/p[3]/a");

        value = myYamlV2Map.get("myContainer");
        assertEquals(value, "//usContainer");

        value = myYamlV2ContainerMap.get("containerLink");
        assertEquals(value, ".//usContainerLink");
    }

    @Test(dependsOnMethods = { "testLoadGuiMap" })
    public void testYamlv2LocalizedGetValues() {
        String value = myLocalizedYamlV2Map.get("viewAPICertificateLink");
        assertEquals(value, "//div[@id='apiOption2']/p[3]/a");

        value = myLocalizedYamlV2Map.get("addOrEditAPIPermissionsLink");
        assertEquals(value, "//frLocator");

        value = myLocalizedYamlV2Map.get("myContainer");
        assertEquals(value, "//frContainer");

        value = myLocalizedYamlV2ContainerMap.get("containerLink");
        assertEquals(value, ".//frContainerLink");
    }

}
