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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.elements.HtmlSeLionElementList;

public class DataReaderTest {
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
    public void getKeys_v1() throws Exception {
        DataReader r = new DataReader("src/test/resources/PayPalAbstractPage.yml");
        List<String> keys = r.getKeys();
        assertTrue(keys.contains("messageBoxConfirmationLabel"));
    }

    @Test
    public void getKeys_v2() throws Exception {
        DataReader r = new DataReader("src/test/resources/SampleV2YamlPage.yaml");
        List<String> keys = r.getKeys();
        assertTrue(keys.contains("requestAPICredentialsLink"));
    }

    @Test
    public void getBaseClass_v2() throws Exception {
        DataReader r = new DataReader("src/test/resources/SampleV2YamlPage.yaml");
        String baseClass = r.getBaseClassName();
        assertEquals(baseClass, "com.paypal.selion.testcomponents.BasicPageImpl");
    }

    @Test
    public void getHtmlObjectDetails() throws Exception {
        DataReader r = new DataReader("src/test/resources/SampleV2YamlPage.yaml");
        List<String> keys = r.getKeys();
        List<GUIObjectDetails> objects = GUIObjectDetails.transformKeys(keys);
        GUIObjectDetails requestAPICredentialsLink = null;
        for (GUIObjectDetails eachObject : objects) {
            if (eachObject.getMemberName().equals("requestAPICredentialsLink")) {
                requestAPICredentialsLink = eachObject;
                break;
            }
        }
        assertEquals(requestAPICredentialsLink.getMemberType(), HtmlSeLionElementList.LINK.stringify());
    }

}
