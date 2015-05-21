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

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.grid.servlets.transfer.UploadedArtifact.UploadedArtifactBuilder;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

public interface UploadRequestProcessor<T extends ManagedArtifact> {

    /**
     * Content Type for multipart form-data request.
     */
    public static final String MULTIPART_CONTENT_TYPE = "multipart/form-data";

    /**
     * Content Type for application form-url-encoded request.
     */
    public static final String APPLICATION_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";

    /**
     * Max file size configuration property retrieved from SeLionConfig file.
     */
    public static final String MAX_FILE_CONFIG_PROPERTY = "artifactMaxFileSize";

    /**
     * @return a {@link List} of {@link ManagedArtifact} which represent items on the {@link ManagedArtifactRepository}
     */
    List<ManagedArtifact> getUploadedData();

    /**
     * Enum for storing the valid HTTP headers/parameters for transferring to SeLion grid.
     */
    enum RequestHeaders {

        /**
         * File name HTTP header, header name is fileName
         */
        FILENAME("fileName"),

        /**
         * User id HTTP header, header name is userId
         */
        USERID("userId"),

        /**
         * Application folder in HTTP header, header name is applicationFolder
         */
        APPLICATIONFOLDER("applicationFolder");

        private String parameterName;

        private RequestHeaders(String parameterName) {
            this.parameterName = parameterName;
        }

        public String getParameterName() {
            return parameterName;
        }

        public static RequestHeaders getRequestHeader(String parameterName) {
            for (RequestHeaders requestHeader : RequestHeaders.values()) {
                if (requestHeader.getParameterName().equals(parameterName)) {
                    return requestHeader;
                }
            }
            throw new ArtifactDownloadException("Unknown parameter name [" + parameterName + "]");
        }
    }

    /**
     * <code>AbstractUploadRequestProcessor</code> is abstract super class for concrete implementations that work on
     * types of {@link ManagedArtifact}. The class initializes a {@link ServerRepository} of {@link ManagedArtifact} to
     * use during processing.
     */
    abstract class AbstractUploadRequestProcessor implements UploadRequestProcessor<ManagedArtifact> {

        /**
         * Maximum size permitted for a single upload artifact.
         */
        public final int MAX_FILE_SIZE;

        protected TransferContext transferContext;

        protected HttpServletRequest httpServletRequest = null;

        protected ServerRepository<? extends ManagedArtifact, ? extends Criteria> repository = null;

        protected List<ManagedArtifact> managedArtifactList = null;

        protected AbstractUploadRequestProcessor(TransferContext transferContext) {
            super();
            MAX_FILE_SIZE = ConfigParser.parse().getInt(MAX_FILE_CONFIG_PROPERTY);
            this.transferContext = transferContext;
            this.httpServletRequest = transferContext.getHttpServletRequest();
            repository = ManagedArtifactRepository.getInstance();
            managedArtifactList = new ArrayList<>();
        }

        public List<ManagedArtifact> getUploadedData() {
            SeLionGridLogger.getLogger(AbstractUploadRequestProcessor.class).entering();
            if (managedArtifactList.isEmpty()) {
                populateManagedArtifactList();
            }
            SeLionGridLogger.getLogger(AbstractUploadRequestProcessor.class).exiting(managedArtifactList);
            return managedArtifactList;
        }

        protected UploadedArtifact createUploadedArtifactUsing(EnumMap<RequestHeaders, String> headerMap,
                byte[] contents) {
            UploadedArtifactBuilder uploadedArtifactBuilder = new UploadedArtifactBuilder(
                    headerMap.get(RequestHeaders.FILENAME), contents).withUserId(headerMap.get(RequestHeaders.USERID));
            if (headerMap.containsKey(RequestHeaders.APPLICATIONFOLDER)) {
                uploadedArtifactBuilder.withApplicationFolderName(headerMap.get(RequestHeaders.APPLICATIONFOLDER));
            }
            UploadedArtifact uploadedArtifact = uploadedArtifactBuilder.build();
            return uploadedArtifact;
        }

