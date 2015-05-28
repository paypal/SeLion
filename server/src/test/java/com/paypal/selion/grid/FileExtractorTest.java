/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.grid;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.Platform;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.pojos.SeLionGridConstants;

public class FileExtractorTest {
    File extractedFile = null;
    File tarFile = null;

    @BeforeClass
    public void mockTestPaths() {
        SeLionGridConstants.SELION_HOME_DIR = SystemUtils.USER_DIR + "/";
        tarFile = new File(SeLionGridConstants.SELION_HOME_DIR
                + "src/test/resources/archives/DummyBz2Archive.tar");
    }
    
    @BeforeMethod
    public void initExtractedFile(){
        extractedFile = new File(SeLionGridConstants.SELION_HOME_DIR + getExtractedFileName());
    }

    @Test
    public void testExtractingZip() {
        List<String> files = FileExtractor.extractArchive(SeLionGridConstants.SELION_HOME_DIR
                + "src/test/resources/archives/DummyArchive.zip");
        assertTrue(extractedFile.exists());
        assertTrue(files.size() == 1);
    }

    @Test
    public void testExtractingBzip2() {
        List<String> files = FileExtractor.extractArchive(SeLionGridConstants.SELION_HOME_DIR
                + "src/test/resources/archives/DummyBz2Archive.tar.bz2");
        assertTrue(extractedFile.exists());
        assertTrue(tarFile.exists());
        assertTrue(files.size() == 2);
    }
    
    @AfterMethod
    public void cleanExtractedFiles(){
        extractedFile.delete();
        if(tarFile.exists()){
            tarFile.delete();
        }
    }
    
    private String getExtractedFileName() {
        String fileName = null;
        switch (Platform.getCurrent()) {
        case LINUX:
        case MAC:
        case UNIX:
            fileName = "phantomjs";
            break;
        default:
            fileName = "phantomjs.exe";
            break;
        }
        return fileName;
    }
}
