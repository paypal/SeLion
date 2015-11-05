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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.grid.servlets.transfer.ManagedArtifact.RequestParameters;
import com.paypal.selion.grid.servlets.transfer.UploadedArtifact.UploadedArtifactBuilder;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

public interface UploadRequestProcessor {

    /**
     * Content Type for multipart form-data request.
     */
    String MULTIPART_CONTENT_TYPE = "multipart/form-data";

    /**
     * Content Type for application form-url-encoded request.
     */
    String APPLICATION_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    /**
     * Max file size configuration property retrieved from SeLionConfig file.
     */
    String MAX_FILE_CONFIG_PROPERTY = "artifactMaxFileSize";

    /**
     * @return a {@link List} of {@link ManagedArtifact} which represent items on the {@link ManagedArtifactRepository}
     */
    List<ManagedArtifact> getUploadedData();

    /**
     * <code>AbstractUploadRequestProcessor</code> is abstract super class for concrete implementations that work on
     * types of {@link ManagedArtifact}. The class initializes a {@link ServerRepository} of {@link ManagedArtifact} to
     * use during processing.
     */
    abstract class AbstractUploadRequestProcessor implements UploadRequestProcessor {

        private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(AbstractUploadRequestProcessor.class);

        /**
         * Maximum size permitted for a single upload artifact.
         */
        public final int MAX_FILE_SIZE;

        protected TransferContext transferContext;

        protected HttpServletRequest httpServletRequest;

        protected ServerRepository repository;

        protected List<ManagedArtifact> managedArtifactList;

        protected RequestParameters managedArtifactRequestParameters;

        private ManagedArtifact instance;

        protected AbstractUploadRequestProcessor(TransferContext transferContext) {
            super();
            MAX_FILE_SIZE = ConfigParser.parse().getInt(MAX_FILE_CONFIG_PROPERTY);
            this.transferContext = transferContext;
            this.httpServletRequest = transferContext.getHttpServletRequest();
            repository = ManagedArtifactRepository.getInstance();
            managedArtifactRequestParameters = getManagedArtifactInstance().getRequestParameters();
            managedArtifactList = new ArrayList<>();
        }

        public List<ManagedArtifact> getUploadedData() {
            LOGGER.entering();
            SeLionGridLogger.getLogger(AbstractUploadRequestProcessor.class).entering();
            if (managedArtifactList.isEmpty()) {
                populateManagedArtifactList();
            }
            SeLionGridLogger.getLogger(AbstractUploadRequestProcessor.class).exiting(managedArtifactList);
            LOGGER.exiting(managedArtifactList);
            return managedArtifactList;
        }

        protected ManagedArtifact getManagedArtifactInstance() {
            if ((instance == null) && (repository != null)) {
                try {
                    instance = repository.getConfiguredManagedArtifactClass().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ArtifactUploadException(e.getCause().getMessage(), e);
                }
            }
            return instance;
        }

        protected UploadedArtifact createUploadedArtifactUsing(Map<String, String> headerMap,
                byte[] contents) {
            UploadedArtifactBuilder uploadedArtifactBuilder = new UploadedArtifactBuilder(contents);

            Map<String, Boolean> artifactParams = managedArtifactRequestParameters.getParameters();
            Map<String, String> meta = new HashMap<>();
            for (String inboundHeader : headerMap.keySet()) {
                if (artifactParams.containsKey(inboundHeader)) {
                    meta.put(inboundHeader, headerMap.get(inboundHeader));
                }
            }
            uploadedArtifactBuilder.withMetaInfo(meta);
            return uploadedArtifactBuilder.build();
        }

        protected Map<String, String> getRequestHeadersMap() {
            Map<String, String> headersMap = new HashMap<>();
            Map<String, Boolean> artifactParams = managedArtifactRequestParameters.getParameters();
            for (String header : artifactParams.keySet()) {
                String value = httpServletRequest.getHeader(header);
                if (!StringUtils.isBlank(value)) {
                    headersMap.put(header, value);
                }
            }
            return headersMap;
        }

