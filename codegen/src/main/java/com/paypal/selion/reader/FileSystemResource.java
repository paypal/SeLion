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

package com.paypal.selion.reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple POJO that represents a file on the local file system.
 */
public class FileSystemResource {

    private final String fileName;

    public FileSystemResource(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getInputStream() {
        return new BufferedInputStream(loadFile());
    }

    private InputStream loadFile() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = loader.getResourceAsStream(fileName);
        if (iStream != null) {
            return iStream;
        }
        try {
            return new FileInputStream(fileName);
        } catch (FileNotFoundException e) { 
            throw new IllegalArgumentException("[" + fileName + "] is not a valid resource");
        }
    }

    @Override
    public String toString() {
        return "FileSystemResource: [ fileName = " + fileName + " ]";
    }

}
