package com.paypal.selion.grid.servlets.transfer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.RequestHeaders;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * <code>UploadResponder</code> responds to HTTP POST upload request for any type that extends {@link ManagedArtifact}.
 * The response Content-Type is decided by the implementations. The response URLs are REST styled formed after the user
 * Id folder and artifact name. Application folder is inserted if available.
 * 
 * @param <T>
 *            Type that is a sub type of {@link ManagedArtifact}
 */
public interface UploadResponder<T extends ManagedArtifact> {

    /**
     * Responds into {@link HttpServletResponse}.
     */
    void respond();

    /**
     * Enum holding the MIME types for HTTP Accept header and the corresponding class that implements the logic for
     * serving the respective content.
     * 
     */
    public enum AcceptHeaderEnum {

        /**
         * Accept: application/json
         */
        APPLICATION_JSON("application/json", JsonUploadResponder.class),

        /**
         * Accept: text/plain
         */
        TEXT_PLAIN("text/plain", TextPlainUploadResponder.class);

        private String acceptHeader;

        private Class<? extends UploadResponder<ManagedArtifact>> uploadResponder;

        private AcceptHeaderEnum(String acceptHeader, Class<? extends UploadResponder<ManagedArtifact>> uploadResponder) {
            this.acceptHeader = acceptHeader;
            this.uploadResponder = uploadResponder;
        }

        private static final Logger logger = SeLionGridLogger.getLogger();

        public String getAcceptHeader() {
            return acceptHeader;
        }