        protected abstract void populateManagedArtifactList();

    }

    /**
     * <code>ApplicationUploadRequestProcessor</code> is an implementation of {@link AbstractUploadRequestProcessor} for
     * {@link ManagedArtifact}s. The implementation is native using streams for parsing
     * 'application/x-www-form-urlencoded' type requests. Artifact upload are saved into repository and returned as a
     * {@link List} after processing. Since the file name may not be deduced from such requests the clients MUST pass
     * the HTTP header 'filename'. The HTTP header 'userID' is also treated as mandatory and the client MUST pass it.
     * HTTP header 'applicationFolder' is optional parameter
     * 
     * Sample curl command for uploading a form-urlencoded file
     * 
     * <pre>
     * {@code
     * curl -v -H 'filename:<fileName>' -H 'userId:<userId>' --data-binary @/path/tofile http://[hostname]:[port]/[upload-context-path] 
     * curl -v -H 'filename:<fileName>' -H 'userId:<userId>' -H 'applicationFolder:<applicationFolder>' --data-binary @/path/tofile http://[hostname]:[port]/[upload-context-path]
     * }
     * </pre>
     */
    public final class ApplicationUploadRequestProcessor extends AbstractUploadRequestProcessor {

        public ApplicationUploadRequestProcessor(TransferContext transferContext) {
            super(transferContext);
        }

        public void populateManagedArtifactList() {
            try {
                saveUploadedData();
            } catch (IOException e) {
                throw new ArtifactUploadException("IOException in parsing file contents", e.getCause());
            }
        }

        private void saveUploadedData() throws IOException {
            populateHeadersMap();
            byte[] contents = parseFileContents();
            UploadedArtifact uploadedArtifact = createUploadedArtifactUsing(transferContext.getHeadersMap(), contents);
            ManagedArtifact managedArtifact = repository.saveContents(uploadedArtifact);
            managedArtifactList.add(managedArtifact);
        }

        private void populateHeadersMap() {
            EnumMap<RequestHeaders, String> headersMap = new EnumMap<>(RequestHeaders.class);
            checkRequiredParameters();
            headersMap.put(RequestHeaders.FILENAME,
                    httpServletRequest.getHeader(RequestHeaders.FILENAME.getParameterName()));
            headersMap.put(RequestHeaders.USERID,
                    httpServletRequest.getHeader(RequestHeaders.USERID.getParameterName()));
            if (!StringUtils.isBlank(httpServletRequest.getHeader(RequestHeaders.APPLICATIONFOLDER.getParameterName()))) {
                headersMap.put(RequestHeaders.APPLICATIONFOLDER,
                        httpServletRequest.getHeader(RequestHeaders.APPLICATIONFOLDER.getParameterName()));
            }
            transferContext.setHeadersMap(headersMap);
        }

        private void checkRequiredParameters() {
            if (StringUtils.isBlank(httpServletRequest.getHeader(RequestHeaders.FILENAME.getParameterName()))) {
                throw new ArtifactUploadException("Required header [" + RequestHeaders.FILENAME.getParameterName()
                        + "] is missing");
            }
            if (StringUtils.isBlank(httpServletRequest.getHeader(RequestHeaders.USERID.getParameterName()))) {
                throw new ArtifactUploadException("Required header [" + RequestHeaders.USERID.getParameterName()
                        + "] is missing");
            }
        }

        private byte[] parseFileContents() throws IOException {
            int fileSize = httpServletRequest.getContentLength();
            if (fileSize <= 0) {
                throw new ArtifactUploadException("File is empty");
            }
            byte[] bytes = IOUtils.toByteArray(httpServletRequest.getInputStream());
            return bytes;
        }

    }

