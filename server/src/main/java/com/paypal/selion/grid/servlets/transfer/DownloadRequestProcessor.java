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

    protected ServerRepository serverRepository;
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(DownloadRequestProcessor.class);

    public DownloadRequestProcessor() {
        super();
        serverRepository = ManagedArtifactRepository.getInstance();
    }

    /**
     * Returns the managed artifact requested in the HTTP call.
     * 
     * @param pathInfo
     *            the path inferred from the GET HTTP URL.
     * @return the artifact.
     */
    public ManagedArtifact getArtifact(String pathInfo) {
        LOGGER.entering();
        ManagedArtifact managedArtifact = serverRepository.getArtifact(pathInfo);
        LOGGER.exiting(managedArtifact);
        return managedArtifact;
    }

}
