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

/**
 * <code>UploadedArtifact</code> is data structure to hold uploaded information through HTTP POST method call. The class
 * stores the uploaded artifact name, the user id which essentially is the folder name under which the artifact is
 * stored. The application folder name is the name of an optional folder name under the user id folder for storing the
 * artifact. Stores the contents as a byte array.
 */
public class UploadedArtifact {

    private String artifactPartName;

    private String userId;

    private String applicationFolderName;

    private byte[] artifactContents;

    private UploadedArtifact(String artifactPartName, byte[] artifactContents) {
        super();
        this.artifactPartName = artifactPartName;
        this.artifactContents = artifactContents;
    }

    /**
     * Returns the artifact name.
     * 
     * @return the artifactPartName
     */
    public String getArtifactPartName() {
        return artifactPartName;
    }

    /**
     * Returns the content of the artifact as a byte array.
     * 
     * @return the artifactContents
     */
    public byte[] getArtifactContents() {
        return artifactContents;
    }

    /**
     * Returns the User id which is essentially the name of a folder containing the artifact.
     * 
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the optional application folder name used for storing the artifact. Its a sub folder under the user id
     * folder.
     * 
     * @return the applicationFolderName
     */
    public String getApplicationFolderName() {
        return applicationFolderName;
    }

    public String toString() {
        return "[ Artifact Part Name: " + getArtifactPartName() + ", User Id: " + getUserId()
                + ((getApplicationFolderName() != null) ? (", Application Folder: " + getApplicationFolderName()) : "")
                + " ]";
    }

    /**
     * <code>UploadedArtifactBuilder</code> is a builder for {@link UploadedArtifact}
     */
    public static class UploadedArtifactBuilder {

        private UploadedArtifact uploadedArtifact;

        /**
         * Create a {@link UploadedArtifact} with basic artifact name and artifact contents.
         * 
         * @param artifactPartName
         *            Artifact name
         * @param artifactContents
         *            Artifact contents
         */
        public UploadedArtifactBuilder(String artifactPartName, byte[] artifactContents) {
            uploadedArtifact = new UploadedArtifact(artifactPartName, artifactContents);
        }

        /**
         * Build with user id.
         * 
         * @param userId
         *            User id.
         * @return Instance of {@link UploadedArtifactBuilder}
         */
        public UploadedArtifactBuilder withUserId(String userId) {
            uploadedArtifact.userId = userId;
            return this;
        }

        /**
         * Build with application folder name
         * 
         * @param applicationFolderName
         *            Application folder name
         * @return Instance of {@link UploadedArtifactBuilder}
         */
        public UploadedArtifactBuilder withApplicationFolderName(String applicationFolderName) {
            uploadedArtifact.applicationFolderName = applicationFolderName;
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
