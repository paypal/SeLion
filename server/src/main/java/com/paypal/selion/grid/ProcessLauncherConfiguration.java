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

    public static final String NO_CONTINUOUS_RESTART = "noContinuousRestart";
    public static final String NO_CONTINUOUS_RESTART_ARG = "-" + NO_CONTINUOUS_RESTART;

    public static final String EXCLUDE_BINARY_PATHS = "excludeBinaryPaths";
    public static final String EXCLUDE_BINARY_PATHS_ARG = "-" + EXCLUDE_BINARY_PATHS;

    public static final String EXCLUDE_JAVA_SYSTEM_PROPS = "excludeJavaSystemProperties";
    public static final String EXCLUDE_JAVA_SYSTEM_PROPS_ARG = "-" + EXCLUDE_JAVA_SYSTEM_PROPS;

    public static final String EXCLUDE_JARS_IN_SELION_HOME = "excludeJarsInSeLionHome";
    public static final String EXCLUDE_JARS_IN_SELION_HOME_ARG = "-" + EXCLUDE_JARS_IN_SELION_HOME;

    public static final String EXCLUDE_JARS_IN_PWD = "excludeJarsInPWD";
    public static final String EXCLUDE_JARS_IN_PWD_ARG = "-" + EXCLUDE_JARS_IN_PWD;

    public static final String EXCLUDE_PARENT_CLASSPATH = "excludeParentProcessClassPath";
    public static final String EXCLUDE_PARENT_CLASSPATH_ARG = "-" + EXCLUDE_PARENT_CLASSPATH;

    public static final String DONT_SETUP_LOGGING_FOR_SUB_PROCESS = "dontSetupLoggingForSubProcess";
    public static final String DONT_SETUP_LOGGING_FOR_SUB_PROCESS_ARG = "-" + DONT_SETUP_LOGGING_FOR_SUB_PROCESS;

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
        names = NO_CONTINUOUS_RESTART_ARG,
        description = "<Boolean> : Disables continuous restart of the SeLion grid sub-process"
    )
    protected Boolean noContinuousRestart = false;

    /**
     * whether to exclude -Dwebdriver.binary.path=path JVM arguments when invoking the sub-process
     */
    @Parameter(
        names = EXCLUDE_BINARY_PATHS_ARG,
        description = "<Boolean> : Exclude Selenium system properties which point to the binary paths of IEDriver, " +
            "chromedriver, etc when invoking the sub-process",
        hidden = true
    )
    protected Boolean excludeBinaryPaths;

    /**
     * whether to exclude forwarding of current JVM system properties to the sub-process
     */
    @Parameter(
        names = EXCLUDE_JAVA_SYSTEM_PROPS_ARG,
        description = "<Boolean> : Exclude forwarding of JVM (-D) system properties to the sub-process",
        hidden = true
    )
    protected Boolean excludeJavaSystemProperties;

    /**
     * whether to exclude scanning of and inclusion of jars in the selionHome
     */
    @Parameter(
        names = EXCLUDE_JARS_IN_SELION_HOME_ARG,
        description = "<Boolean> : Do not automatically add jars in the <selionHome> to the CLASSPATH when invoking" +
            " the sub-process",
        hidden = true
    )
    protected Boolean excludeJarsInSeLionHome;

    /**
     * whether to exclude scanning of and inclusion of jars in the PWD
     */
    @Parameter(
        names = EXCLUDE_JARS_IN_PWD_ARG,
        description = "<Boolean> : Do not automatically add jars in the present working directory to the CLASSPATH " +
            "when invoking the sub-process",
        hidden = true
    )
    protected Boolean excludeJarsInPWD;

    /**
     * whether to exclude the parent processes CLASSPATH
     */
    @Parameter(
        names = EXCLUDE_PARENT_CLASSPATH_ARG,
        description = "<Boolean> : Do not forward the parent process's CLASSPATH to the sub-process when invoking it",
        hidden = true
    )
    protected Boolean excludeParentProcessClassPath;

    /**
     * whether to disable automatic setup of a logging.properties file for the sub-process
     */
    @Parameter(
        names = DONT_SETUP_LOGGING_FOR_SUB_PROCESS_ARG,
        description = "<Boolean> : Do not automatically setup a logging.properties (and apply it) to the sub-process",
        hidden = true
    )
    protected Boolean dontSetupLoggingForSubProcess;

    public boolean isIncludeWebDriverBinaryPaths() {
        return excludeBinaryPaths != null ? !excludeBinaryPaths : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeWebDriverBinaryPaths(boolean val) {
        this.excludeBinaryPaths = !val;
        return (T) this;
    }

    public boolean isIncludeJavaSystemProperties() {
        return excludeJavaSystemProperties != null ? !excludeJavaSystemProperties : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeJavaSystemProperties(boolean val) {
        this.excludeJavaSystemProperties = !val;
        return (T) this;
    }

    public boolean isIncludeJarsInSeLionHomeDir() {
        return excludeJarsInSeLionHome != null ? !excludeJarsInSeLionHome : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeJarsInSeLionHomeDir(boolean val) {
        this.excludeJarsInSeLionHome = !val;
        return (T) this;
    }

    public boolean isIncludeParentProcessClassPath() {
        return excludeParentProcessClassPath != null ? !excludeParentProcessClassPath : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeParentProcessClassPath(boolean val) {
        this.excludeParentProcessClassPath = !val;
        return (T) this;
    }

    public boolean isIncludeJarsInPresentWorkingDir() {
        return excludeJarsInPWD != null ? !excludeJarsInPWD : true;
    }

    public <T extends ProcessLauncherOptions> T setIncludeJarsInPresentWorkingDir(boolean val) {
        this.excludeJarsInPWD = !val;
        return (T) this;
    }

    public boolean isContinuouslyRestart() {
        return noContinuousRestart != null ? !noContinuousRestart : true;
    }

    public <T extends ProcessLauncherOptions> T setContinuouslyRestart(boolean val) {
        this.noContinuousRestart = !val;
        return (T) this;
    }

    public boolean isSetupLoggingForJavaSubProcess() {
        return dontSetupLoggingForSubProcess != null ? !dontSetupLoggingForSubProcess : true;
    }

    public <T extends ProcessLauncherOptions> T setSetupLoggingForJavaSubProcess(boolean val) {
        this.dontSetupLoggingForSubProcess = !val;
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
        super.merge((LauncherOptions) other);

        excludeBinaryPaths = !other.isIncludeWebDriverBinaryPaths();
        excludeJavaSystemProperties = !other.isIncludeJavaSystemProperties();
        excludeJarsInSeLionHome = !other.isIncludeJarsInSeLionHomeDir();
        excludeParentProcessClassPath = !other.isIncludeParentProcessClassPath();
        excludeJarsInPWD = !other.isIncludeJarsInPresentWorkingDir();
        noContinuousRestart = !other.isContinuouslyRestart();
        dontSetupLoggingForSubProcess = !other.isSetupLoggingForJavaSubProcess();
        restartCycle = other.getRestartCycle();
    }

    public void merge(ProcessLauncherConfiguration other) {
        if (other == null) {
            return;
        }
        super.merge((LauncherConfiguration) other);

        if (other.excludeBinaryPaths != null) {
            excludeBinaryPaths = other.excludeBinaryPaths;
        }
        if (other.excludeJavaSystemProperties != null) {
            excludeJavaSystemProperties = other.excludeJavaSystemProperties;
        }
        if (other.excludeJarsInSeLionHome != null) {
            excludeJarsInSeLionHome = other.excludeJarsInSeLionHome;
        }
        if (other.excludeParentProcessClassPath != null) {
            excludeParentProcessClassPath = other.excludeParentProcessClassPath;
        }
        if (other.excludeJarsInPWD != null) {
            excludeJarsInPWD = other.excludeJarsInPWD;
        }
        if (other.noContinuousRestart != null) {
            noContinuousRestart = other.noContinuousRestart;
        }
        if (other.dontSetupLoggingForSubProcess != null) {
            dontSetupLoggingForSubProcess = other.dontSetupLoggingForSubProcess;
        }
        if (other.restartCycle != null) {
            restartCycle = other.restartCycle;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProcessLauncherConfiguration [");
        builder.append(super.toString());
        builder.append(", restartCycle=");
        builder.append(restartCycle);
        builder.append(", noContinuousRestart=");
        builder.append(noContinuousRestart);
        builder.append(", excludeBinaryPaths=");
        builder.append(excludeBinaryPaths);
        builder.append(", excludeJavaSystemProperties=");
        builder.append(excludeJavaSystemProperties);
        builder.append(", excludeJarsInSeLionHome=");
        builder.append(excludeJarsInSeLionHome);
        builder.append(", excludeJarsInPWD=");
        builder.append(excludeJarsInPWD);
        builder.append(", excludeParentProcessClassPath=");
        builder.append(excludeParentProcessClassPath);
        builder.append(", dontSetupLoggingForSubProcess=");
        builder.append(dontSetupLoggingForSubProcess);
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
 