/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import java.io.IOException;

import org.testng.annotations.Test;

import com.beust.jcommander.JCommander;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paypal.selion.pojos.SeLionGridConstants;

public class LauncherConfigurationTest {
    private final class TestLauncherOptions implements LauncherOptions {
        public <T extends LauncherOptions> T setFileDownloadCleanupOnInvocation(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isFileDownloadCleanupOnInvocation() {
            return false;
        }

        public <T extends LauncherOptions> T setFileDownloadCheckTimeStampOnInvocation(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isFileDownloadCheckTimeStampOnInvocation() {
            return true;
        }

        public String getSeLionConfig() {
            return "TestLauncherOptions.json";
        }

        public <T extends LauncherOptions> T setSeLionConfig(String configFile) {
            throw new UnsupportedOperationException("not implemented");
        }
    }

    @Test
    public void testDefaults() {
        LauncherConfiguration lc = new LauncherConfiguration();
        assertTrue(lc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(lc.isFileDownloadCleanupOnInvocation());
        assertEquals(lc.getSeLionConfig(), SeLionGridConstants.SELION_CONFIG_FILE);
    }

    @Test
    public void testParsedByJCommander() {
        LauncherConfiguration lc = new LauncherConfiguration();
        new JCommander(lc, "-selionConfig", "foo.bar", "-downloadCleanup", "false", "-downloadTimeStampCheck", "false");
        assertFalse(lc.isFileDownloadCheckTimeStampOnInvocation());
        assertFalse(lc.isFileDownloadCleanupOnInvocation());
        assertEquals(lc.getSeLionConfig(), "foo.bar");
    }

    @Test
    public void testMerge() {
        LauncherConfiguration lc = new LauncherConfiguration();

        // test that it can merge ANY LauncherOptions implementation
        TestLauncherOptions tlo = new TestLauncherOptions();
        lc.merge(tlo);
        assertTrue(lc.isFileDownloadCheckTimeStampOnInvocation());
        assertFalse(lc.isFileDownloadCleanupOnInvocation());
        assertEquals(lc.getSeLionConfig(), tlo.getSeLionConfig());

        // test that it can merge any LauncherOptions and that a merged null value results in the default response
        lc.downloadCleanup = null;
        LauncherConfiguration otherLc = new LauncherConfiguration();
        otherLc.merge(lc);
        assertTrue(otherLc.isFileDownloadCleanupOnInvocation()); // should return true, since downloadCleanup=null
        assertEquals(otherLc.getSeLionConfig(), lc.getSeLionConfig()); // should be merged from the lc
    }

    @Test
    public void testSettersAndGetters() {
        LauncherConfiguration lc = new LauncherConfiguration();
        lc.setFileDownloadCheckTimeStampOnInvocation(false);
        lc.setFileDownloadCleanupOnInvocation(false);
        lc.setSeLionConfig("bar.json");
        assertFalse(lc.isFileDownloadCheckTimeStampOnInvocation());
        assertFalse(lc.isFileDownloadCleanupOnInvocation());
        assertEquals(lc.getSeLionConfig(), "bar.json");
    }

    @Test
    public void testToString() {
        LauncherConfiguration lc = new LauncherConfiguration();
        assertNotNull(lc.toString());
        assertTrue(lc.toString().contains("downloadTimeStampCheck=true"));
    }

    @Test
    public void testToJson() {
        LauncherConfiguration lc = new LauncherConfiguration();
        lc.setFileDownloadCheckTimeStampOnInvocation(false);
        JsonElement json = lc.toJson();
        assertNotNull(json);
        assertFalse(json.getAsJsonObject().get("downloadTimeStampCheck").getAsBoolean());
        assertTrue(json.getAsJsonObject().get("downloadCleanup").getAsBoolean());
        assertFalse(json.getAsJsonObject().has("selionConfig")); // does not serialize or de-serialize
    }

    @Test
    public void testFromJsonElement() {
        JsonObject json = new JsonObject();
        json.addProperty("selionConfig", "");
        json.addProperty("downloadCleanup", false);
        LauncherConfiguration lc = new LauncherConfiguration().fromJson(json);
        assertNotNull(lc);
        // selionConfig should not serialize or de-serialize, so the default value should be on lc
        assertEquals(lc.getSeLionConfig(), SeLionGridConstants.SELION_CONFIG_FILE);
        assertTrue(lc.isFileDownloadCheckTimeStampOnInvocation());
        assertFalse(lc.isFileDownloadCleanupOnInvocation());
    }

    @Test
    public void testFromJsonString() {
        String json = "{\"selionConfig\": \"\", \"downloadCleanup\": false}";
        LauncherConfiguration lc = new LauncherConfiguration().fromJson(json);
        assertNotNull(lc);
        // selionConfig should not serialize or de-serialize, so the default value should be on lc
        assertEquals(lc.getSeLionConfig(), SeLionGridConstants.SELION_CONFIG_FILE);
        assertTrue(lc.isFileDownloadCheckTimeStampOnInvocation());
        assertFalse(lc.isFileDownloadCleanupOnInvocation());
    }

    @Test
    public void testLoadFromFile() throws IOException {
        LauncherConfiguration lc = LauncherConfiguration.loadFromFile(SeLionGridConstants.SELION_CONFIG_FILE);
        assertNotNull(lc);
        // we should just get the defaults back since the default selionConfig file specifies no values
        assertEquals(lc.getSeLionConfig(), SeLionGridConstants.SELION_CONFIG_FILE);
        assertTrue(lc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(lc.isFileDownloadCleanupOnInvocation());
    }
}
