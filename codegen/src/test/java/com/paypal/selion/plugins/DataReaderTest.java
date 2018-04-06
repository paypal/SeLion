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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.elements.HtmlSeLionElementSet.HtmlSeLionElement;

public class DataReaderTest {
    private static final String YAML_V2 = "src/test/resources/SampleV2YamlPage.yaml";
    private static final String YAML_MOBILE = "src/test/resources/SampleMobilePage.yaml";

    @BeforeClass
    public void before() {
        CodeGeneratorLoggerFactory.setLogger(new CodeGeneratorSimpleLogger());
    }

    @Test
    public void getKeysV1() throws Exception {
        DataReader r = new DataReader("src/test/resources/PayPalAbstractPage.yml");
        List<String> keys = r.getKeys();
        assertTrue(keys.contains("messageBoxConfirmationLabel"));
    }

    @Test
    public void getKeysV2() throws Exception {
        DataReader r = new DataReader(YAML_V2);
        List<String> keys = r.getKeys();
        assertTrue(keys.contains("requestAPICredentialsLink"));
    }

    @Test
    public void getKeysMobile() throws Exception {
        DataReader r = new DataReader(YAML_MOBILE);
        List<String> keys = r.getKeys();
        assertTrue(keys.contains("sampleTextField"));
    }

    @Test
    public void getBaseClassV2() throws Exception {
        DataReader r = new DataReader(YAML_V2);
        String baseClass = r.getBaseClassName();
        assertEquals(baseClass, "com.paypal.selion.testcomponents.BasicPageImpl");
    }

    @Test
    public void getHtmlObjectDetails() throws Exception {
        DataReader r = new DataReader(YAML_V2);
        List<String> keys = r.getKeys();
        List<GUIObjectDetails> objects = GUIObjectDetails.transformKeys(keys);
        GUIObjectDetails requestAPICredentialsLink = null;
        for (GUIObjectDetails eachObject : objects) {
            if (eachObject.getMemberName().equals("requestAPICredentialsLink")) {
                requestAPICredentialsLink = eachObject;
                break;
            }
        }
        assertEquals(requestAPICredentialsLink != null ? requestAPICredentialsLink.getMemberType() : null,
                HtmlSeLionElement.LINK.getElementClass());
    }

}
