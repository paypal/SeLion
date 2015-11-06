package com.paypal.selion.grid.servlets.transfer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.paypal.selion.logging.SeLionGridLogger;
import org.apache.commons.io.FilenameUtils;

/**
 * <code>UploadResponder</code> responds to HTTP POST upload request for any type that extends {@link ManagedArtifact}.
 * The response Content-Type is decided by the implementations. The response URLs are REST styled formed after the user
 * Id folder and artifact name. Application folder is inserted if available.
 */
public interface UploadResponder {

    /**
     * Responds into {@link HttpServletResponse}.
     */
    void respond();

    /**
     * Enum holding the MIME types for HTTP Accept header and the corresponding class that implements the logic for
     * serving the respective content.
     * 
     */
    enum AcceptHeaderEnum {

        /**
         * Accept: application/json
         */
        APPLICATION_JSON("application/json", JsonUploadResponder.class),

        /**
         * Accept: text/plain
         */
        TEXT_PLAIN("text/plain", TextPlainUploadResponder.class);

        private String acceptHeader;

        private Class<? extends UploadResponder> uploadResponder;

        AcceptHeaderEnum(String acceptHeader, Class<? extends UploadResponder> uploadResponder) {
            this.acceptHeader = acceptHeader;
            this.uploadResponder = uploadResponder;
        }

        private static final Logger LOGGER = SeLionGridLogger.getLogger(AcceptHeaderEnum.class);

        public String getAcceptHeader() {
            return acceptHeader;
        }

        public Class<? extends UploadResponder> getUploadResponder() {
            return uploadResponder;
        }

        /**
         * Returns {@link AcceptHeaderEnum} for the given HTTP Accept header, or throws {@link ArtifactUploadException}
         * if there is no implementation for the Accept header or if the Accept header is null
         * 
         * @param acceptHeader
         *            Accept header of the HTTP method
         * @return Instance of {@link AcceptHeaderEnum}
         */
        public static AcceptHeaderEnum getAcceptHeaderEnum(String acceptHeader) {
            if (acceptHeader != null) {
                for (AcceptHeaderEnum acceptHeaderEnum : AcceptHeaderEnum.values()) {
                    if (acceptHeader.contains(acceptHeaderEnum.getAcceptHeader())) {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE, "Returning: " + acceptHeaderEnum.getClass().getSimpleName()
                                    + " for accept header: " + acceptHeader);
                        }
                        return acceptHeaderEnum;
                    }
                }
            }
            throw new ArtifactUploadException("No 'UploadResponder' found for AcceptHeader: " + acceptHeader);
        }
    }

    /**
     * <code>AbstractUploadResponder</code> is abstract super class for concrete implementations that work on types of
     * {@link ManagedArtifact}.
     */
    abstract class AbstractUploadResponder implements UploadResponder {

        protected final TransferContext transferContext;

        protected final String requestUrl;

        protected final Map<String, String> headersMap;

        protected final List<ManagedArtifact> managedArtifactList;

        protected ManagedArtifact managedArtifactUnderProcess;

        public AbstractUploadResponder(TransferContext transferContext) {
            super();
            this.transferContext = transferContext;
            this.requestUrl = this.transferContext.getHttpServletRequest().getRequestURL().toString();
            this.managedArtifactList = this.transferContext.getUploadRequestProcessor().getUploadedData();
            this.headersMap = this.transferContext.getHeadersMap();
        }

        public void respond() {
            if (transferContext.getUploadRequestProcessor().getUploadedData().size() <= 0) {
                throw new ArtifactUploadException("No files processed by request processor");
            }
            respondFromRequestProcessor();
        }

        protected void addArtifactPath(StringBuffer url) {
            File repoFolder = ManagedArtifactRepository.getInstance().getRepositoryFolder();
            String relPath = managedArtifactUnderProcess.getAbsolutePath().replace(repoFolder.getAbsolutePath(), "");
            relPath = FilenameUtils.normalize(relPath);
            relPath = FilenameUtils.separatorsToUnix(relPath);
            url.append(relPath);
        }

        protected abstract void respondFromRequestProcessor();
    }

    /**
     * <code>JsonUploadResponder</code> for {@link AbstractUploadResponder} which sends out application/json responses
     * to {@link HttpServletResponse}
     */
    final class JsonUploadResponder extends AbstractUploadResponder {

        public static final String CONTENT_TYPE_VALUE = AcceptHeaderEnum.APPLICATION_JSON.getAcceptHeader();

        private final Gson gson;

        private final JsonObject jsonResponse;

        private final JsonArray files;

        public JsonUploadResponder(TransferContext transferContext) {
            super(transferContext);
            gson = new GsonBuilder().disableHtmlEscaping().create();
            jsonResponse = new JsonObject();
            files = new JsonArray();
        }

        protected void respondFromRequestProcessor() {
            SeLionGridLogger.getLogger(JsonUploadResponder.class).entering();
            PrintWriter out;
            transferContext.getHttpServletResponse().setContentType(CONTENT_TYPE_VALUE);
            try {
                out = transferContext.getHttpServletResponse().getWriter();
                jsonResponse.add("files", files);
                for (ManagedArtifact managedArtifact : managedArtifactList) {
                    managedArtifactUnderProcess = managedArtifact;
                    processArtifact();
                }
                out.println(gson.toJson(jsonResponse));
                SeLionGridLogger.getLogger(JsonUploadResponder.class).exiting();
            } catch (IOException e) {
                throw new ArtifactUploadException("IOException in retrieving HttpServletResponse's Writer", e);
            }
        }

        private void processArtifact() {
            JsonObject file = new JsonObject();
            StringBuffer url = new StringBuffer(requestUrl);
            addArtifactPath(url);
            file.addProperty(ManagedArtifact.ARTIFACT_FILE_NAME, managedArtifactUnderProcess.getArtifactName());
            file.addProperty("url", url.toString());
            files.add(file);
        }

    }

    /**
     * <code>TextPlainUploadResponder</code> for {@link AbstractUploadResponder} which sends out text/plain responses to
     * {@link HttpServletResponse}
     */
    final class TextPlainUploadResponder extends AbstractUploadResponder {

        public static final String CONTENT_TYPE_VALUE = AcceptHeaderEnum.TEXT_PLAIN.getAcceptHeader();

        private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(TextPlainUploadResponder.class);

        private final StringBuffer textResponse;

        public TextPlainUploadResponder(TransferContext transferContext) {
            super(transferContext);
            textResponse = new StringBuffer();

        }

        protected void respondFromRequestProcessor() {
            LOGGER.entering();
            PrintWriter out;
            transferContext.getHttpServletResponse().setContentType(CONTENT_TYPE_VALUE);
            try {
                out = transferContext.getHttpServletResponse().getWriter();
                for (ManagedArtifact managedArtifact : managedArtifactList) {
                    managedArtifactUnderProcess = managedArtifact;
                    processArtifact();
                }
                out.println(textResponse.toString());
            } catch (IOException e) {
                throw new ArtifactUploadException("IOException in retrieving HttpServletResponse's Writer", e);
            }
            LOGGER.exiting();
        }

        private void processArtifact() {
            StringBuilder fileName = new StringBuilder();
            StringBuffer url = new StringBuffer(requestUrl);
            addArtifactPath(url);
            fileName.append(ManagedArtifact.ARTIFACT_FILE_NAME).append("=")
                    .append(managedArtifactUnderProcess.getArtifactName());
            textResponse.append(fileName.toString()).append(",url=").append(url.toString());
        }

    }
}
