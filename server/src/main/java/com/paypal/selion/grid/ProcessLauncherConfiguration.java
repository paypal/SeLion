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

import com.beust.jcommander.Parameter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * SeLion Grid process configuration options which are shared across all {@link RunnableLauncher}s which spawn a new
 * process, usually a Java one.
 */
@SuppressWarnings("unchecked")
public class ProcessLauncherConfiguration extends LauncherConfiguration implements ProcessLauncherOptions {
    public static final String RESTART_CYCLE = "restartCycle";
    public static final String RESTART_CYCLE_ARG = "-" + RESTART_CYCLE;

    public static final String CONTINUOUS_RESTART = "continuousRestart";
    public static final String CONTINUOUS_RESTART_ARG = "-" + CONTINUOUS_RESTART;

    public static final String INCLUDE_WEB_DRIVER_BINARY_PATHS = "includeWebDriverBinaryPaths";
    public static final String INCLUDE_WEB_DRIVER_BINARY_PATHS_ARG = "-" + INCLUDE_WEB_DRIVER_BINARY_PATHS;

    public static final String INCLUDE_JAVA_SYSTEM_PROPERTIES = "includeJavaSystemProperties";
    public static final String INCLUDE_JAVA_SYSTEM_PROPERTIES_ARG = "-" + INCLUDE_JAVA_SYSTEM_PROPERTIES;

    public static final String INCLUDE_JARS_IN_SELION_HOME = "includeJarsInSeLionHome";
    public static final String INCLUDE_JARS_IN_SELION_HOME_ARG = "-" + INCLUDE_JARS_IN_SELION_HOME;

    public static final String INCLUDE_JARS_IN_PWD = "includeJarsInPWD";
    public static final String INCLUDE_JARS_IN_PWD_ARG = "-" + INCLUDE_JARS_IN_PWD;

    public static final String INCLUDE_PARENT_PROCESS_CLASSPATH = "includeParentProcessClassPath";
    public static final String INCLUDE_PARENT_PROCESS_CLASSPATH_ARG = "-" + INCLUDE_PARENT_PROCESS_CLASSPATH;

    public static final String SETUP_LOGGING_FOR_JAVA_SUB_PROCESS = "setupLoggingForJavaSubProcess";
    public static final String SETUP_LOGGING_FOR_JAVA_SUB_PROCESS_ARG = "-" + SETUP_LOGGING_FOR_JAVA_SUB_PROCESS;

    private static final Long DEFAULT_RESTART_CYCLE = 60000L;

    /**
     * how often SeLion Grid will check and if needed, attempt to restart a sub-process it has started
     */
    @Parameter(
        names = RESTART_CYCLE_ARG,
        description = "<Long> in ms : How often SeLion Grid will check the (hub/node/other) sub-process" +
            " is still running. If the sub-process is exited, it will be restarted when this cycle wakes up."
    )
    protected Long restartCycle = DEFAULT_RESTART_CYCLE;

    /**
     * if {@code true}, does not initiate a SeLion Grid {@link #restartCycle}
     */
    @Parameter(
        names = CONTINUOUS_RESTART_ARG,
        description = "<Boolean> : Enable/Disable continuous restart of the SeLion grid sub-process",
        arity = 1
    )
    protected Boolean continuousRestart = true;

    /**
     * whether to include -Dwebdriver.binary.path=path JVM arguments when invoking the sub-process
     */
    @Parameter(
        names = INCLUDE_WEB_DRIVER_BINARY_PATHS_ARG,
        description = "<Boolean> : Enable/Disable inclusion of Selenium system properties which point to the binary " +
            "paths of IEDriver, chromedriver, etc when invoking the sub-process",
        hidden = true,
        arity = 1
    )
    protected Boolean includeWebDriverBinaryPaths = true;

    /**
     * whether to include forwarding of current JVM system properties to the sub-process
     */
    @Parameter(
        names = INCLUDE_JAVA_SYSTEM_PROPERTIES_ARG,
        description = "<Boolean> : Enable/Disable forwarding of JVM (-D) system properties to the sub-process",
        hidden = true,
        arity = 1
    )
    protected Boolean includeJavaSystemProperties = true;

    /**
     * whether to include jars in the selionHome and add them to the CLASSPATH of the sub-process
     */
    @Parameter(
        names = INCLUDE_JARS_IN_SELION_HOME_ARG,
        description = "<Boolean> : Enable/Disable automatic addition of jars in the <selionHome> to the CLASSPATH " +
            "when invoking the sub-process",
        hidden = true,
        arity = 1
    )
    protected Boolean includeJarsInSeLionHome = true;

    /**
     * whether to include jars in the PWD and add them to the CLASSPATH of the sub-process
     */
    @Parameter(
        names = INCLUDE_JARS_IN_PWD_ARG,
        description = "<Boolean> : Enable/Disable automatic addition of jars in the present working directory to the " +
            "CLASSPATH when invoking the sub-process",
        hidden = true,
        arity = 1
    )
    protected Boolean includeJarsInPWD = true;

