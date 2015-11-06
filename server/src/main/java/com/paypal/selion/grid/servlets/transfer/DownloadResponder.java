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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.paypal.selion.logging.SeLionGridLogger;

/**
 * <code>DownloadResponder</code> is default implementation for responding to HTTP GET download requests. This class is
 * designed for REST style download URLs. The below snippet shows some examples
 * 
 * <pre>
 * {@code
 * URL = http://<server>:<port>/servletPath/<artifactName>
 * URL = http://<server>:<port>/servletPath/<applicationFolder>/<artifactName>
 * }
 * </pre>
 */
public class DownloadResponder {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(DownloadResponder.class);

    private final HttpServletResponse httpServletResponse;

    private final DownloadRequestProcessor downloadRequestProcessor;

    private final String pathInfo;

    private ManagedArtifact managedArtifact;

    private byte[] contents;

    public DownloadResponder(TransferContext transferContext) {
        super();
        this.httpServletResponse = transferContext.getHttpServletResponse();
        this.downloadRequestProcessor = transferContext.getDownloadRequestProcessor();
        this.pathInfo = transferContext.getHttpServletRequest().getPathInfo();
    }

    /**
     * Sends a response over the HTTP servlet for the current {@link TransferContext}.
     */
    public void respond() {
        LOGGER.entering();
        managedArtifact = downloadRequestProcessor.getArtifact(this.pathInfo);
        contents = managedArtifact.getArtifactContents();
        setResponseMetadata();
        try {
            IOUtils.copy(new ByteArrayInputStream(contents), httpServletResponse.getOutputStream());
        } catch (IOException e) {
            throw new ArtifactDownloadException("IOException in writing to servlet response", e);
        }
        LOGGER.exiting();
    }

    private void setResponseMetadata() {
        httpServletResponse.setContentType(managedArtifact.getHttpContentType());
        httpServletResponse.setContentLength(contents.length);
        httpServletResponse.setHeader(CONTENT_DISPOSITION, "attachment; filename=" + managedArtifact.getArtifactName());
    }
    
    @Override
    public String toString() {
        return "[ Class Name: " + getClass().getName() + ", Path Info: " + pathInfo + "]";
    }
}
