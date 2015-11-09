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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.paypal.selion.grid.ArtifactDetails;
import com.paypal.selion.grid.ArtifactDetails.URLChecksumEntity;
import com.paypal.selion.grid.RunnableLauncher.InstanceType;

public class ArtifactDetailsTest {
    @Test
    public void testGetArtifactDetailsByRole() throws FileNotFoundException {
        File fileToTest = new File("src/test/resources/config/Dummydownload.json");
        List<URLChecksumEntity> parsedDetails = ArtifactDetails.getArtifactDetailsForCurrentPlatformByRole(fileToTest,
                InstanceType.SELENIUM_NODE);
        assertEquals(parsedDetails.size(), 2);
        URLChecksumEntity entity = parsedDetails.get(0);
        // Asserting for details read using any key
        assertEquals(entity.getUrl().getValue(), "seleniumURL");
        assertEquals(entity.getChecksum().getValue(), "seleniumChecksum");
        // Asserting for details read for a platform
        entity = parsedDetails.get(1);
        assertNotNull(entity.getUrl().getValue());
        assertNotNull(entity.getChecksum().getValue());
    }
    
    @Test
    public void testGetArtifactDetailsByName() throws FileNotFoundException {
        List<String> downloads = new ArrayList<>();
        downloads.add("selenium");
        
        File fileToTest = new File("src/test/resources/config/Dummydownload.json");
        List<URLChecksumEntity> parsedDetails = ArtifactDetails.getArtifactDetailsForCurrentPlatformByNames(fileToTest,
                downloads);
        assertEquals(parsedDetails.size(), 1);
        URLChecksumEntity entity = parsedDetails.get(0);
        // Asserting for details read using any key
        assertEquals(entity.getUrl().getValue(), "seleniumURL");
        assertEquals(entity.getChecksum().getValue(), "seleniumChecksum");
    }
}
