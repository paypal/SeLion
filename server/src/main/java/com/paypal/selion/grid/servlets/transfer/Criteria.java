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

import java.util.Map;

/**
 * <code>Criteria</code> matches some {@link ManagedArtifact} primarily based on name and containing folder named as the
 * user id. Application folder can be optionally introduced between the user Id and the artifact to add a additional
 * layer of segregation. The implementation may populate the members from HTTP request headers or parameters when used
 * in a web context. Refer to {@link UploadRequestProcessor.RequestHeaders} for additional details.
 * 
 */
public interface Criteria {

    /**
     * Returns the artifact name.
     * 
     * @return Artifact name.
     */
    String getArtifactName();

    /**
     * Returns the optional application folder that can be used to store artifacts under the user Id folder.
     * 
     * @return Application folder name
     */
    String getApplicationFolder();

    /**
     * Returns the contents as a name value pairs that this {@link Criteria} is concerned for matching
     * 
     * @return Instance of {@link Map} containing the name and value
     */
    Map<String, String> asMap();

}
