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

    public static final String DOWNLOAD_CLEANUP = "downloadCleanup";
    public static final String DOWNLOAD_CLEANUP_ARG = "-" + DOWNLOAD_CLEANUP;

    public static final String DOWNLOAD_TIMESTAMP_CHECK = "downloadTimeStampCheck";
    public static final String DOWNLOAD_TIMESTAMP_CHECK_ARG = "-" + DOWNLOAD_TIMESTAMP_CHECK;

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
        names = DOWNLOAD_CLEANUP_ARG,
        description = "<Boolean> : Enable/Disable clean up of previously downloaded artifacts within the same JVM process",
        hidden = true,
        arity = 1
    )
    protected Boolean downloadCleanup = true;

    /**
     * whether to disable timestamp checking on the download.json file within the JVM process
     */
    @Parameter(
        names = DOWNLOAD_TIMESTAMP_CHECK_ARG,
        description = "<Boolean> : Enable/Disable time stamp checks of the SeLion download.json file within the same JVM process",
        hidden = true,
        arity = 1
    )
    protected Boolean downloadTimeStampCheck = true;

    public boolean isFileDownloadCleanupOnInvocation() {
        return downloadCleanup != null ? downloadCleanup : true;
    }

    public <T extends LauncherOptions> T setFileDownloadCleanupOnInvocation(boolean val) {
        this.downloadCleanup = val;
        return (T) this;
    };

    public boolean isFileDownloadCheckTimeStampOnInvocation() {
        return downloadTimeStampCheck != null ? downloadTimeStampCheck : true;
    }

    public <T extends LauncherOptions> T setFileDownloadCheckTimeStampOnInvocation(boolean val) {
        this.downloadTimeStampCheck = val;
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

        downloadCleanup = other.isFileDownloadCleanupOnInvocation();
        downloadTimeStampCheck = other.isFileDownloadCheckTimeStampOnInvocation();
        if (StringUtils.isNotBlank(other.getSeLionConfig())) {
            selionConfig = other.getSeLionConfig();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LauncherConfiguration [selionConfig=");
        builder.append(selionConfig);
        builder.append(", downloadCleanup=");
        builder.append(downloadCleanup);
        builder.append(", downloadTimeStampCheck=");
        builder.append(downloadTimeStampCheck);
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
