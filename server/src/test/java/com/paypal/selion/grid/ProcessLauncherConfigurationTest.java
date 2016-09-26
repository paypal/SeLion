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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.testng.annotations.Test;

import com.beust.jcommander.JCommander;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paypal.selion.pojos.SeLionGridConstants;

public class ProcessLauncherConfigurationTest {
    class TestProcessLauncherOptions implements ProcessLauncherOptions {
        public <T extends LauncherOptions> T setFileDownloadCleanupOnInvocation(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isFileDownloadCleanupOnInvocation() {
            return true;
        }

        public <T extends LauncherOptions> T setFileDownloadCheckTimeStampOnInvocation(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isFileDownloadCheckTimeStampOnInvocation() {
            return true;
        }

        public String getSeLionConfig() {
            return "TestProcessLauncherOptions.json";
        }

        public <T extends LauncherOptions> T setSeLionConfig(String configFile) {
            throw new UnsupportedOperationException("not implemented");
        }

        public <T extends ProcessLauncherOptions> T setIncludeWebDriverBinaryPaths(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isIncludeWebDriverBinaryPaths() {
            return true;
        }

        public <T extends ProcessLauncherOptions> T setIncludeJavaSystemProperties(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isIncludeJavaSystemProperties() {
            return true;
        }

        public <T extends ProcessLauncherOptions> T setIncludeJarsInSeLionHomeDir(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isIncludeJarsInSeLionHomeDir() {
            return true;
        }

        public <T extends ProcessLauncherOptions> T setIncludeParentProcessClassPath(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isIncludeParentProcessClassPath() {
            return true;
        }

        public <T extends ProcessLauncherOptions> T setIncludeJarsInPresentWorkingDir(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isIncludeJarsInPresentWorkingDir() {
            return true;
        }

        public <T extends ProcessLauncherOptions> T setContinuouslyRestart(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isContinuouslyRestart() {
            return false;
        }

        public <T extends ProcessLauncherOptions> T setSetupLoggingForJavaSubProcess(boolean val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public boolean isSetupLoggingForJavaSubProcess() {
            return true;
        }

        public <T extends ProcessLauncherOptions> T setRestartCycle(long val) {
            throw new UnsupportedOperationException("not implemented");
        }

        public long getRestartCycle() {
            return 0L;
        }
    }

    @Test
    public void testDefaults() {
        ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration();
        assertTrue(plc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(plc.isFileDownloadCleanupOnInvocation());
        assertEquals(plc.getSeLionConfig(), SeLionGridConstants.SELION_CONFIG_FILE);
        assertTrue(plc.isContinuouslyRestart());
        assertTrue(plc.isIncludeJarsInPresentWorkingDir());
        assertTrue(plc.isIncludeJarsInSeLionHomeDir());
        assertTrue(plc.isIncludeJavaSystemProperties());
        assertTrue(plc.isIncludeParentProcessClassPath());
        assertTrue(plc.isIncludeWebDriverBinaryPaths());
        assertTrue(plc.isSetupLoggingForJavaSubProcess());
        assertEquals(plc.getRestartCycle(), 60000L);
    }

    @Test
    public void testParsedByJCommander() {
        ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration();
        new JCommander(plc, "-selionConfig", "foo.bar", "-noContinuousRestart", "-excludeJarsInPWD");
        assertTrue(plc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(plc.isFileDownloadCleanupOnInvocation());
        assertEquals(plc.getSeLionConfig(), "foo.bar");
        assertFalse(plc.isContinuouslyRestart());
        assertFalse(plc.isIncludeJarsInPresentWorkingDir());
        assertTrue(plc.isIncludeJarsInSeLionHomeDir());
        assertTrue(plc.isIncludeJavaSystemProperties());
        assertTrue(plc.isIncludeParentProcessClassPath());
        assertTrue(plc.isIncludeWebDriverBinaryPaths());
        assertTrue(plc.isSetupLoggingForJavaSubProcess());
        assertEquals(plc.getRestartCycle(), 60000L);
    }

    @Test
    public void testMerge() {
        ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration();

        // thest that it can merge ANY ProcessLauncherOptions implementation
        TestProcessLauncherOptions tplo = new TestProcessLauncherOptions();
        plc.merge(tplo);
        assertTrue(plc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(plc.isFileDownloadCleanupOnInvocation());
        assertEquals(plc.getSeLionConfig(), tplo.getSeLionConfig());
        assertFalse(plc.isContinuouslyRestart());
        assertTrue(plc.isIncludeJarsInPresentWorkingDir());
        assertTrue(plc.isIncludeJarsInSeLionHomeDir());
        assertTrue(plc.isIncludeJavaSystemProperties());
        assertTrue(plc.isIncludeParentProcessClassPath());
        assertTrue(plc.isIncludeWebDriverBinaryPaths());
        assertTrue(plc.isSetupLoggingForJavaSubProcess());
        assertEquals(plc.getRestartCycle(), 0L);

        // test that it can merge any ProcessLauncherConfiguration and that a null value is not merged
        plc.noContinuousRestart = null;
        ProcessLauncherConfiguration otherPlc = new ProcessLauncherConfiguration();
        otherPlc.merge(plc);
        assertTrue(otherPlc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(otherPlc.isFileDownloadCleanupOnInvocation());
        assertEquals(otherPlc.getSeLionConfig(), plc.getSeLionConfig()); // should be merged from the lc
        assertTrue(otherPlc.isContinuouslyRestart()); // should be back to the default value
        assertTrue(otherPlc.isIncludeJarsInPresentWorkingDir());
        assertTrue(otherPlc.isIncludeJarsInSeLionHomeDir());
        assertTrue(otherPlc.isIncludeJavaSystemProperties());
        assertTrue(otherPlc.isIncludeParentProcessClassPath());
        assertTrue(otherPlc.isIncludeWebDriverBinaryPaths());
        assertTrue(otherPlc.isSetupLoggingForJavaSubProcess());
        assertEquals(otherPlc.getRestartCycle(), plc.getRestartCycle());
    }

    @Test
    public void testSettersAndGetters() {
        ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration();
        plc.setFileDownloadCheckTimeStampOnInvocation(false);
        plc.setFileDownloadCleanupOnInvocation(false);
        plc.setSeLionConfig("bar.json");
        plc.setContinuouslyRestart(false);
        plc.setIncludeJarsInPresentWorkingDir(false);
        plc.setIncludeJarsInSeLionHomeDir(false);
        plc.setIncludeJavaSystemProperties(false);
        plc.setIncludeParentProcessClassPath(false);
        plc.setIncludeWebDriverBinaryPaths(false);
        plc.setSetupLoggingForJavaSubProcess(false);
        plc.setRestartCycle(20000L);
        assertFalse(plc.isFileDownloadCheckTimeStampOnInvocation());
        assertFalse(plc.isFileDownloadCleanupOnInvocation());
        assertEquals(plc.getSeLionConfig(), "bar.json");
        assertFalse(plc.isContinuouslyRestart());
        assertFalse(plc.isIncludeJarsInPresentWorkingDir());
        assertFalse(plc.isIncludeJarsInSeLionHomeDir());
        assertFalse(plc.isIncludeJavaSystemProperties());
        assertFalse(plc.isIncludeParentProcessClassPath());
        assertFalse(plc.isIncludeWebDriverBinaryPaths());
        assertFalse(plc.isSetupLoggingForJavaSubProcess());
        assertEquals(plc.getRestartCycle(), 20000L);
    }

    @Test
    public void testToString() {
        ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration();
        assertNotNull(plc.toString());
        assertTrue(plc.toString().contains("noDownloadTimeStampCheck=null"));
        assertTrue(plc.toString().contains("excludeJarsInPWD=null"));
    }

    @Test
    public void testToJson() {
        ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration();
        plc.setIncludeJarsInPresentWorkingDir(false);
        JsonElement json = plc.toJson();
        assertNotNull(json);
        assertTrue(json.getAsJsonObject().get("excludeJarsInPWD").getAsBoolean());
        assertTrue(json.getAsJsonObject().get("noDownloadCleanup").isJsonNull());
        assertFalse(json.getAsJsonObject().has("selionConfig")); // does not serialize or de-serialize
    }

    @Test
    public void testFromJsonElement() {
        JsonObject json = new JsonObject();
        json.addProperty("excludeJarsInPWD", true);
        ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration().fromJson(json);
        assertNotNull(plc);
        assertTrue(plc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(plc.isFileDownloadCleanupOnInvocation());
        assertEquals(plc.getSeLionConfig(), SeLionGridConstants.SELION_CONFIG_FILE);
        assertTrue(plc.isContinuouslyRestart());
        assertFalse(plc.isIncludeJarsInPresentWorkingDir());
        assertTrue(plc.isIncludeJarsInSeLionHomeDir());
        assertTrue(plc.isIncludeJavaSystemProperties());
        assertTrue(plc.isIncludeParentProcessClassPath());
        assertTrue(plc.isIncludeWebDriverBinaryPaths());
        assertTrue(plc.isSetupLoggingForJavaSubProcess());
        assertEquals(plc.getRestartCycle(), 60000L);
    }

    @Test
    public void testFromJsonString() {
        String json = "{\"excludeJarsInPWD\": true}";
        ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration().fromJson(json);
        assertNotNull(plc);
        assertTrue(plc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(plc.isFileDownloadCleanupOnInvocation());
        assertEquals(plc.getSeLionConfig(), SeLionGridConstants.SELION_CONFIG_FILE);
        assertTrue(plc.isContinuouslyRestart());
        assertFalse(plc.isIncludeJarsInPresentWorkingDir());
        assertTrue(plc.isIncludeJarsInSeLionHomeDir());
        assertTrue(plc.isIncludeJavaSystemProperties());
        assertTrue(plc.isIncludeParentProcessClassPath());
        assertTrue(plc.isIncludeWebDriverBinaryPaths());
        assertTrue(plc.isSetupLoggingForJavaSubProcess());
        assertEquals(plc.getRestartCycle(), 60000L);
    }

    @Test
    public void testLoadFromFile() throws IOException {
        ProcessLauncherConfiguration plc = ProcessLauncherConfiguration
                .loadFromFile(SeLionGridConstants.SELION_CONFIG_FILE);
        assertNotNull(plc);
        // we should just get the defaults back since the default selionConfig file specifies no values
        assertTrue(plc.isFileDownloadCheckTimeStampOnInvocation());
        assertTrue(plc.isFileDownloadCleanupOnInvocation());
        assertEquals(plc.getSeLionConfig(), SeLionGridConstants.SELION_CONFIG_FILE);
        assertTrue(plc.isContinuouslyRestart());
        assertTrue(plc.isIncludeJarsInPresentWorkingDir());
        assertTrue(plc.isIncludeJarsInSeLionHomeDir());
        assertTrue(plc.isIncludeJavaSystemProperties());
        assertTrue(plc.isIncludeParentProcessClassPath());
        assertTrue(plc.isIncludeWebDriverBinaryPaths());
        assertTrue(plc.isSetupLoggingForJavaSubProcess());
        assertEquals(plc.getRestartCycle(), 60000L);

    }
}