        public Class<? extends UploadResponder<ManagedArtifact>> getUploadResponder() {
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
                    if (acceptHeader.indexOf(acceptHeaderEnum.getAcceptHeader()) > -1) {
                        if (logger.isLoggable(Level.FINE)) {
                            logger.log(Level.FINE, "Returning: " + acceptHeaderEnum.getClass().getSimpleName()
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
    public abstract class AbstractUploadResponder implements UploadResponder<ManagedArtifact> {

        protected final TransferContext transferContext;

        protected final String requestUrl;

        protected final EnumMap<RequestHeaders, String> headersMap;

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

        protected String getRequestUrl() {
            return transferContext.getHttpServletRequest().getRequestURL().toString();
        }

        protected void addArtifactParameters(StringBuffer url) {
            if (isApplicationFolderRequested()) {
                addUserIdAndApplicationFolder(url);
            } else {
                addUserId(url);
            }
            addFileName(url);
        }

        protected void addFileName(StringBuffer url) {
            if (headersMap.get(RequestHeaders.FILENAME).trim()
                    .equals(managedArtifactUnderProcess.getArtifactName().trim())) {
                url.append("/").append(managedArtifactUnderProcess.getArtifactName());
                return;
            }
            throw new ArtifactUploadException("Requested file name : " + headersMap.get(RequestHeaders.FILENAME)
                    + " does not match with artifact name: " + managedArtifactUnderProcess.getArtifactName());
        }

        protected void addUserIdAndApplicationFolder(StringBuffer url) {
            if (headersMap.get(RequestHeaders.USERID).trim()
                    .equals(managedArtifactUnderProcess.getParentFolderName().trim())
                    && headersMap.get(RequestHeaders.APPLICATIONFOLDER).trim()
                            .equals(managedArtifactUnderProcess.getFolderName().trim())) {
                url.append("/").append(managedArtifactUnderProcess.getParentFolderName());
                url.append("/").append(managedArtifactUnderProcess.getFolderName());
                return;
            }
            throw new ArtifactUploadException("Requested userId name : " + headersMap.get(RequestHeaders.USERID)
                    + " does not match with artifact userId: " + managedArtifactUnderProcess.getParentFolderName()
                    + " or application folder: " + headersMap.get(RequestHeaders.APPLICATIONFOLDER)
                    + " does not match with artifact application folder: "
                    + managedArtifactUnderProcess.getFolderName());
        }

        protected void addUserId(StringBuffer url) {
            if (headersMap.get(RequestHeaders.USERID).trim().equals(managedArtifactUnderProcess.getFolderName().trim())) {
                url.append("/").append(managedArtifactUnderProcess.getFolderName());
                return;
            }
            throw new ArtifactUploadException("Requested userId : " + headersMap.get(RequestHeaders.USERID)
                    + " does not match with artifact userId: " + managedArtifactUnderProcess.getFolderName());
        }

        protected boolean isApplicationFolderRequested() {
            return headersMap.containsKey(RequestHeaders.APPLICATIONFOLDER);
        }

        protected abstract void respondFromRequestProcessor();

    }

    /**
     * <code>JsonUploadResponder</code> for {@link AbstractUploadResponder} which sends out application/json responses
     * to {@link HttpServletResponse}
     */
    public final class JsonUploadResponder extends AbstractUploadResponder {

        public static final String CONTENT_TYPE_VALUE = AcceptHeaderEnum.APPLICATION_JSON.getAcceptHeader();

        private Gson gson = null;

        private JsonObject jsonResponse = null;

        private JsonArray files = null;

        public JsonUploadResponder(TransferContext transferContext) {
            super(transferContext);
            gson = new GsonBuilder().disableHtmlEscaping().create();
            jsonResponse = new JsonObject();
            files = new JsonArray();
        }

        protected void respondFromRequestProcessor() {
            SeLionGridLogger.entering();
            PrintWriter out = null;
            transferContext.getHttpServletResponse().setContentType(CONTENT_TYPE_VALUE);
            try {
                out = transferContext.getHttpServletResponse().getWriter();
                jsonResponse.add("files", files);
                for (ManagedArtifact managedArtifact : managedArtifactList) {
                    managedArtifactUnderProcess = managedArtifact;
                    processArtifact();
                }
                out.println(gson.toJson(jsonResponse));
                SeLionGridLogger.exiting();
            } catch (IOException e) {
                throw new ArtifactUploadException("IOException in retrieving HttpServletResponse's Writer", e);
            }
        }

        private void processArtifact() {
            JsonObject file = new JsonObject();
            StringBuffer url = new StringBuffer(requestUrl);
            addArtifactParameters(url);
            file.addProperty(RequestHeaders.FILENAME.getParameterName(), managedArtifactUnderProcess.getArtifactName());
            file.addProperty("url", url.toString());
            files.add(file);
        }

    }

    /**
     * <code>TextPlainUploadResponder</code> for {@link AbstractUploadResponder} which sends out text/plain responses to
     * {@link HttpServletResponse}
     */
    public final class TextPlainUploadResponder extends AbstractUploadResponder {

        public static final String CONTENT_TYPE_VALUE = AcceptHeaderEnum.TEXT_PLAIN.getAcceptHeader();

        private static final Logger logger = SeLionGridLogger.getLogger();

        private StringBuffer textResponse = null;

        public TextPlainUploadResponder(TransferContext transferContext) {
            super(transferContext);
            textResponse = new StringBuffer();

        }

        protected void respondFromRequestProcessor() {
            logger.entering(this.getClass().getName(), "respondFromRequestProcessor");
            PrintWriter out = null;
            transferContext.getHttpServletResponse().setContentType(CONTENT_TYPE_VALUE);
            try {
                out = transferContext.getHttpServletResponse().getWriter();
                for (ManagedArtifact managedArtifact : managedArtifactList) {
                    managedArtifactUnderProcess = managedArtifact;
                    processArtifact();
                }
                out.println(textResponse.toString());
                logger.exiting(this.getClass().getName(), "respondFromRequestProcessor");
            } catch (IOException e) {
                throw new ArtifactUploadException("IOException in retrieving HttpServletResponse's Writer", e);
            }
        }

        private void processArtifact() {
            StringBuffer fileName = new StringBuffer();
            StringBuffer url = new StringBuffer(requestUrl);
            addArtifactParameters(url);
            fileName.append(RequestHeaders.FILENAME.getParameterName()).append("=")
                    .append(managedArtifactUnderProcess.getArtifactName());
            textResponse.append(fileName.toString()).append(",url=").append(url.toString()).append(";");
        }

    }
}
