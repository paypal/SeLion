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

package com.paypal.selion.grid.servlets.transfer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.RequestHeaders;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

/**
 * <code>DefaultManagedArtifact</code> represents an artifact that is successfully saved to SeLion grid by an HTTP POST
 * method call. This artifact mostly represents binary file types rather than text files. The MIME type for this
 * artifact is set to 'application/zip'. Expiry of the artifact is based on TTL (Time To Live) specified in milli
 * seconds. The configuration is read from Grid configuration system.
 */
public class DefaultManagedArtifact implements ManagedArtifact {

    private static final Logger logger = SeLionGridLogger.getLogger();

    private static final String EXPIRY_CONFIG_PROPERTY = "artifactExpiryInMilliSec";

    private static final String HTTP_CONTENT_TYPE = "application/zip";

    private String filePath = null;

    private File artifactFile = null;

    private String artifactName = null;

    private String folderName = null;

    private String parentFolderName = null;

    private byte[] contents = null;

    private final long timeToLiveInMillis;

    public DefaultManagedArtifact(String pathName) {
        this.filePath = pathName;
        artifactFile = new File(this.filePath);
        timeToLiveInMillis = ConfigParser.getInstance().getLong(EXPIRY_CONFIG_PROPERTY);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Time To Live configured in Grid: " + timeToLiveInMillis + " milli seconds.");
        }
    }

    public String getArtifactName() {
        if (artifactName == null) {
            artifactName = artifactFile.getName();
        }
        return artifactName;
    }

    public String getFolderName() {
        if (folderName == null) {
            folderName = artifactFile.getParentFile().getName();
        }
        return folderName;
    }

    public String getParentFolderName() {
        if (parentFolderName == null) {
            parentFolderName = artifactFile.getParentFile().getParentFile().getName();
        }
        return parentFolderName;
    }

    @Override
    public byte[] getArtifactContents() {
        if (contents == null) {
            readContents();
        }
        return contents;
    }

    @Override
    public <T extends Criteria> boolean matches(T criteria) {
        SeLionGridLogger.entering(criteria);
        if (!criteria.getArtifactName().equals(getArtifactName())) {
            SeLionGridLogger.exiting(false);
            return false;
        }
        if (isApplicationFolderRequested(criteria) && applicationFolderAndUserIdMatches(criteria)) {
            SeLionGridLogger.exiting(true);
            return true;
        }
        boolean matches = !isApplicationFolderRequested(criteria) && userIdMatches(criteria);
        SeLionGridLogger.exiting(matches);
        return matches;
    }

    @Override
    public boolean isExpired() {
        boolean expired = (System.currentTimeMillis() - artifactFile.lastModified()) > timeToLiveInMillis;
        if (expired) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(
                        Level.INFO,
                        "Artifact: " + getArtifactName() + " expired, time(now): "
                                + FileTime.fromMillis(System.currentTimeMillis()) + ", created: "
                                + FileTime.fromMillis(artifactFile.lastModified()));
            }
        }
        return expired;
    }

    @Override
    public String getHttpContentType() {
        return HTTP_CONTENT_TYPE;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultManagedArtifact)) {
            return false;
        }
        DefaultManagedArtifact otherManagedArtifact = DefaultManagedArtifact.class.cast(other);
        if (!getArtifactName().equals(otherManagedArtifact.getArtifactName())) {
            return false;
        }
        if (!getFolderName().equals(otherManagedArtifact.getFolderName())) {
            return false;
        }
        if (!getParentFolderName().equals(otherManagedArtifact.getParentFolderName())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getArtifactName().hashCode();
        result = 31 * result + getFolderName().hashCode();
        result = 31 * result + getParentFolderName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[ Artifact Name: " + getArtifactName() + ", Folder: " + getFolderName() + ", ParentFolder: "
                + getParentFolderName() + "]";
    }

    private void readContents() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(artifactFile));
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) artifactFile.length());
            IOUtils.copy(bis, bos);
            contents = bos.toByteArray();
        } catch (FileNotFoundException exe) {
            throw new ArtifactDownloadException("FileNotFoundException in reading bytes", exe);
        } catch (IOException exe) {
            throw new ArtifactDownloadException("IOException in reading bytes", exe);
        }
    }

    private <T extends Criteria> boolean isApplicationFolderRequested(T criteria) {
        return !StringUtils.isBlank(criteria.getApplicationFolder());
    }

    private <T extends Criteria> boolean applicationFolderAndUserIdMatches(T criteria) {
        return criteria.getApplicationFolder().equals(getFolderName())
                && criteria.getUserId().equals(getParentFolderName());
    }

    private <T extends Criteria> boolean userIdMatches(T criteria) {
        return criteria.getUserId().equals(getFolderName());
    }

    /**
     * {@link Criteria} to match a {@link DefaultManagedArtifact} uniquely. Criteria uses artifact name, user id and
     * application folder to uniquely identify a {@link DefaultManagedArtifact}. Parameters artifactName, userId and
     * applicationFolder match artifact name, folder name and parent folder name of some {@link DefaultManagedArtifact}
     * respectively.
     */
    public static class DefaultCriteria implements Criteria {

        protected String artifactName;

        protected String userId;

        protected String applicationFolder;

        public DefaultCriteria(EnumMap<RequestHeaders, String> parametersMap) {
            validateParametersMap(parametersMap);
            this.artifactName = parametersMap.get(RequestHeaders.FILENAME);
            this.userId = parametersMap.get(RequestHeaders.USERID);
            this.applicationFolder = parametersMap.get(RequestHeaders.APPLICATIONFOLDER);
        }

        private void validateParametersMap(EnumMap<RequestHeaders, String> parametersMap) {
            if (!parametersMap.containsKey(RequestHeaders.FILENAME)
                    || !parametersMap.containsKey(RequestHeaders.USERID)) {
                throw new ArtifactDownloadException("Request missing essential parametes: "
                        + RequestHeaders.FILENAME.getParameterName() + ", " + RequestHeaders.USERID.getParameterName());
            }
        }

        public String getArtifactName() {
            return artifactName;
        }

        public String getUserId() {
            return userId;
        }

        public String getApplicationFolder() {
            return applicationFolder;
        }

        public Map<String, String> asMap() {
            SeLionGridLogger.entering();
            Map<String, String> contentMap = new HashMap<>();
            contentMap.put(RequestHeaders.FILENAME.getParameterName(), getArtifactName());
            contentMap.put(RequestHeaders.USERID.getParameterName(), getUserId());
            if (!StringUtils.isBlank(getApplicationFolder())) {
                contentMap.put(RequestHeaders.APPLICATIONFOLDER.getParameterName(), getApplicationFolder());
            }
            SeLionGridLogger.exiting(contentMap);
            return contentMap;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DefaultCriteria)) {
                return false;
            }
            DefaultCriteria otherCriteria = DefaultCriteria.class.cast(other);
            if (!getArtifactName().equals(otherCriteria.getArtifactName())) {
                return false;
            }
            if (!getUserId().equals(otherCriteria.getUserId())) {
                return false;
            }
            boolean equals = getApplicationFolder() == null ? otherCriteria.getApplicationFolder() == null
                    : getApplicationFolder().equals(otherCriteria.getApplicationFolder());
            if (equals == false) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + this.getArtifactName().hashCode();
            result = 31 * result + this.getUserId().hashCode();
            result = 31 * result + (this.getApplicationFolder() != null ? this.getApplicationFolder().hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "[ artifactName: " + getArtifactName() + ", userId: " + getUserId() + ", applicationFolder: "
                    + getApplicationFolder() != null ? getApplicationFolder() : "" + " ]";
        }
    }

}
