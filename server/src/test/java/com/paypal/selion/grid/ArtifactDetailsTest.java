package com.paypal.selion.grid;

import static org.testng.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.testng.annotations.Test;

import com.paypal.selion.grid.ArtifactDetails;
import com.paypal.selion.grid.ArtifactDetails.URLChecksumEntity;
import com.paypal.selion.grid.RunnableLauncher.InstanceType;

public class ArtifactDetailsTest {
    @Test
    public void testGetArtifactDetails() throws FileNotFoundException {
        File fileToTest = new File("src/test/resources/config/Dummydownload.json");
        List<URLChecksumEntity> parsedDetails = ArtifactDetails.getArtifactDetailsForCurrentPlatformAndRole(fileToTest,
                InstanceType.SELENIUM_NODE);
        assertTrue(parsedDetails.size() == 2);
        URLChecksumEntity entity = parsedDetails.get(0);
        // Asserting for details read using any key
        assertEquals(entity.getUrl().getValue(), "seleniumURL");
        assertEquals(entity.getChecksum().getValue(), "seleniumChecksum");
        // Asserting for details read for a platform
        entity = parsedDetails.get(1);
        assertNotNull(entity.getUrl().getValue());
        assertNotNull(entity.getChecksum().getValue());
    }
}
