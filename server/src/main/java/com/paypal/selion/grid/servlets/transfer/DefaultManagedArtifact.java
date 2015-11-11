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
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;
import org.apache.commons.lang.SystemUtils;

/**
 * <code>DefaultManagedArtifact</code> represents an artifact that is successfully saved to SeLion grid by an HTTP POST
 * method call. This artifact mostly represents binary file types rather than text files. The MIME type for this
 * artifact is set to 'application/zip'. Expiration of the artifact is based on TTL (Time To Live) specified in
 * milliseconds The configuration is read from Grid configuration system.
 */
public class DefaultManagedArtifact implements ManagedArtifact {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(DefaultManagedArtifact.class);

    private static final String EXPIRY_CONFIG_PROPERTY = "artifactExpiryInMilliSec";

    private static final String HTTP_CONTENT_TYPE = "application/zip";

    private static final String REPO_ABSOLUTE_PATH =
            ManagedArtifactRepository.getInstance().getRepositoryFolder().getAbsolutePath();

    private File artifactFile;

    private String artifactName;

    private String subFolderName;

    private String uidFolderName;

    private byte[] contents;

    private static final long timeToLiveInMillis = ConfigParser.parse().getLong(EXPIRY_CONFIG_PROPERTY);

    static final RequestParameters managedArtifactRequestParameters = new DefaultRequestParameters();

    static final class DefaultRequestParameters implements RequestParameters {
        static final String UID = "uid";
        static final Map<String, Boolean> params = ImmutableMap.of(
            ARTIFACT_FILE_NAME, true,
            ARTIFACT_FOLDER_NAME, false,
            UID, true
        );

        public Map<String, Boolean> getParameters() {
            return params;
        }

        public boolean isRequired(String parameter) {
            if (getParameters().containsKey(parameter)) {
                return getParameters().get(parameter);
            }
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends RequestParameters> T getRequestParameters() {
        return (T) managedArtifactRequestParameters;
    }

    public DefaultManagedArtifact() {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Managed Artifact TTL configured in Grid to " + timeToLiveInMillis
                    + " milliseconds.");
        }
    }

    // package visible, this constructor is for test purposes only
    DefaultManagedArtifact(String pathName) {
        this();
        initFromPath(pathName);
    }

    public void initFromPath(String absolutePath) {
        // do not allow paths that are empty or try to break out of the repository folder
        Preconditions.checkArgument(StringUtils.isNotBlank(absolutePath), "Path can not be blank or null.");
        Preconditions.checkArgument(!StringUtils.contains(absolutePath, ".."), "Path can not contain '..'.");

        String filePath = FilenameUtils.normalize(absolutePath);
        Preconditions.checkArgument(StringUtils.contains(filePath, REPO_ABSOLUTE_PATH),
                "Path specified (" + filePath + ") is outside the server repository.");
        artifactFile = new File(filePath);
    }

    public void initFromUploadedArtifact(UploadedArtifact uploaded) {
        // do not allow uploads to specify a tree of subFolders
        Preconditions.checkArgument(!StringUtils.contains(uploaded.getArtifactFolderName(), "/"));
        Preconditions.checkArgument(!StringUtils.contains(uploaded.getArtifactFolderName(), "\\"));
        // do not allow a windows-like ':' either
        Preconditions.checkArgument(!StringUtils.contains(uploaded.getArtifactFolderName(), ":"));

        this.artifactName = uploaded.getArtifactName();
        this.uidFolderName = uploaded.getMetaInfo().get(DefaultRequestParameters.UID);
        this.subFolderName = uploaded.getArtifactFolderName();

        StringBuilder buffer = new StringBuilder();
        buffer.append(REPO_ABSOLUTE_PATH).append(SystemUtils.FILE_SEPARATOR);
        buffer.append(this.uidFolderName).append(SystemUtils.FILE_SEPARATOR);
        if (!StringUtils.isBlank(this.subFolderName)) {
            buffer.append(this.subFolderName).append(SystemUtils.FILE_SEPARATOR);
        }
        buffer.append(this.artifactName);
        initFromPath(FilenameUtils.normalize(buffer.toString()));
    }

    public String getArtifactName() {
        if (artifactName == null) {
            artifactName = artifactFile.getName();
        }
        return artifactName;
    }

    /**
     * Returns the optional sub folder for the artifact
     *
     * @return the folder name or <code>""</code> if not specified
     */
    String getSubFolderName() {
        if (subFolderName == null) {
            String relPath = getAbsolutePath().replace(REPO_ABSOLUTE_PATH, "");
            relPath = relPath.substring(relPath.indexOf(SystemUtils.FILE_SEPARATOR) + 1);
            String[] parts = relPath.split("[\\\\/]");
            subFolderName = ((parts.length < 3) || (StringUtils.isBlank(parts[1]))) ? "" : parts[1];
        }
        return subFolderName;
    }

    /**
     * Returns the parent folder for the artifact. This folder must be a uid for {@link DefaultManagedArtifact}s
     * 
     * @return the folder name or <code>""</code> if not specified
     */
    String getUIDFolderName() {
        if (uidFolderName == null) {
            String relPath = getAbsolutePath().replace(REPO_ABSOLUTE_PATH, "");
            relPath = relPath.substring(relPath.indexOf(SystemUtils.FILE_SEPARATOR) + 1);
            String[] parts = relPath.split("[\\\\/]");
            uidFolderName = StringUtils.isEmpty(parts[0]) ? "" : parts[0];
        }
        return uidFolderName;
    }

    public byte[] getArtifactContents() {
        if (contents == null) {
            readContents();
        }
        return Arrays.copyOf(contents, contents.length);
    }

    public boolean matchesPathInfo(String pathInfo) {
        LOGGER.entering(pathInfo);
        DefaultManagedArtifact request = new DefaultManagedArtifact(REPO_ABSOLUTE_PATH + pathInfo);
        boolean matches = this.equals(request);
        LOGGER.exiting(matches);
        return matches;
    }

    public boolean isExpired() {
        boolean expired = (System.currentTimeMillis() - artifactFile.lastModified()) > timeToLiveInMillis;
        if (expired) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Artifact: " + this.getArtifactName() + " expired, time(now): "
                        + FileTime.fromMillis(System.currentTimeMillis()) + ", created: "
                        + FileTime.fromMillis(artifactFile.lastModified()));
            }
        }
        return expired;
    }

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
        if (!getSubFolderName().equals(otherManagedArtifact.getSubFolderName())) {
            return false;
        }
        return getUIDFolderName().equals(otherManagedArtifact.getUIDFolderName());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getArtifactName().hashCode();
        result = 31 * result + getSubFolderName().hashCode();
        result = 31 * result + getUIDFolderName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[ Artifact Name: " + getArtifactName() + ", UID: "
                + getUIDFolderName() + ", Subfolder: " + getSubFolderName() + "]";
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

    public String getAbsolutePath() {
        return (artifactFile == null) ? "" : artifactFile.getAbsolutePath();
    }

}
