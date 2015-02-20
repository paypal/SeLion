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
 * <code>ManagedArtifact</code> represents a basic artifact successfully saved to SeLion grid by an HTTP POST method
 * call.
 * 
 */
public interface ManagedArtifact {

    /**
     * Returns the artifact name.
     * 
     * @return Artifact name.
     */
    String getArtifactName();

    /**
     * Returns the name of the folder this artifact is housed.
     * 
     * @return Folder name of this artifact
     */
    String getFolderName();

    /**
     * Returns the name of the parent folder (folder of the folder) for this artifact.
     * 
     * @return Folder name of the parent folder.
     */
    String getParentFolderName();

    /**
     * Returns the contents of the artifact as a byte array.
     * 
     * @return Contents as a byte array.
     */
    byte[] getArtifactContents();

    /**
     * Matches the artifact based on some {@link Criteria}
     * 
     * @param criteria
     *            Instance of {@link Criteria} to match.
     * @return true if there is a match, false otherwise.
     */
    <U extends Criteria> boolean matches(U criteria);

    /**
     * Returns true if this {@link ManagedArtifact} has expired.
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

}
