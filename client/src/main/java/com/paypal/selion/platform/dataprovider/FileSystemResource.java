/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.dataprovider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.paypal.selion.platform.utilities.FileAssistant;

/**
 * FileSystemResource defines the file path and file name of the data source to be used for data provider consumption. Loading a
 * file (Yaml for e.g.,) containing user-defined (complex) objects also requires passing in the object class. Passing in a complex
 * object with nested complex objects does not require any additional parameters.
 * 
 * <br>
 * <h3>Sample usage:</h3>
 * 
 * <pre>
 * public static Object[][] dataProviderGetAllDocuments() throws IOException {
 *     FileSystemResource resource = new FileSystemResource(pathName, fileName, USER.class);
 *     Object[][] data = YamlDataProvider.getAllData(resource);
 *     return data;
 * }
 * </pre>
 * 
 * If test requires passing multiple arguments, multiple YamlResources can be defined to create such a data provider.
 * 
 * <br>
 * <h3>Sample usage:</h3>
 * 
 * <pre>
 * public static Object[][] dataProviderGetMultipleArguments() throws IOException {
 *     List&lt;FileSystemResource&gt; yamlResources = new ArrayList&lt;FileSystemResource&gt;();
 *     yamlResources.add(new FileSystemResource(pathName, fileName1, USER.class));
 *     yamlResources.add(new FileSystemResource(pathName, fileName2, USER.class));
 * 
 *     Object[][] data = YamlDataProvider.getAllDataMultipleArgs(yamlResources);
 * 
 *     return data;
 * }
 * </pre>
 * 
 * Test method signature example:
 * 
 * <pre>
 *   public void testExample(USER user1, USER user2)
 * </pre>
 * 
 * 
 */
public class FileSystemResource {

    private String pathName = null;
    private String fileName = null;
    private Class<?> cls = null;

    public FileSystemResource() {
    }

    /**
     * Use this constructor when a data source file can be found in the path specified and contains a user-defined object.
     * 
     * @param pathName
     * @param fileName
     * @param cls
     */
    public FileSystemResource(String pathName, String fileName, Class<?> cls) {
        super();
        setPathName(pathName);
        setFileName(fileName);
        setCls(cls);
    }

    /**
     * Use this constructor when a data source file can be found as a resource and contains a user-defined object.
     * 
     * @param fileName
     * @param cls
     */
    public FileSystemResource(String fileName, Class<?> cls) {
        this(null, fileName, cls);
    }

    /**
     * Use this constructor when a data source file can be found in the path specified and does NOT contain a user-defined
     * object.
     * 
     * @param pathName
     * @param fileName
     */
    public FileSystemResource(String pathName, String fileName) {
        this(pathName, fileName, null);
    }

    /**
     * Use this constructor when a data source file can be found as a resource and does NOT contain a user-defined object.
     * 
     * @param fileName
     */
    public FileSystemResource(String fileName) {
        this(null, fileName, null);
    }

    public String getPathName() {
        return pathName;
    }

    public final void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getFileName() {
        return fileName;
    }

    public final void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Class<?> getCls() {
        return cls;
    }

    public final void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public InputStream getInputStream() {
        InputStream is = null;

        if (this.pathName == null) {
            is = new BufferedInputStream(FileAssistant.loadFile(this.fileName));
        } else {
            File file = new File(this.pathName + this.fileName);
            is = new BufferedInputStream(FileAssistant.loadFile(file));
        }

        return is;
    }
    
    public Reader getReader() {
        InputStreamReader is = null;
        try {
            File resourceFile = new File(this.pathName,this.fileName);
            is = new InputStreamReader(new FileInputStream(resourceFile));
        } catch (FileNotFoundException f) {
            throw new RuntimeException("File Not Found", f);
        }
        return is;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("FileSystemResource: [ ");
        str.append("pathName = " + this.getPathName() + ", ");
        str.append("fileName = " + this.getFileName() + ", ");
        str.append("class = " + this.getCls() + " ]");
        return str.toString();
    }

}
