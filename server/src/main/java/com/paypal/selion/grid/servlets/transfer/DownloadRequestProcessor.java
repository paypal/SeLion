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

import com.paypal.selion.logging.SeLionGridLogger;

/**
 * <code>DownloadRequestProcessor</code> is a default implementation for HTTP GET download requests.
 */
public class DownloadRequestProcessor {

    protected ServerRepository<ManagedArtifact<Criteria>> serverRepository;
    private SeLionGridLogger logger = SeLionGridLogger.getLogger(DownloadRequestProcessor.class);

    public DownloadRequestProcessor() {
        super();
        serverRepository = ManagedArtifactRepository.getInstance();
    }

    /**
     * Verifies whether the artifact requested in the HTTP call is present.
     * 
     * @param pathInfo
     *            the path inferred from the GET HTTP URL.
     * @return A boolean indicating the presence of the artifact.
     */
    public boolean isArtifactPresent(String pathInfo) {
        logger.entering();
        boolean isPresentInRepository = serverRepository.isArtifactPresent(pathInfo);
        logger.exiting(isPresentInRepository);
        return isPresentInRepository;
    }

    /**
     * Returns the managed artifact requested in the HTTP call.
     * 
     * @param pathInfo
     *            the path inferred from the GET HTTP URL.
     * @return the artifact.
     */
    public ManagedArtifact<Criteria> getArtifact(String pathInfo) {
        logger.entering();
        ManagedArtifact<Criteria> managedArtifact = serverRepository.getArtifact(pathInfo);
        logger.exiting(managedArtifact);
        return managedArtifact;
    }

}
