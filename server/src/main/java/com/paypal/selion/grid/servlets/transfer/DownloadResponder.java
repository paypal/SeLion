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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.RequestHeaders;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

/**
 * <code>DownloadResponder</code> is default implementation for responding to HTTP GET download requests. This class is
 * designed for REST style download URLs. The below snippet shows some examples
 * 
 * <pre>
 * {@code
 * URL = http://<domain-name>:<port>/servletPath/<userId>/<artifactName>
 * URL = http://<domain-name>:<port>/servletPath/<userId>/<appliationFolder>/<artifactName>
 * }
 * </pre>
 */
public class DownloadResponder {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    private static final String CRITERIA_CONFIG_PROPERTY = "managedCriteria";

    private static final Logger logger = SeLionGridLogger.getLogger();

    private HttpServletResponse httpServletResponse;

    private DownloadRequestProcessor downloadRequestProcessor;

    private String pathInfo;

    private Criteria requestedCriteria;

    private ManagedArtifact managedArtifact;

    private byte[] contents;

    public DownloadResponder(TransferContext transferContext) {
        super();
        this.httpServletResponse = transferContext.getHttpServletResponse();
        this.downloadRequestProcessor = transferContext.getDownloadRequestProcessor();
        this.pathInfo = transferContext.getHttpServletRequest().getPathInfo();
    }

    public void respond() {
        SeLionGridLogger.entering();
        formCriteria();
        managedArtifact = downloadRequestProcessor.getArtifact(requestedCriteria);
        contents = managedArtifact.getArtifactContents();
        setResponseMetadata();
        try {
            IOUtils.copy(new ByteArrayInputStream(contents), httpServletResponse.getOutputStream());
        } catch (IOException e) {
            throw new ArtifactDownloadException("IOException in writing to servlet response", e);
        }
        SeLionGridLogger.exiting();
    }

    private Criteria formCriteria() {
        if (requestedCriteria == null) {
            EnumMap<RequestHeaders, String> parametersMap = getParametersMap();
            try {
                String criteriaClassName = ConfigParser.getInstance().getString(CRITERIA_CONFIG_PROPERTY);
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Criteria class name configured in grid: " + criteriaClassName);
                }
                @SuppressWarnings("unchecked")
                Class<? extends Criteria> criteriaClass = (Class<? extends Criteria>) this.getClass().getClassLoader()
                        .loadClass(criteriaClassName);
                requestedCriteria = criteriaClass.getConstructor(new Class[] { EnumMap.class }).newInstance(
                        new Object[] { parametersMap });
            } catch (InvocationTargetException exe) {
                throw new ArtifactDownloadException(exe.getCause().getMessage(), exe);
            } catch (Exception exe) {
                throw new ArtifactDownloadException(exe.getClass().getSimpleName() + " in creating Criteria: "
                        + ConfigParser.getInstance().getString(CRITERIA_CONFIG_PROPERTY), exe);
            }
        }
        return requestedCriteria;
    }

    private EnumMap<RequestHeaders, String> getParametersMap() {
        EnumMap<RequestHeaders, String> parametersMap = populateMapFromPathInfo();
        if (!(parametersMap.containsKey(RequestHeaders.FILENAME) && parametersMap.containsKey(RequestHeaders.USERID))) {
            throw new ArtifactDownloadException("Request missing essential parametes: "
                    + RequestHeaders.FILENAME.getParameterName() + ", " + RequestHeaders.USERID.getParameterName());
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Parametes map received in request: " + parametersMap);
        }
        return parametersMap;
    }

    private EnumMap<RequestHeaders, String> populateMapFromPathInfo() {
        EnumMap<RequestHeaders, String> parametersMap = new EnumMap<>(RequestHeaders.class);
        String[] pathItems = getPathItems();
        if (pathItems.length >= 2 && pathItems.length <= 3) {
            if (pathItems.length == 3) {
                parametersMap.put(RequestHeaders.USERID, pathItems[0].trim());
                parametersMap.put(RequestHeaders.APPLICATIONFOLDER, pathItems[1].trim());
                parametersMap.put(RequestHeaders.FILENAME, pathItems[2].trim());
            }
            if (pathItems.length == 2) {
                parametersMap.put(RequestHeaders.USERID, pathItems[0].trim());
                parametersMap.put(RequestHeaders.FILENAME, pathItems[1].trim());
            }
        } else {
            throw new ArtifactDownloadException("Invalid path: " + pathInfo);
        }
        return parametersMap;
    }

    private void setResponseMetadata() {
        httpServletResponse.setContentType(managedArtifact.getHttpContentType());
        httpServletResponse.setContentLength(contents.length);
        httpServletResponse.setHeader(CONTENT_DISPOSITION, "attachment; filename=" + managedArtifact.getArtifactName());
    }

    private String[] getPathItems() {
        if (StringUtils.isBlank(pathInfo) || pathInfo.length() < 4) {
            throw new ArtifactDownloadException("Artifact path is null or empty");
        }
        pathInfo = pathInfo.substring(pathInfo.indexOf('/') + 1);
        return pathInfo.split("/");
    }

    public String toString() {
        return "[ Class Name: " + getClass().getName() + ", Path Info: " + pathInfo + ", Criteria: " + formCriteria()
                + "]";
    }
}
