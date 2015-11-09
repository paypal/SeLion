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

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * <code>UploadedArtifact</code> is data structure to hold uploaded information through HTTP POST method call. The class
 * stores the uploaded artifact name. The application folder name is the name of an optional folder for storing the
 * artifact. Stores the contents as a byte array.
 */
public class UploadedArtifact {

    private final Map<String, String> artifactMetaInfo;

    private final byte[] artifactContents;

    private UploadedArtifact(byte[] artifactContents) {
        super();
        this.artifactContents = artifactContents;
        this.artifactMetaInfo = new TreeMap<>();
    }

    /**
     * Returns the artifact name.
     * 
     * @return the artifactPartName
     */
    public String getArtifactName() {
        return artifactMetaInfo.get(ManagedArtifact.ARTIFACT_FILE_NAME);
    }

    /**
     * Returns the content of the artifact as a byte array.
     * 
     * @return the artifactContents
     */
    public byte[] getArtifactContents() {
        return Arrays.copyOf(artifactContents, artifactContents.length);
    }

    /**
     * Returns all of the meta info for the artifact
     * 
     * @return the artifact meta info as a {@link Map}
     */
    public Map<String, String> getMetaInfo() {
        return artifactMetaInfo;
    }

    /**
     * Returns the optional application folder name used for storing the artifact.
     * 
     * @return the artifactFolderName
     */
    public String getArtifactFolderName() {
        return artifactMetaInfo.get(ManagedArtifact.ARTIFACT_FOLDER_NAME);
    }

    public String toString() {
        return "[ Artifact Name: " + getArtifactName() +
                ", Artifact Folder: " + getArtifactFolderName() + " ]";
    }

    /**
     * <code>UploadedArtifactBuilder</code> is a builder for {@link UploadedArtifact}
     */
    public static class UploadedArtifactBuilder {

        private UploadedArtifact uploadedArtifact;

        /**
         * Create a {@link UploadedArtifact} with basic artifact name and artifact contents.
         * 
         * @param artifactName
         *            Artifact name
         * @param artifactContents
         *            Artifact contents
         */
        public UploadedArtifactBuilder(String artifactName, byte[] artifactContents) {
            this(artifactContents);
            this.withArtifactName(artifactName);
        }

        
        /**
         * Create a {@link UploadedArtifact} with basic artifact name and artifact contents.
         * 
         * @param artifactName
         *            Artifact name
         * @param folderName
         *            Artifact folder
         * @param artifactContents
         *            Artifact contents
         */
        public UploadedArtifactBuilder(String artifactName, String folderName, byte[] artifactContents) {
            this(artifactContents);
            this.withArtifactName(artifactName);
            this.withFolderName(folderName);
        }

        /**
         * Create a {@link UploadedArtifact} with basic artifact name and artifact contents.
         * 
         * @param artifactContents
         *            Artifact contents
         */
        public UploadedArtifactBuilder(byte[] artifactContents) {
            uploadedArtifact = new UploadedArtifact(artifactContents);
        }

        /**
         * Build with an artifact name
         * 
         * @param artifactName
         *            an artifact name
         * @return Instance of {@link UploadedArtifactBuilder}
         */
        public UploadedArtifactBuilder withArtifactName(String artifactName) {
            uploadedArtifact.artifactMetaInfo.put(ManagedArtifact.ARTIFACT_FILE_NAME, artifactName);
            return this;
        }

        /**
         * Build with a folder name
         * 
         * @param folderName
         *            a folder name
         * @return Instance of {@link UploadedArtifactBuilder}
         */
        public UploadedArtifactBuilder withFolderName(String folderName) {
            uploadedArtifact.artifactMetaInfo.put(ManagedArtifact.ARTIFACT_FOLDER_NAME, folderName);
            return this;
        }

        /**
         * Build with custom meta information
         * 
         * @param meta
         *            custom meta information in the form of a {@link Map}
         * @return Instance of {@link UploadedArtifactBuilder}
         */
        public UploadedArtifactBuilder withMetaInfo(Map<String, String> meta) {
            uploadedArtifact.artifactMetaInfo.putAll(meta);
            return this;
        }

        /**
         * Returns an instance of {@link UploadedArtifact}
         * 
         * @return Instance of {@link UploadedArtifact}
         */
        public UploadedArtifact build() {
            return uploadedArtifact;
        }
    }

}