    /**
     * whether to include the parent process CLASSPATH and add it to the CLASSPATH of the sub-process
     */
    @Parameter(
        names = INCLUDE_PARENT_PROCESS_CLASSPATH_ARG,
        description = "<Boolean> : Enable/Disable forwarding of the parent process's CLASSPATH to the sub-process " +
            "when invoking it",
        hidden = true,
        arity = 1
    )
    protected Boolean includeParentProcessClassPath = true;

    /**
     * whether to automatically setup (and use) of a logging.properties file for the sub-process
     */
    @Parameter(
        names = SETUP_LOGGING_FOR_JAVA_SUB_PROCESS_ARG,
        description = "<Boolean> : Enable/Disable automatic setup (and use) of a logging.properties file for the " +
            "sub-process",
        hidden = true,
        arity = 1
    )
    protected Boolean setupLoggingForJavaSubProcess = true;

    public boolean isIncludeWebDriverBinaryPaths() {
        return includeWebDriverBinaryPaths != null ? includeWebDriverBinaryPaths : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeWebDriverBinaryPaths(boolean val) {
        this.includeWebDriverBinaryPaths = val;
        return (T) this;
    }

    public boolean isIncludeJavaSystemProperties() {
        return includeJavaSystemProperties != null ? includeJavaSystemProperties : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeJavaSystemProperties(boolean val) {
        this.includeJavaSystemProperties = val;
        return (T) this;
    }

    public boolean isIncludeJarsInSeLionHomeDir() {
        return includeJarsInSeLionHome != null ? includeJarsInSeLionHome : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeJarsInSeLionHomeDir(boolean val) {
        this.includeJarsInSeLionHome = val;
        return (T) this;
    }

    public boolean isIncludeParentProcessClassPath() {
        return includeParentProcessClassPath != null ? includeParentProcessClassPath : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeParentProcessClassPath(boolean val) {
        this.includeParentProcessClassPath = val;
        return (T) this;
    }

    public boolean isIncludeJarsInPresentWorkingDir() {
        return includeJarsInPWD != null ? includeJarsInPWD : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeJarsInPresentWorkingDir(boolean val) {
        this.includeJarsInPWD = val;
        return (T) this;
    }

    public boolean isContinuouslyRestart() {
        return continuousRestart != null ? continuousRestart : true;
    }

    public <T extends ProcessLauncherOptions> T setContinuouslyRestart(boolean val) {
        this.continuousRestart = val;
        return (T) this;
    }

    public boolean isSetupLoggingForJavaSubProcess() {
        return setupLoggingForJavaSubProcess != null ? setupLoggingForJavaSubProcess : true;
    }

    public <T extends ProcessLauncherOptions> T setSetupLoggingForJavaSubProcess(boolean val) {
        this.setupLoggingForJavaSubProcess = val;
        return (T) this;
    }

    public long getRestartCycle() {
        return restartCycle != null ? restartCycle : DEFAULT_RESTART_CYCLE;
    }

    public <T extends ProcessLauncherOptions> T setRestartCycle(long val) {
        this.restartCycle = val;
        return (T) this;
    }

    public void merge(ProcessLauncherOptions other) {
        if (other == null) {
            return;
        }
        super.merge(other);

        includeWebDriverBinaryPaths = other.isIncludeWebDriverBinaryPaths();
        includeJavaSystemProperties = other.isIncludeJavaSystemProperties();
        includeJarsInSeLionHome = other.isIncludeJarsInSeLionHomeDir();
        includeParentProcessClassPath = other.isIncludeParentProcessClassPath();
        includeJarsInPWD = other.isIncludeJarsInPresentWorkingDir();
        continuousRestart = other.isContinuouslyRestart();
        setupLoggingForJavaSubProcess = other.isSetupLoggingForJavaSubProcess();
        restartCycle = other.getRestartCycle();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProcessLauncherConfiguration [");
        builder.append(super.toString());
        builder.append(", restartCycle=");
        builder.append(restartCycle);
        builder.append(", continuousRestart=");
        builder.append(continuousRestart);
        builder.append(", includeWebDriverBinaryPaths=");
        builder.append(includeWebDriverBinaryPaths);
        builder.append(", includeJavaSystemProperties=");
        builder.append(includeJavaSystemProperties);
        builder.append(", includeJarsInSeLionHome=");
        builder.append(includeJarsInSeLionHome);
        builder.append(", includeJarsInPWD=");
        builder.append(includeJarsInPWD);
        builder.append(", includeParentProcessClassPath=");
        builder.append(includeParentProcessClassPath);
        builder.append(", setupLoggingForJavaSubProcess=");
        builder.append(setupLoggingForJavaSubProcess);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public ProcessLauncherConfiguration fromJson(JsonElement json) {
        return new GsonBuilder().create().fromJson(json, ProcessLauncherConfiguration.class);
    }

    @Override
    public ProcessLauncherConfiguration fromJson(String json) {
        return new GsonBuilder().create().fromJson(json, ProcessLauncherConfiguration.class);
    }

    public static ProcessLauncherConfiguration loadFromFile(String configFile) throws IOException {
        return new ProcessLauncherConfiguration().fromJson(FileUtils.readFileToString(new File(configFile), "UTF-8"));
    }
}
