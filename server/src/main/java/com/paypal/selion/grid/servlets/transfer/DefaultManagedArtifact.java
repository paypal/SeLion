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

package com.paypal.selion.grid.servlets.transfer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.EnumMap;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.RequestHeaders;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

/**
 * <code>DefaultManagedArtifact</code> represents an artifact that is successfully saved to SeLion grid by an HTTP POST
 * method call. This artifact mostly represents binary file types rather than text files. The MIME type for this
 * artifact is set to 'application/zip'. Expiry of the artifact is based on TTL (Time To Live) specified in milli
 * seconds. The configuration is read from Grid configuration system.
 */
public class DefaultManagedArtifact implements ManagedArtifact<DefaultCriteria> {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(DefaultManagedArtifact.class);

    private static final String EXPIRY_CONFIG_PROPERTY = "artifactExpiryInMilliSec";

    private static final String HTTP_CONTENT_TYPE = "application/zip";

    private String filePath = null;

    private File artifactFile = null;

    private String artifactName = null;

    private String folderName = null;

    private String parentFolderName = null;

    private byte[] contents = null;

    private final long timeToLiveInMillis;

    private String requestPathInfo;

    private DefaultCriteria requestedCriteria = null;

    public DefaultManagedArtifact(String pathName) {
        this.filePath = pathName;
        artifactFile = new File(this.filePath);
        timeToLiveInMillis = ConfigParser.parse().getLong(EXPIRY_CONFIG_PROPERTY);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Time To Live configured in Grid: " + timeToLiveInMillis + " milli seconds.");
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
    public DefaultCriteria getCriteria() {
        return formCriteria();
    }

    @Override
    public boolean matchesCriteria(String pathInfo) {
        LOGGER.entering();
        this.requestPathInfo = pathInfo; 
        DefaultCriteria criteria = getCriteria();
        if (!criteria.getArtifactName().equals(this.getArtifactName())) {
            LOGGER.exiting(false);
            return false;
        }
        if (isApplicationFolderRequested(criteria) && applicationFolderAndUserIdMatches(criteria)) {
            LOGGER.exiting(true);
            return true;
        }
        boolean matches = !isApplicationFolderRequested(criteria) && userIdMatches(criteria);
        LOGGER.exiting(matches);
        return matches;
    }

    @Override
    public boolean isExpired() {
        boolean expired = (System.currentTimeMillis() - artifactFile.lastModified()) > timeToLiveInMillis;
        if (expired) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(
                        Level.INFO,
                        "Artifact: " + this.getArtifactName() + " expired, time(now): "
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

    private boolean isApplicationFolderRequested(DefaultCriteria criteria) {
        return !StringUtils.isBlank(criteria.getApplicationFolder());
    }

    private boolean applicationFolderAndUserIdMatches(DefaultCriteria criteria) {
        return criteria.getApplicationFolder().equals(getFolderName())
                && criteria.getUserId().equals(getParentFolderName());
    }

    private boolean userIdMatches(DefaultCriteria criteria) {
        return criteria.getUserId().equals(getFolderName());
    }

    private DefaultCriteria formCriteria() {
        if (requestedCriteria == null) {
            EnumMap<RequestHeaders, String> parametersMap = getParametersMap();
            requestedCriteria = new DefaultCriteria(parametersMap);
        }
        return requestedCriteria;
    }

    private EnumMap<RequestHeaders, String> getParametersMap() {
        EnumMap<RequestHeaders, String> parametersMap = populateMapFromPathInfo();
        if (!(parametersMap.containsKey(RequestHeaders.FILENAME) && parametersMap.containsKey(RequestHeaders.USERID))) {
            throw new ArtifactDownloadException("Request missing essential parameters: "
                    + RequestHeaders.FILENAME.getParameterName() + ", " + RequestHeaders.USERID.getParameterName());
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Parameters map received in request: " + parametersMap);
        }
        return parametersMap;
    }

    private EnumMap<RequestHeaders, String> populateMapFromPathInfo() {
        EnumMap<RequestHeaders, String> parametersMap = new EnumMap<>(RequestHeaders.class);
        String[] pathItems = getPathItems();
        if (pathItems.length >= 2 && pathItems.length <= 3) {
            if (pathItems.length == 3) {
                parametersMap.put(RequestHeaders.USERID, pathItems[0].trim());
                parametersMap.put(RequestHeaders.APPLICATIONFOLDER, pathItems[1].trim());
                parametersMap.put(RequestHeaders.FILENAME, pathItems[2].trim());
            }
            if (pathItems.length == 2) {
                parametersMap.put(RequestHeaders.USERID, pathItems[0].trim());
                parametersMap.put(RequestHeaders.FILENAME, pathItems[1].trim());
            }
        } else {
            throw new ArtifactDownloadException("Invalid path: " + this.requestPathInfo);
        }
        return parametersMap;
    }

    private String[] getPathItems() {
        if (StringUtils.isBlank(requestPathInfo) || requestPathInfo.length() < 4) {
            throw new ArtifactDownloadException("Artifact path is null or empty");
        }
        String pathInfo = this.requestPathInfo.substring(requestPathInfo.indexOf('/') + 1);
        return pathInfo.split("/");
    }

}
