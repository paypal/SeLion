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

package com.paypal.selion.reports.services;

import java.util.Map;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import com.paypal.selion.reports.services.ReporterConfigMetadata;

public class ReporterConfigMetadataTest {

    @Test(groups = "unit")
    public void testInitialMetaDataHasCurrentDate() {
        String json = ReporterConfigMetadata.toJsonAsString();
        assertTrue(json.contains("\"" + ReporterConfigMetadata.DISPLAY_LABEL + "\": \"Current Date\""));

    }

    @Test(groups = "unit")
    public void testAddItemToMetaData() {
        String configKey = "testConfigKey";
        ReporterConfigMetadata.addReporterMetadataItem(configKey, ReporterConfigMetadata.DISPLAY_LABEL,
                "DisplayedTestKey");
        String json = ReporterConfigMetadata.toJsonAsString();
        assertTrue(json.contains("\"" + ReporterConfigMetadata.DISPLAY_LABEL + "\": \"DisplayedTestKey\""));

        Map<String, Map<String, String>> metadata = ReporterConfigMetadata.getReporterMetaData();
        assertTrue(metadata.containsKey(configKey));
        assertFalse(metadata.get(configKey).isEmpty());

    }

    @Test(groups = "unit")
    public void testNewDisplayLabelOverwritesOldOne() {
        String configKey = "testConfigKey2";
        ReporterConfigMetadata.addReporterMetadataItem(configKey, ReporterConfigMetadata.DISPLAY_LABEL,
                "OldDisplayValue");
        ReporterConfigMetadata.addReporterMetadataItem(configKey, ReporterConfigMetadata.DISPLAY_LABEL,
                "NewDisplayValue");
        String json = ReporterConfigMetadata.toJsonAsString();
        assertTrue(json.contains("\"" + ReporterConfigMetadata.DISPLAY_LABEL + "\": \"NewDisplayValue\""));
        assertFalse(json.contains("\"" + ReporterConfigMetadata.DISPLAY_LABEL + "\": \"OldDisplayValue\""));
    }

    @Test(groups = "unit")
    public void testUnsupportedMetaDataIsNotAdded() {
        ReporterConfigMetadata.addReporterMetadataItem("TestKey", "Unsupported", "DisplayedTestKey");
        String json = ReporterConfigMetadata.toJsonAsString();
        assertFalse(json.contains("\"Unsupported\": \"DisplayedTestKey\""));
    }

}
