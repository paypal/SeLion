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

package com.paypal.selion.platform.utilities;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.testng.annotations.Test;

import com.paypal.selion.platform.utilities.FileAssistant;

public class FileAssistantTest {
    @Test(groups = "unit")
    public void testLoadFileWithFileObject() {
        InputStream istream = FileAssistant.loadFile(new File("src/test/resources/List.yaml"));
        assertNotNull(istream, "File Load test via File Object");
    }

    @Test(groups = "unit")
    public void testLoadFileWithClassPath() {
        InputStream istream = FileAssistant.loadFile("List.yaml");
        assertNotNull(istream, "File Load test via File Object");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class }, expectedExceptionsMessageRegExp = ".* not a valid resource")
    public void testLoadFileWithFileObjectNegative() {
        FileAssistant.loadFile(new File("src/test/resources/PayPalProfilePageDoesntExist.yaml"));
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class }, expectedExceptionsMessageRegExp = ".* not a valid resource")
    public void testLoadFileWithClassPathNegative() {
        FileAssistant.loadFile("PayPalProfilePageDoesntExist.yaml");
    }

    @Test(groups = "unit")
    public void testReadFile() throws IOException {
        String contents = FileAssistant.readFile("src/test/resources/List.yaml");
        assertNotNull(contents, "File contents check");
        assertTrue(contents.contains("Block list of strings"), "File contents check");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testReadFileErrorConditionEmptyFileName() throws IOException {
        FileAssistant.readFile(" ");
    }

    @Test(groups = "unit", expectedExceptions = { IllegalArgumentException.class })
    public void testReadFileErrorConditionNullFileName() throws IOException {
        FileAssistant.readFile(null);
    }

}
