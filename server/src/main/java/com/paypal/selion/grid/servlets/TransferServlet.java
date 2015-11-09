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

package com.paypal.selion.grid.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.selion.grid.servlets.transfer.ArtifactDownloadException;
import com.paypal.selion.grid.servlets.transfer.ArtifactUploadException;
import com.paypal.selion.grid.servlets.transfer.DefaultManagedArtifact;
import com.paypal.selion.grid.servlets.transfer.DownloadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.DownloadResponder;
import com.paypal.selion.grid.servlets.transfer.TransferContext;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.AbstractUploadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.ApplicationUploadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.MultipartUploadRequestProcessor;
import com.paypal.selion.grid.servlets.transfer.UploadResponder;
import com.paypal.selion.grid.servlets.transfer.UploadResponder.AbstractUploadResponder;
import com.paypal.selion.grid.servlets.transfer.UploadResponder.AcceptHeaderEnum;
import com.paypal.selion.grid.servlets.transfer.UploadResponder.JsonUploadResponder;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * <code>TransferServlet</code> is used for processing HTTP POST upload requests to SeLion grid. The artifacts are
 * uploaded using POST HTTP method call. The response of the POST HTTP method call contains the necessary HTTP GET url
 * used for downloading the artifact.
 */
public class TransferServlet extends HttpServlet {

    private static final long serialVersionUID = -4598713481663637719L;
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(TransferServlet.class);

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        LOGGER.entering((Object)new Object[] { httpServletRequest, httpServletResponse });
        try {
            TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
            UploadRequestProcessor requestProcessor = getUploadRequestProcessor(transferContext);
            transferContext.setUploadRequestProcessor(requestProcessor);
            UploadResponder uploadResponder = getUploadResponder(transferContext);
            uploadResponder.respond();
        } catch (ArtifactUploadException exe) {

            /*
             * Catching RuntimeException because UploadResponder some times throws IOException wrapped in
             * ArtifactUploadException and this IOException should be thrown back as IOException defined by the Servlet
             * API.
             */
            handleExceptions(exe);
        }
        LOGGER.exiting();
    }

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        LOGGER.entering((Object)new Object[] { httpServletRequest, httpServletResponse });
        try {
            TransferContext transferContext = new TransferContext(httpServletRequest, httpServletResponse);
            DownloadRequestProcessor downloadRequestProcessor = new DownloadRequestProcessor();
            transferContext.setDownloadRequestProcessor(downloadRequestProcessor);
            DownloadResponder downloadResponder = new DownloadResponder(transferContext);
            downloadResponder.respond();
        } catch (ArtifactDownloadException exe) {

            /*
             * Catching RuntimeException because DownloadResponder some times throws IOException wrapped in
             * ArtifactDownloadException and this IOException should be thrown back as IOException defined by the
             * Servlet API.
             */
            handleExceptions(exe);
        }
        LOGGER.exiting();
    }

    private void handleExceptions(Exception exe) throws IOException, ServletException {
        if (exe.getCause() instanceof IOException) {
            throw (IOException) exe.getCause();
        } else {
            throw new ServletException(exe.getMessage());
        }
    }

    /**
     * Returns a {@link AbstractUploadRequestProcessor} for {@link DefaultManagedArtifact}
     * 
     * @param transferContext
     *            Instance of {@link TransferContext}
     * @return Instance of {@link UploadRequestProcessor}.
     */
    private UploadRequestProcessor getUploadRequestProcessor(TransferContext transferContext) {
        LOGGER.entering(transferContext);
        String contentType = transferContext.getHttpServletRequest().getContentType() != null ? transferContext
                .getHttpServletRequest().getContentType().toLowerCase() : "unknown";
        if (contentType.contains(AbstractUploadRequestProcessor.MULTIPART_CONTENT_TYPE)) {

            // Return a Multipart request processor
            UploadRequestProcessor uploadRequestProcessor = new MultipartUploadRequestProcessor(
                    transferContext);
            LOGGER.exiting(uploadRequestProcessor);
            return uploadRequestProcessor;
        }
        if (contentType.contains(AbstractUploadRequestProcessor.APPLICATION_URLENCODED_CONTENT_TYPE)) {

            // Return normal Urlencoded request processor
            UploadRequestProcessor uploadRequestProcessor = new ApplicationUploadRequestProcessor(
                    transferContext);
            LOGGER.exiting(uploadRequestProcessor);
            return uploadRequestProcessor;
        }
        throw new ArtifactUploadException("Content-Type should be either: "
                + AbstractUploadRequestProcessor.MULTIPART_CONTENT_TYPE + " or: "
                + AbstractUploadRequestProcessor.APPLICATION_URLENCODED_CONTENT_TYPE + " for file uploads");
    }

    /**
     * Returns a {@link AbstractUploadResponder} depending on the Accept header received in {@link HttpServletRequest}.
     * If there is no matching {@link AbstractUploadResponder} then return {@link JsonUploadResponder} as the default
     * implementation.
     * 
     * @param transferContext
     *            Instance of {@link TransferContext}
     * @return Instance of {@link AbstractUploadResponder}.
     */
    private UploadResponder getUploadResponder(TransferContext transferContext) {
        LOGGER.entering(transferContext);
        UploadResponder uploadResponder;
        Class<? extends UploadResponder> uploadResponderClass = getResponderClass(transferContext
                .getHttpServletRequest().getHeader("accept"));
        try {
            uploadResponder = (UploadResponder) uploadResponderClass.getConstructor(new Class[] { TransferContext.class }).newInstance(
                    new Object[] { transferContext });
            LOGGER.exiting(uploadResponder);
            return uploadResponder;
        } catch (Exception e) {
            // We cannot do any meaningful operation to handle this; catching exception and returning
            // default responder
            uploadResponder = new JsonUploadResponder(transferContext);
            LOGGER.exiting(uploadResponder);
            return uploadResponder;
        }
    }

    private Class<? extends UploadResponder> getResponderClass(String headerString) {
        String[] headers = headerString.split(";");
        List<String> headerPrecedenceList = Arrays.asList(headers);
        Collections.reverse(headerPrecedenceList);
        for (String header : headerPrecedenceList) {
            try {
                AcceptHeaderEnum acceptHeaderEnum = AcceptHeaderEnum.getAcceptHeaderEnum(header);
                return acceptHeaderEnum.getUploadResponder();
            } catch (ArtifactUploadException exe) {
                // Exception is thrown if there is no implementation; just ignore and iterate until success
            }
        }
        return JsonUploadResponder.class;
    }

}