    /**
     * <code>MultipartUploadRequestProcessor</code> is an implementation of {@link AbstractUploadRequestProcessor} for
     * {@link DefaultManagedArtifact}. The implementation relies on 'commons-fileupload' library for parsing
     * 'multipart/form-data' type requests. Multiple artifact uploads are saved into repository and returned as a
     * {@link List} after processing. The clients are expected to pass 'userId' as a mandatory parameter and
     * 'folderName' is an optional parameter. The clients may choose to pass the as either HTTP headers or request
     * parameters: if using CURL then -F option (name=value) pair or -H (HTTP headers). The implementation limits to
     * only one file upload. Sample curl command for uploading a multipart file
     * 
     * <pre>
     * {@code
     * curl -v curl -v -H 'userId:<userId>' -H 'applicationFolder:<applicationFolder>' -F file=@/path/tofile http://[hostname]:[port]/[upload-context-path]
     * curl -v curl -v -F userId=<userId> -F applicationFolder=<applicationFolder> -F file=@/path/tofile http://[hostname]:[port]/[upload-context-path]
     * }
     * </pre>
     */
    public final class MultipartUploadRequestProcessor extends AbstractUploadRequestProcessor {

        private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(MultipartUploadRequestProcessor.class);

        private ServletFileUpload servletFileUpload = null;

        private List<FileItem> fileItems = null;

        public MultipartUploadRequestProcessor(TransferContext transferContext) {
            super(transferContext);
            initializeApacheCommonsSystem();
        }

        public void populateManagedArtifactList() {
            try {
                saveUploadedData();
            } catch (FileUploadException e) {
                throw new ArtifactUploadException(e.getMessage());
            }
        }

        private void saveUploadedData() throws FileUploadException {
            LOGGER.entering(this.getClass().getName(), "saveUploadedData");
            int count = parseRequestAsFileItems();
            if (count > 1) {
                throw new ArtifactUploadException("Only one file supported for upload using multipart");
            }

            // Get parameters from headers and override it with request
            // parameters.
            populateHeadersMap();
            for (FileItem fileItem : fileItems) {
                if (!fileItem.isFormField()) {
                    UploadedArtifact uploadedArtifact = createUploadedArtifactUsing(transferContext.getHeadersMap(),
                            fileItem.get());
                    ManagedArtifact managedArtifact = repository.saveContents(uploadedArtifact);
                    managedArtifactList.add(managedArtifact);
                }
            }
            LOGGER.exiting(this.getClass().getName(), "saveUploadedData");
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
            EnumMap<RequestHeaders, String> headersMap = getMapFromHeaders();
            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()) {
                    headersMap.put(RequestHeaders.getRequestHeader(fileItem.getFieldName().trim()), fileItem
                            .getString().trim());
                } else {

                    // Assuming the only other parameter is the fileName
                    headersMap.put(RequestHeaders.FILENAME, isNotBlank(fileItem.getName().trim()));
                }
            }
            checkForRequiredParameters(headersMap);
            transferContext.setHeadersMap(headersMap);
        }

        private void checkForRequiredParameters(EnumMap<RequestHeaders, String> headersMap) {
            if (!headersMap.containsKey(RequestHeaders.FILENAME) || !headersMap.containsKey(RequestHeaders.USERID)) {
                throw new ArtifactUploadException("Required paremeter/header ["
                        + RequestHeaders.FILENAME.getParameterName() + ", " + RequestHeaders.USERID.getParameterName()
                        + "] is missing");
            }
        }

        private EnumMap<RequestHeaders, String> getMapFromHeaders() {
            EnumMap<RequestHeaders, String> headersMap = new EnumMap<>(RequestHeaders.class);
            if (!StringUtils.isBlank(httpServletRequest.getHeader(RequestHeaders.USERID.getParameterName()))) {
                headersMap.put(RequestHeaders.USERID,
                        httpServletRequest.getHeader(RequestHeaders.USERID.getParameterName()));
            }
            if (!StringUtils.isBlank(httpServletRequest.getHeader(RequestHeaders.APPLICATIONFOLDER.getParameterName()))) {
                headersMap.put(RequestHeaders.APPLICATIONFOLDER,
                        httpServletRequest.getHeader(RequestHeaders.APPLICATIONFOLDER.getParameterName()));
            }
            return headersMap;
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
