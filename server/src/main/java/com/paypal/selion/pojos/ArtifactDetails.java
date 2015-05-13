/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 eBay Software Foundation                                                                   |
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

package com.paypal.selion.pojos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Platform;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A POJO class to hold the details of the artifacts that need to be downloaded. These details are defined in the
 * download.json file
 * 
 */
public class ArtifactDetails {

    private static final String CHECKSUM = "checksum";
    private static final String URL = "url";
    private static final String NAME = "name";

    private static URLChecksumEntity getEntityFromProp(String urlKey, String urlValue, String checksumKey,
            String checksumValue) {
        NameValuePair url = new BasicNameValuePair(urlKey, urlValue);
        NameValuePair checksum = new BasicNameValuePair(checksumKey, checksumValue);
        return new URLChecksumEntity(url, checksum);
    }

    /**
     * Utility method to return the {@link ArtifactDetails} as a {@link List} specific to {@link Platform}
     *
     * @param downloadFile
     *            containing the artifact details
     * @return A {@link List} containing the URL and CheckSum
     */
    public static List<URLChecksumEntity> getArtifactDetailsForCurrentPlatform(File downloadFile)
            throws FileNotFoundException {
        Preconditions.checkNotNull(downloadFile, "The JSON to get artifact details cannot be null");
        List<URLChecksumEntity> artifactDetails = new ArrayList<URLChecksumEntity>();

        JsonArray downloads = (new JsonParser()).parse(new FileReader(downloadFile)).getAsJsonArray();

        for (int i = 0; i < downloads.size(); i++) {
            JsonObject artifact = (JsonObject) downloads.get(i);
            if (artifact.has(NAME)) {
                JsonElement platformJson = artifact.has("any") ? artifact.get("any") : artifact.get(getPlatform());
                if (platformJson != null) {
                    JsonObject platform = platformJson.getAsJsonObject();
                    String url = platform.get(URL).getAsString();
                    String checksum = platform.get(CHECKSUM).getAsString();
                    URLChecksumEntity entity = getEntityFromProp(URL, url, CHECKSUM, checksum);
                    artifactDetails.add(entity);
                }
            }
        }

        return artifactDetails;
    }

    private static String getPlatform() {
        switch (Platform.getCurrent()) {
        case UNIX:
        case LINUX:
            return "linux";

        case MAC:
            return "mac";

        default:
            return "windows";
        }
    }

    /**
     * A simple POJO that represents the key value pair for url and checksum for a given entity.
     *
     */
    public static class URLChecksumEntity {
        private NameValuePair url, checksum;

        public URLChecksumEntity(NameValuePair url, NameValuePair checksum) {
            this.url = url;
            this.checksum = checksum;
        }

        public NameValuePair getUrl() {
            return url;
        }

        public NameValuePair getChecksum() {
            return checksum;
        }
    }

}
