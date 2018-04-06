/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

import java.util.Map;

/**
 * <code>ManagedArtifact</code> represents a basic artifact successfully saved to SeLion grid by an HTTP POST method
 * call. All implementations MUST provide a default, no-argument constructor for the {@link ManagedArtifact}
 * implementation.
 */
public interface ManagedArtifact {
    String ARTIFACT_FILE_NAME = "fileName";
    String ARTIFACT_FOLDER_NAME = "folderName";

    /**
     * Provides access to and/or represents the HTTP request parameters used by the @{link {@link ManagedArtifact}}
     */
    interface RequestParameters {
        /**
         * Returns the input parameters that this artifact uses. Implementations <b>must</b> include
         * {@link ManagedArtifact#ARTIFACT_FILE_NAME}
         * 
         * @return the input parameters as a {@link Map} of {@link String}, {@link Boolean} where the string is the
         *         parameter name, and the boolean states if it is a required parameter.
         */
        Map<String, Boolean> getParameters();

        /**
         * Is the input parameter required
         * 
         * @param parameter
         *            the input parameter in question
         * @return <code>true</code> or </code>false</code>
         */
        boolean isRequired(String parameter);
    }

    /**
     * Initialize the artifact by it's file path.
     * 
     * @param absolutePath
     *            absolute path to the artifact. Must be in the {@link ServerRepository} path. For example:
     *            <code>/{serverRepository}/{artifactFolder}/{artifactName}</code>
     */
    void initFromPath(String absolutePath);

    /**
     * Initialize the artifact by an in-bound uploaded artifact
     * 
     * @param uploaded
     *            instance of {@link UploadedArtifact} which will contain the meta-info for the uploaded artifact
     */
    void initFromUploadedArtifact(UploadedArtifact uploaded);

    /**
     * Returns the artifact name.
     * 
     * @return Artifact name.
     */
    String getArtifactName();

    /**
     * Returns the contents of the artifact as a byte array. Called by the {@link DownloadResponder}
     * 
     * @return Contents as a byte array.
     */
    byte[] getArtifactContents();

    /**
     * Matches the artifact based on some path info. Called by the {@link ServerRepository} to find artifacts by a
     * relative, publicly exposed (via the download URL), path
     * 
     * @param pathInfo
     *            {@link String} path information to match against. For example:
     *            <code>/{artifactFolder}/{artifactName}</code>
     * @return true if there is a match, false otherwise.
     */
    boolean matchesPathInfo(String pathInfo);

    /**
     * Return the path of the artifact file
     * 
     * @return the absolute path to the artifact file. Must include the {@link ServerRepository} path. For example:
     *         <code>/{serverRepository}/{artifactFolder}/{artifactName}</code>
     */
    String getAbsolutePath();

    /**
     * Returns true if this {@link ManagedArtifact} has expired. Called by the {@link ServerRepository} to clean up
     * artifacts.
     * 
     * @return true if expired, false otherwise
     */
    boolean isExpired();

    /**
     * Returns the MIME content type for this {@link ManagedArtifact}
     * 
     * @return MIME content type
     */
    String getHttpContentType();

    /**
     * Returns the headers associated with this {@link ManagedArtifact}
     * 
     * @return Instance of {@link RequestParameters}
     */
    <T extends RequestParameters> T getRequestParameters();

}
