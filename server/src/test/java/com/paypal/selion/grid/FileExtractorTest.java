/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import static org.testng.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.openqa.selenium.Platform;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.pojos.SeLionGridConstants;

@PrepareForTest({ FileExtractor.class })
public class FileExtractorTest extends PowerMockTestCase {
    File extractedFile;
    File tarFile;

    static final String DUMMY_BZ2_ARCHIVE_FILE_PATH = new File(FileExtractorTest.class.getResource(
            "/artifacts/DummyBz2Archive.tar.bz2").getPath()).getAbsolutePath();

    static final String DUMMY_ZIP_ARCHIVE_FILE_PATH = new File(FileExtractorTest.class.getResource(
            "/artifacts/DummyArchive.zip").getPath()).getAbsolutePath();

    private static List<String> processNames;
    static {
        processNames = new ArrayList<String>();

        switch (Platform.getCurrent()) {
        case MAC:
        case UNIX:
        case LINUX: {
            processNames.add("dummyapp");
            break;
        }
        default: {
            processNames.add("dummyapp.exe");
            break;
        }
        }
    }

    @BeforeClass
    public void mkDownloadDir() throws IOException {
        File downloadDir = new File(SeLionGridConstants.DOWNLOADS_DIR);
        downloadDir.mkdirs();
    }
    
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        mockStatic(FileExtractor.class);
        when(FileExtractor.getExecutableNames()).thenReturn(processNames);
        when(FileExtractor.extractArchive(Mockito.anyString())).thenCallRealMethod();
        when(FileExtractor.getFileNameFromPath(Mockito.anyString())).thenCallRealMethod();

        extractedFile = new File(SeLionConstants.SELION_HOME_DIR + processNames.get(0));
    }

    @Test
    public void testExtractingZip() {
        List<String> files = FileExtractor.extractArchive(DUMMY_ZIP_ARCHIVE_FILE_PATH);
        assertTrue(extractedFile.exists());
        assertEquals(files.size(), 1);
    }

    @Test
    public void testExtractingBzip2() {
        List<String> files = FileExtractor.extractArchive(DUMMY_BZ2_ARCHIVE_FILE_PATH);
        assertTrue(extractedFile.exists());

        tarFile = new File(
                new File(FileExtractorTest.class.getResource("/artifacts/DummyBz2Archive.tar").getPath())
                        .getAbsolutePath());

        assertTrue(tarFile.exists());
        assertEquals(files.size(), 2);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanExtractedFiles() {
        extractedFile.delete();
        if (tarFile != null && tarFile.exists()) {
            tarFile.delete();
        }
    }
}
