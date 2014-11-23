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

package com.paypal.selion.pojos;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Platform;

import com.google.common.base.Preconditions;

/**
 * A Pojo class to hold the details of the artifacts that needs to be downloaded
 * 
 */
public class ArtifactDetails {
    public static final String SELENIUM_KEY = "selenium";
    public static final String CHROME_KEY = "chrome";
    public static final String PHANTOM_KEY = "phantom";
    public static final String EXPLORER_KEY = "explorer";
    public static final String CHROME_LINUX_KEY = "chrome_linux";
    public static final String CHROME_MAC_KEY = "chrome_mac";
    public static final String PHANTOM_LINUX_KEY = "phantom_linux";
    public static final String PHANTOM_MAC_KEY = "phantom_mac";
    private Map<String, URLChecksumEntity> entities = new HashMap<>();

    public ArtifactDetails(Map<String, String> request) {
        entities.put(SELENIUM_KEY, getEntityFromRqst(PropsKeys.SELENIUM_URL, PropsKeys.SELENIUM_CHECKSUM, request));
        switch (Platform.getCurrent()) {
        case UNIX:
        case LINUX: 
            entities.put(CHROME_LINUX_KEY,
                    getEntityFromRqst(PropsKeys.CHROME_LINUX_URL, PropsKeys.CHROME_LINUX_CHECKSUM, request));
            entities.put(PHANTOM_LINUX_KEY,
                    getEntityFromRqst(PropsKeys.PHANTOMJS_LINUX_URL, PropsKeys.PHANTOMJS_LINUX_CHECKSUM, request));
            break;

        case MAC: 
            entities.put(CHROME_MAC_KEY,
                    getEntityFromRqst(PropsKeys.CHROME_MAC_URL, PropsKeys.CHROME_MAC_CHECKSUM, request));
            entities.put(PHANTOM_MAC_KEY,
                    getEntityFromRqst(PropsKeys.PHANTOMJS_MAC_URL, PropsKeys.PHANTOMJS_MAC_CHECKSUM, request));
            break;

        default: 
            entities.put(CHROME_KEY, getEntityFromRqst(PropsKeys.CHROME_URL, PropsKeys.CHROME_CHECKSUM, request));
            entities.put(EXPLORER_KEY, getEntityFromRqst(PropsKeys.IE_URL, PropsKeys.IE_CHECKSUM, request));
            entities.put(PHANTOM_KEY, getEntityFromRqst(PropsKeys.PHANTOMJS_URL, PropsKeys.PHANTOMJS_CHECKSUM, request));
            break;

        }
    }
    
    private URLChecksumEntity getEntityFromRqst(PropsKeys urlKey, PropsKeys checksumKey, Map<String, String> request) {
        NameValuePair url = new BasicNameValuePair(urlKey.getKey(), request.get(urlKey.getKey()));
        NameValuePair checksum = new BasicNameValuePair(checksumKey.getKey(), request.get(checksumKey.getKey()));
        return new URLChecksumEntity(url, checksum);
    }

    private static URLChecksumEntity getEntityFromProp(PropsKeys urlKey, PropsKeys checksumKey, Properties request) {
        NameValuePair url = new BasicNameValuePair(urlKey.getKey(), request.getProperty(urlKey.getKey()));
        NameValuePair checksum = new BasicNameValuePair(checksumKey.getKey(), request.getProperty(checksumKey.getKey()));
        return new URLChecksumEntity(url, checksum);
    }

