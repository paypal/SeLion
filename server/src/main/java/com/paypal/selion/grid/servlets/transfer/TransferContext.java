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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <code>TransferContext</code> acts as a data structure for holding upload and download information required for the
 * upload and download request and response factories. The also hold the intermediate data and or state between the
 * request and response phases.
 */
public class TransferContext {

    private final HttpServletRequest httpServletRequest;

    private final HttpServletResponse httpServletResponse;

    private Map<String, String> headersMap;

    private UploadRequestProcessor uploadRequestProcessor;

    private DownloadRequestProcessor downloadRequestProcessor;

    public TransferContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super();
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * Returns the {@link HttpServletRequest} associated with the server call.
     * 
     * @return Instance of {@link HttpServletRequest}
     */
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Returns the {@link HttpServletResponse} associated with the server call.
     * 
     * @return Instance of {@link HttpServletResponse}
     */
    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    /**
     * Returns the {@link UploadRequestProcessor} instance associated this upload HTTP request method call.
     * 
     * @return Instance of {@link UploadRequestProcessor}.
     */
    public UploadRequestProcessor getUploadRequestProcessor() {
        return uploadRequestProcessor;
    }

    /**
     * Sets the {@link UploadRequestProcessor} instance created for this upload HTTP request method call.
     * 
     * @param uploadRequestProcessor
     *            Instance of {@link UploadRequestProcessor}s
     */
    public void setUploadRequestProcessor(UploadRequestProcessor uploadRequestProcessor) {
        this.uploadRequestProcessor = uploadRequestProcessor;
    }

    /**
     * Returns the {@link DownloadRequestProcessor} instance associated for this download HTTP request method call.
     * 
     * @return Instance of {@link DownloadRequestProcessor}
     */
    public DownloadRequestProcessor getDownloadRequestProcessor() {
        return downloadRequestProcessor;
    }

    /**
     * Sets the {@link DownloadRequestProcessor} instance created for this download HTTP request method call.
     * 
     * @param downloadRequestProcessor
     *            the downloadRequestProcessor to set
     */
    public void setDownloadRequestProcessor(DownloadRequestProcessor downloadRequestProcessor) {
        this.downloadRequestProcessor = downloadRequestProcessor;
    }

    /**
     * Get the headers map of the transfer context
     * 
     * @return the headersMap
     */
    public Map<String, String> getHeadersMap() {
        return headersMap;
    }

    /**
     * Set the headers map for this transfer context
     * 
     * @param headersMap
     *            the headersMap to set
     */
    public void setHeadersMap(Map<String, String> headersMap) {
        this.headersMap = headersMap;
    }

}
