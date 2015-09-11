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

import java.io.File;

/**
 * <code>ServerRepository</code> represents a repository for storing any artifacts of type {@link ManagedArtifact}
 * received by HTTP POST upload request. The artifacts are saved and returned as a type extending
 * {@link ManagedArtifact} which the implementations are free to implement.
 */
public interface ServerRepository {

    /**
     * Saves the {@link UploadedArtifact} and returns a repository managed artifact.
     * 
     * @param uploadedArtifact
     *            {@link UploadedArtifact} received by from the upload request.
     * @return the stored artifact.
     */
    ManagedArtifact saveContents(UploadedArtifact uploadedArtifact);

    /**
     * Returns the artifact if there is a matching artifact for the requested artifact.
     * 
     * @param pathInfo
     *            Path to artifact received in the HTTP request.
     * @return Returns the artifact if there is a matching artifact or throws an {@link ArtifactDownloadException}.
     */
    ManagedArtifact getArtifact(String pathInfo);

    /**
     * Returns the configured {@link ManagedArtifact} class
     * 
     * @return the {@link ManagedArtifact} class that the {@link ServerRepository} is managing
     */
    Class<? extends ManagedArtifact> getConfiguredManagedArtifactClass();

    /**
     * Returns the repository folder
     * 
     * @return the artifact repository folder as a {@link File}
     */
    File getRepositoryFolder();
}
