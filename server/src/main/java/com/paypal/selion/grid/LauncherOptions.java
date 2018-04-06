/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

/**
 * These options give a programmatic way to influence some of the behaviors of a {@link RunnableLauncher}. These
 * behaviors may or may not have a SeLion Grid dash argument that corresponds to them. Dash arguments override
 * {@link LauncherOptions}.
 */
public interface LauncherOptions {
    /**
     * Enable/Disable clean up of previously downloaded artifacts for subsequent calls to {@link FileDownloader}
     */
    <T extends LauncherOptions> T setFileDownloadCleanupOnInvocation(boolean val);

    /**
     * @return the configured state.
     */
    boolean isFileDownloadCleanupOnInvocation();

    /**
     * Enable/Disable download.json time stamp check. If enabled, subsequent calls to {@link FileDownloader} will
     * immediately return if the time stamp is unchanged.
     */
    <T extends LauncherOptions> T setFileDownloadCheckTimeStampOnInvocation(boolean val);

    /**
     * @return the configured state.
     */
    boolean isFileDownloadCheckTimeStampOnInvocation();

    /**
     * @return the selionConfig file to use
     */
    String getSeLionConfig();

    /**
     * Set the selionConfig file to use
     */
    <T extends LauncherOptions> T setSeLionConfig(String configFile);
}
