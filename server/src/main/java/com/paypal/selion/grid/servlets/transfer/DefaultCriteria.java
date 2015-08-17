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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.paypal.selion.grid.servlets.transfer.UploadRequestProcessor.RequestHeaders;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * {@link Criteria} to match a {@link DefaultManagedArtifact} uniquely. Criteria uses artifact name, user id and
 * application folder to uniquely identify a {@link DefaultManagedArtifact}. Parameters artifactName, userId and
 * applicationFolder match artifact name, folder name and parent folder name of some {@link DefaultManagedArtifact}
 * respectively.
 */
public final class DefaultCriteria implements Criteria {
    
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(DefaultCriteria.class);

    private String artifactName;

    private String userId;

    private String applicationFolder;

    public DefaultCriteria(EnumMap<RequestHeaders, String> parametersMap) {
        validateParametersMap(parametersMap);
        this.artifactName = parametersMap.get(RequestHeaders.FILENAME);
        this.userId = parametersMap.get(RequestHeaders.USERID);
        this.applicationFolder = parametersMap.get(RequestHeaders.APPLICATIONFOLDER);
    }

    private void validateParametersMap(EnumMap<RequestHeaders, String> parametersMap) {
        if (!parametersMap.containsKey(RequestHeaders.FILENAME)
                || !parametersMap.containsKey(RequestHeaders.USERID)) {
            throw new ArtifactDownloadException("Request missing essential parametes: "
                    + RequestHeaders.FILENAME.getParameterName() + ", " + RequestHeaders.USERID.getParameterName());
        }
    }

    public String getArtifactName() {
        return artifactName;
    }

    public String getUserId() {
        return userId;
    }

    public String getApplicationFolder() {
        return applicationFolder;
    }

    public Map<String, String> asMap() {
        LOGGER.entering();
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put(RequestHeaders.FILENAME.getParameterName(), getArtifactName());
        contentMap.put(RequestHeaders.USERID.getParameterName(), getUserId());
        if (!StringUtils.isBlank(getApplicationFolder())) {
            contentMap.put(RequestHeaders.APPLICATIONFOLDER.getParameterName(), getApplicationFolder());
        }
        LOGGER.exiting(contentMap);
        return contentMap;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultCriteria)) {
            return false;
        }
        DefaultCriteria otherCriteria = DefaultCriteria.class.cast(other);
        if (!getArtifactName().equals(otherCriteria.getArtifactName())) {
            return false;
        }
        if (!getUserId().equals(otherCriteria.getUserId())) {
            return false;
        }
        boolean equals = getApplicationFolder() == null ? otherCriteria.getApplicationFolder() == null
                : getApplicationFolder().equals(otherCriteria.getApplicationFolder());
        if (equals == false) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.getArtifactName().hashCode();
        result = 31 * result + this.getUserId().hashCode();
        result = 31 * result + (this.getApplicationFolder() != null ? this.getApplicationFolder().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[ artifactName: " + getArtifactName() + ", userId: " + getUserId() + ", applicationFolder: "
                + getApplicationFolder() != null ? getApplicationFolder() : "" + " ]";
    }
}