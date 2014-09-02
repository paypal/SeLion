package com.paypal.selion.pojos;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * A Pojo class to hold the details of the artifacts that needs to be downloaded
 * 
 */
public class ArtifactDetails {
    public static final String SELENIUM_KEY = "selenium";
    public static final String CHROME_KEY = "chrome";
    public static final String PHANTOM_KEY = "phantom";
    public static final String EXPLORER_KEY = "explorer";
    private Map<String, URLChecksumEntity> entities = new HashMap<>();

    public ArtifactDetails(Map<String, String> request) {
        if (request == null || request.isEmpty()) {
            throw new IllegalStateException("Request Map cannot be null (or) empty.");
        }
        entities.put(SELENIUM_KEY, getEntityFromRqst(PropsKeys.SELENIUM_URL,PropsKeys.SELENIUM_CHECKSUM, request));
        entities.put(CHROME_KEY, getEntityFromRqst(PropsKeys.CHROME_URL,PropsKeys.CHROME_CHECKSUM, request));
        entities.put(EXPLORER_KEY, getEntityFromRqst(PropsKeys.IE_URL,PropsKeys.IE_CHECKSUM, request));
        entities.put(PHANTOM_KEY, getEntityFromRqst(PropsKeys.PHANTOMJS_URL,PropsKeys.PHANTOMJS_CHECKSUM, request));

        
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

        entity = getEntityFromProp(PropsKeys.IE_URL, PropsKeys.IE_CHECKSUM, props);
        artifactDetailMap.put(EXPLORER_KEY, entity);

        entity = getEntityFromProp(PropsKeys.PHANTOMJS_URL, PropsKeys.PHANTOMJS_CHECKSUM, props);
        artifactDetailMap.put(PHANTOM_KEY, entity);
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
