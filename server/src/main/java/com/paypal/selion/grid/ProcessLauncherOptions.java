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

import com.paypal.selion.SeLionConstants;

/**
 * {@link LauncherOptions} which apply to {@link RunnableLauncher}s which spawn a new process
 */
public interface ProcessLauncherOptions extends LauncherOptions {

    /**
     * Enable/Disable passing of the system properties for IEDriver, Chromedriver, and PhantomJS which establish the
     * binary paths.
     */
    <T extends ProcessLauncherOptions> T setIncludeWebDriverBinaryPaths(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeWebDriverBinaryPaths();

    /**
     * Enable/Disable forwarding of current Java System Properties to sub process.
     */
    <T extends ProcessLauncherOptions> T setIncludeJavaSystemProperties(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeJavaSystemProperties();

    /**
     * Enable/Disable scanning for jar files in the {@link SeLionConstants#SELION_HOME_DIR}.
     */
    <T extends ProcessLauncherOptions> T setIncludeJarsInSeLionHomeDir(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeJarsInSeLionHomeDir();

    /**
     * Enable/Disable forwarding of current Java CLASSPATH to sub process.
     */
    <T extends ProcessLauncherOptions> T setIncludeParentProcessClassPath(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeParentProcessClassPath();

    /**
     * Enable/Disable scanning for jar files in present working directory.
     */
    <T extends ProcessLauncherOptions> T setIncludeJarsInPresentWorkingDir(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeJarsInPresentWorkingDir();

    /**
     * Enable/Disable continuous restart.
     */
    <T extends ProcessLauncherOptions> T setContinuouslyRestart(boolean val);

    /**
     * @return the configured state.
     */
    boolean isContinuouslyRestart();

    /**
     * Enable/Disable setup of logging.properties file for the Java sub process AND passing the system property
     * <code>-Djava.util.logging.config.file</code>.
     */
    <T extends ProcessLauncherOptions> T setSetupLoggingForJavaSubProcess(boolean val);

    /**
     * @return the configured state.
     */
    boolean isSetupLoggingForJavaSubProcess();

    /**
     * Set the restart cycle when in milliseconds. Restart cycle is how often to check that the sub-process is still
     * running. Used when continuous restart is enabled.
     */
    <T extends ProcessLauncherOptions> T setRestartCycle(long val);

    /**
     * @return the configured restartCycle in milliseconds
     */
    long getRestartCycle();
}
