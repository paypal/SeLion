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


/**
 * <code>ServerRepository</code> represents a repository for storing any artifacts of type {@link ManagedArtifact}
 * received by HTTP POST upload request. The artifacts are saved and returned as a type extending
 * {@link ManagedArtifact} which the implementations are free to implement.
 * 
 * @param <T>
 *            Saved artifact type that is an extension of {@link ManagedArtifact}
 * @param <U>
 *            Extension of {@link Criteria} type for matching artifacts.
 */
public interface ServerRepository<T extends ManagedArtifact<Criteria>> {

    /**
     * Saves the {@link UploadedArtifact} and returns a repository managed artifact.
     * 
     * @param uploadedArtifact
     *            {@link UploadedArtifact} received by from the upload request.
     * @return Type of the stored artifact.
     */
    T saveContents(UploadedArtifact uploadedArtifact);

    /**
     * Returns true if there is a matching artifact for the requested artifact.
     * 
     * @param pathInfo
     *            Path to artifact received in the HTTP request.
     * @return True if if there is a matching artifact, or false otherwise.
     */
    boolean isArtifactPresent(String pathInfo);

    /**
     * Returns the artifact if there is a matching artifact for the requested artifact.
     * 
     * @param pathInfo
     *            Path to artifact received in the HTTP request.
     * @return Returns the artifact if there is a matching artifact, or throws an {@link ArtifactDownloadException}.
     */
    T getArtifact(String pathInfo);

}