        protected abstract void populateManagedArtifactList();

    }

    /**
     * <code>ApplicationUploadRequestProcessor</code> is an implementation of {@link AbstractUploadRequestProcessor} for
     * {@link ManagedArtifact}s. The implementation is native using streams for parsing
     * 'application/x-www-form-urlencoded' type requests. Artifact upload are saved into repository and returned as a
     * {@link List} after processing. Since the file name may not be deduced from such requests the clients MUST pass
     * the HTTP header 'fileName'. HTTP header 'folderName' is optional parameter. Additional HTTP headers may apply and
     * are defined by the {@link ManagedArtifact} implementation.
     * 
     * Sample curl command for uploading a form-urlencoded file
     * 
     * <pre>
     * {@code
     * curl -v -H 'filename:<fileName>' --data-binary @/path/tofile http://[hostname]:[port]/[upload-context-path] 
     * curl -v -H 'filename:<fileName>' -H 'folderName:<folderName>' --data-binary @/path/tofile http://[hostname]:[port]/[upload-context-path]
     * }
     * </pre>
     */
    final class ApplicationUploadRequestProcessor extends AbstractUploadRequestProcessor {

        private static final SeLionGridLogger LOGGER = SeLionGridLogger
                .getLogger(ApplicationUploadRequestProcessor.class);

        public ApplicationUploadRequestProcessor(TransferContext transferContext) {
            super(transferContext);
        }

        public void populateManagedArtifactList() {
            LOGGER.entering();
            try {
                saveUploadedData();
            } catch (IOException e) {
                throw new ArtifactUploadException("IOException in parsing file contents", e.getCause());
            }
            LOGGER.exiting();
        }

        private void saveUploadedData() throws IOException {
            populateHeadersMap();
            byte[] contents = parseFileContents();

            UploadedArtifact uploadedArtifact = createUploadedArtifactUsing(transferContext.getHeadersMap(), contents);
            ManagedArtifact managedArtifact = repository.saveContents(uploadedArtifact);
            managedArtifactList.add(managedArtifact);
        }

        private void populateHeadersMap() {
            checkRequiredParameters();
            transferContext.setHeadersMap(getRequestHeadersMap());
        }

        private void checkRequiredParameters() {
            if (StringUtils.isBlank(httpServletRequest.getHeader(ManagedArtifact.ARTIFACT_FILE_NAME))) {
                throw new ArtifactUploadException("Required header [" + ManagedArtifact.ARTIFACT_FILE_NAME
                        + "] is missing or has no value");
            }

            for (String param : managedArtifactRequestParameters.getParameters().keySet()) {
                boolean isRequired = managedArtifactRequestParameters.isRequired(param);
                if (isRequired && StringUtils.isBlank(httpServletRequest.getHeader(param))) {
                    throw new ArtifactUploadException("Required header [" + param + "] is missing or has no value");
                }
            }
        }

        private byte[] parseFileContents() throws IOException {
            int fileSize = httpServletRequest.getContentLength();
            if (fileSize <= 0) {
                throw new ArtifactUploadException("File is empty");
            }
            return IOUtils.toByteArray(httpServletRequest.getInputStream());
        }

    }

    /**
     * <code>MultipartUploadRequestProcessor</code> is an implementation of {@link AbstractUploadRequestProcessor} for
     * {@link DefaultManagedArtifact}. The implementation relies on 'commons-fileupload' library for parsing
     * 'multipart/form-data' type requests. Multiple artifact uploads are saved into repository and returned as a
     * {@link List} after processing. The clients pass 'folderName' is an optional parameter. The clients may choose to
     * pass them as either HTTP headers or request parameters: if using CURL then -F option (name=value) pair or -H
     * (HTTP headers). Additional HTTP headers or request parameters may apply and are defined by the
     * {@link ManagedArtifact} implementation. The implementation limits to only one file upload. Sample curl command
     * for uploading a multipart file
     * 
     * <pre>
     * {@code
     * curl -v -H 'folderName:<folderName>' -F file=@/path/tofile http://[hostname]:[port]/[upload-context-path]
     * }
     * </pre>
     */
    final class MultipartUploadRequestProcessor extends AbstractUploadRequestProcessor {