    /**
     * Utility method to convert a properties list into a {@link Map} containing URL and CheckSum in Lists.<br>
     * 
     * @param props
     *            - The property list containing the URL and Checksum parameters
     * @return A Map Containing the list which in turn has URL and Checksum
     */
    public static Map<String, URLChecksumEntity> getArtifactDetailsAsMap(Properties props) {
        Map<String, URLChecksumEntity> artifactDetailMap = new HashMap<>();

        URLChecksumEntity entity = getEntityFromProp(PropsKeys.SELENIUM_URL, PropsKeys.SELENIUM_CHECKSUM, props);
        artifactDetailMap.put(SELENIUM_KEY, entity);

        entity = getEntityFromProp(PropsKeys.CHROME_URL, PropsKeys.CHROME_CHECKSUM, props);
        artifactDetailMap.put(CHROME_KEY, entity);

        entity = getEntityFromProp(PropsKeys.CHROME_LINUX_URL, PropsKeys.CHROME_LINUX_CHECKSUM, props);
        artifactDetailMap.put(CHROME_LINUX_KEY, entity);

        entity = getEntityFromProp(PropsKeys.CHROME_MAC_URL, PropsKeys.CHROME_MAC_CHECKSUM, props);
        artifactDetailMap.put(CHROME_MAC_KEY, entity);

        entity = getEntityFromProp(PropsKeys.IE_URL, PropsKeys.IE_CHECKSUM, props);
        artifactDetailMap.put(EXPLORER_KEY, entity);

        entity = getEntityFromProp(PropsKeys.PHANTOMJS_URL, PropsKeys.PHANTOMJS_CHECKSUM, props);
        artifactDetailMap.put(PHANTOM_KEY, entity);

        entity = getEntityFromProp(PropsKeys.PHANTOMJS_LINUX_URL, PropsKeys.PHANTOMJS_LINUX_CHECKSUM, props);
        artifactDetailMap.put(PHANTOM_LINUX_KEY, entity);

        entity = getEntityFromProp(PropsKeys.PHANTOMJS_MAC_URL, PropsKeys.PHANTOMJS_MAC_CHECKSUM, props);
        artifactDetailMap.put(PHANTOM_MAC_KEY, entity);
        return artifactDetailMap;
    }

    /**
     * Utility method to return the Artifact details as a Map making it convenient to iterate
     * 
     * @return A {@link Map} containing the URL and CheckSum with Keys for the map from {@link PropsKeys}
     */
    public Map<String, URLChecksumEntity> getArtifactDetailsAsMap() {
        return new HashMap<>(this.entities);
    }

    /**
     * Utility method to return the {@link ArtifactDetails} as a {@link Map} specific to {@link Platform}
     * 
     * @param platform
     *            {@link Platform} mentioning the platform
     * @param props
     *            {@link Properties} containing the artifact details
     * @return A {@link Map} containing the URL and CheckSum with Keys for the map from {@link PropsKeys}
     */
    public static Map<String, URLChecksumEntity> getArtifactDetailsForCurrentPlatform(Properties props) {
        Preconditions.checkNotNull(props, "The Properties to get artifact details cannot be null");
        Map<String, URLChecksumEntity> artifactDetailMap = new HashMap<>();

        URLChecksumEntity entity = getEntityFromProp(PropsKeys.SELENIUM_URL, PropsKeys.SELENIUM_CHECKSUM, props);
        artifactDetailMap.put(SELENIUM_KEY, entity);

        switch (Platform.getCurrent()) {
        case UNIX:
        case LINUX: 

            entity = getEntityFromProp(PropsKeys.CHROME_LINUX_URL, PropsKeys.CHROME_LINUX_CHECKSUM, props);
            artifactDetailMap.put(CHROME_LINUX_KEY, entity);

            entity = getEntityFromProp(PropsKeys.PHANTOMJS_LINUX_URL, PropsKeys.PHANTOMJS_LINUX_CHECKSUM, props);
            artifactDetailMap.put(PHANTOM_LINUX_KEY, entity);

            break;

        case MAC: 

            entity = getEntityFromProp(PropsKeys.CHROME_MAC_URL, PropsKeys.CHROME_MAC_CHECKSUM, props);
            artifactDetailMap.put(CHROME_MAC_KEY, entity);

            entity = getEntityFromProp(PropsKeys.PHANTOMJS_MAC_URL, PropsKeys.PHANTOMJS_MAC_CHECKSUM, props);
            artifactDetailMap.put(PHANTOM_MAC_KEY, entity);

            break;

        default: 
            entity = getEntityFromProp(PropsKeys.CHROME_URL, PropsKeys.CHROME_CHECKSUM, props);
            artifactDetailMap.put(CHROME_KEY, entity);

            entity = getEntityFromProp(PropsKeys.PHANTOMJS_URL, PropsKeys.PHANTOMJS_CHECKSUM, props);
            artifactDetailMap.put(PHANTOM_KEY, entity);

            entity = getEntityFromProp(PropsKeys.IE_URL, PropsKeys.IE_CHECKSUM, props);
            artifactDetailMap.put(EXPLORER_KEY, entity);

            break;

        }

        return artifactDetailMap;
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
