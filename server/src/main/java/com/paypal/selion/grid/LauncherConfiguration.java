/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

package com.paypal.selion.grid;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.Parameter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * SeLion Grid configuration options that influence some of the behaviors of a {@link RunnableLauncher}.
 */
@SuppressWarnings("unchecked")
public class LauncherConfiguration implements LauncherOptions {
    public static final String SELION_CONFIG = "selionConfig";
    public static final String SELION_CONFIG_ARG = "-" + SELION_CONFIG;

    public static final String NO_DOWNLOAD_CLEANUP = "noDownloadCleanup";
    public static final String NO_DOWNLOAD_CLEANUP_ARG = "-" + NO_DOWNLOAD_CLEANUP;

    public static final String NO_DOWNLOAD_TIMESTAMP_CHECK = "noDownloadTimeStampCheck";
    public static final String NO_DOWNLOAD_TIMESTAMP_CHECK_ARG = "-" + NO_DOWNLOAD_TIMESTAMP_CHECK;

    /**
     * the location of the SeLion Grid config file
     */
    @Parameter(
        names = SELION_CONFIG_ARG, 
        description = "<String> filename : A SeLion Grid configuration JSON file."
    )
    // transient because we don't want it to serialize
    protected transient String selionConfig = SeLionGridConstants.SELION_CONFIG_FILE;

    /**
     * whether to clean up previously downloaded artifact within the JVM process
     */
    @Parameter(
        names = NO_DOWNLOAD_CLEANUP_ARG, 
        description = "<Boolean> : Disable clean up of previously downloaded artifacts within the same JVM process", 
        hidden = true
    )
    protected Boolean noDownloadCleanup;

    /**
     * whether to disable timestamp checking on the download.json file within the JVM process
     */
    @Parameter(
        names = NO_DOWNLOAD_TIMESTAMP_CHECK_ARG, 
        description = "<Boolean> : Disable time stamp checks of the SeLion download.json file within the same JVM process",
        hidden = true
    )
    protected Boolean noDownloadTimeStampCheck;

    public boolean isFileDownloadCleanupOnInvocation() {
        return noDownloadCleanup != null ? !noDownloadCleanup : true;
    }

    public <T extends LauncherOptions> T setFileDownloadCleanupOnInvocation(boolean val) {
        this.noDownloadCleanup = !val;
        return (T) this;
    };

    public boolean isFileDownloadCheckTimeStampOnInvocation() {
        return noDownloadTimeStampCheck != null ? !noDownloadTimeStampCheck : true;
    }

    public <T extends LauncherOptions> T setFileDownloadCheckTimeStampOnInvocation(boolean val) {
        this.noDownloadTimeStampCheck = !val;
        return (T) this;
    }

    public String getSeLionConfig() {
        return StringUtils.isNotBlank(selionConfig) ?  selionConfig : SeLionGridConstants.SELION_CONFIG_FILE;
    }

    public <T extends LauncherOptions> T setSeLionConfig(String config) {
        this.selionConfig = config;
        return (T) this;
    }

    public void merge(LauncherOptions other) {
        if (other == null) {
            return;
        }

        noDownloadCleanup = !other.isFileDownloadCleanupOnInvocation();
        noDownloadTimeStampCheck = !other.isFileDownloadCheckTimeStampOnInvocation();
        if (StringUtils.isNotBlank(other.getSeLionConfig())) {
            selionConfig = other.getSeLionConfig();
        }
    }

    public void merge(LauncherConfiguration other) {
        if (other == null) {
            return;
        }

        if (other.noDownloadCleanup != null) {
            noDownloadCleanup = other.noDownloadCleanup;
        }
        if (other.noDownloadTimeStampCheck != null) {
            noDownloadTimeStampCheck = other.noDownloadTimeStampCheck;
        }
        if (StringUtils.isNotBlank(other.selionConfig)) {
            selionConfig = other.selionConfig;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LauncherConfiguration [selionConfig=");
        builder.append(selionConfig);
        builder.append(", noDownloadCleanup=");
        builder.append(noDownloadCleanup);
        builder.append(", noDownloadTimeStampCheck=");
        builder.append(noDownloadTimeStampCheck);
        builder.append("]");
        return builder.toString();
    }

    public JsonElement toJson() {
        return new GsonBuilder().serializeNulls().disableHtmlEscaping().create().toJsonTree(this);
    }

    public LauncherConfiguration fromJson(JsonElement json) {
        return new GsonBuilder().create().fromJson(json, LauncherConfiguration.class);
    }

    public LauncherConfiguration fromJson(String json) {
        return new GsonBuilder().create().fromJson(json, LauncherConfiguration.class);
    }

    public static LauncherConfiguration loadFromFile(String configFile) throws IOException {
        return new LauncherConfiguration().fromJson(FileUtils.readFileToString(new File(configFile), "UTF-8"));
    }
}