        private static final SeLionGridLogger LOGGER = SeLionGridLogger
                .getLogger(MultipartUploadRequestProcessor.class);

        private ServletFileUpload servletFileUpload;

        private List<FileItem> fileItems;

        public MultipartUploadRequestProcessor(TransferContext transferContext) {
            super(transferContext);
            initializeApacheCommonsSystem();
        }

        public void populateManagedArtifactList() {
            LOGGER.entering();
            try {
                saveUploadedData();
            } catch (FileUploadException e) {
                throw new ArtifactUploadException(e.getMessage());
            }
            LOGGER.exiting();
        }

        private void saveUploadedData() throws FileUploadException {
            LOGGER.entering();
            int count = parseRequestAsFileItems();
            if (count > 1) {
                throw new ArtifactUploadException("Only one file supported for upload using multipart");
            }

            // Get parameters from headers and override it with request parameters.
            populateHeadersMap();
            for (FileItem fileItem : fileItems) {
                if (!fileItem.isFormField()) {
                    UploadedArtifact uploadedArtifact = createUploadedArtifactUsing(transferContext.getHeadersMap(),
                            fileItem.get());
                    ManagedArtifact managedArtifact = repository.saveContents(uploadedArtifact);
                    managedArtifactList.add(managedArtifact);
                }
            }
            LOGGER.exiting();
        }

        private int parseRequestAsFileItems() throws FileUploadException {
            int fileCount = 0;
            if (fileItems == null) {
                fileItems = servletFileUpload.parseRequest(httpServletRequest);
            }
            for (FileItem fileItem : fileItems) {
                if (!fileItem.isFormField()) {
                    ++fileCount;
                }
            }
            return fileCount;
        }

        private void populateHeadersMap() {
            Map<String, String> headersMap = getRequestHeadersMap();
            Map<String, Boolean> artifactParams = managedArtifactRequestParameters.getParameters();
            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()) {
                    String parameter = fileItem.getFieldName().trim();
                    if (artifactParams.containsKey(parameter)) {
                        headersMap.put(parameter, fileItem.getString().trim());
                    }
                } else {
                    // TODO fix assumption that the only other parameter is the fileName
                    headersMap.put(ManagedArtifact.ARTIFACT_FILE_NAME, isNotBlank(fileItem.getName().trim()));
                }
            }
            checkForRequiredParameters(headersMap);
            transferContext.setHeadersMap(headersMap);
        }

        // TODO See if this can be merged with ApplicationUploadRequestProcessor#checkRequiredParameters
        private void checkForRequiredParameters(Map<String, String> headersMap) {
            if (!headersMap.containsKey(ManagedArtifact.ARTIFACT_FILE_NAME)) {
                throw new ArtifactUploadException("Required input ["
                        + ManagedArtifact.ARTIFACT_FILE_NAME + "] is missing or has no value");
            }

            for (String param : managedArtifactRequestParameters.getParameters().keySet()) {
                boolean isRequired = managedArtifactRequestParameters.isRequired(param);
                if (isRequired && StringUtils.isBlank(headersMap.get(param))) {
                    throw new ArtifactUploadException("Required input [" + param
                            + "] is missing or has no value");
                }
            }
        }

        private String isNotBlank(String fileName) {
            if (StringUtils.isBlank(fileName)) {
                throw new ArtifactUploadException("File name is empty in multipart upload request");
            }
            return fileName;
        }

        private void initializeApacheCommonsSystem() {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            diskFileItemFactory.setSizeThreshold(MAX_FILE_SIZE);
            servletFileUpload = new ServletFileUpload(diskFileItemFactory);
            servletFileUpload.setFileSizeMax(MAX_FILE_SIZE);
        }

    }

}
