package com.paypal.selion.grid;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * {@link LauncherOptions} which apply to {@link RunnableLauncher}s which spawn a new process
 */
public interface ProcessLauncherOptions extends LauncherOptions {

    /**
     * Enable/Disable passing of the system properties for IEDriver, Chromedriver, and PhantomJS which establish the
     * binary paths. Default: enabled.
     */
    <T extends ProcessLauncherOptions> T setIncludeWebDriverBinaryPaths(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeWebDriverBinaryPaths();

    /**
     * Enable/Disable forwarding of current Java System Properties to sub process. Default: enabled.
     */
    <T extends ProcessLauncherOptions> T setIncludeJavaSystemProperties(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeJavaSystemProperties();

    /**
     * Enable/Disable scanning for jar files in the {@link SeLionConstants#SELION_HOME_DIR}. Default: enabled.
     */
    <T extends ProcessLauncherOptions> T setIncludeJarsInSeLionHomeDir(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeJarsInSeLionHomeDir();

    /**
     * Enable/Disable forwarding of current Java CLASSPATH to sub process. Default: enabled.
     */
    <T extends ProcessLauncherOptions> T setIncludeParentProcessClassPath(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeParentProcessClassPath();

    /**
     * Enable/Disable scanning for jar files in present working directory. Default: enabled.
     */
    <T extends ProcessLauncherOptions> T setIncludeJarsInPresentWorkingDir(boolean val);

    /**
     * @return the configured state.
     */
    boolean isIncludeJarsInPresentWorkingDir();

    /**
     * Enable/Disable continuous restart. Can also be disabled via the dash argument
     * {@link SeLionGridConstants#SELION_NOCONTINUOUS_ARG}. Default: enabled.
     */
    <T extends ProcessLauncherOptions> T setContinuouslyRestart(boolean val);

    /**
     * @return the configured state.
     */
    boolean isContinuouslyRestart();

    /**
     * Enable/Disable setup of logging.propertis file for the Java sub process AND passing the system property
     * <code>-Djava.util.logging.config.file</code>. Default: enabled.
     */
    <T extends ProcessLauncherOptions> T setSetupLoggingForJavaSubProcess(boolean val);

    /**
     * @return the configured state.
     */
    boolean isSetupLoggingForJavaSubProcess();

    /**
     * Implements {@link ProcessLauncherOptions}
     */
    @SuppressWarnings("unchecked")
    class ProcessLauncherOptionsImpl extends LauncherOptionsImpl implements ProcessLauncherOptions {
        private boolean includeSystemProperties = true;
        private boolean includeParentProcessClassPath = true;
        private boolean includeJarsInSeLionHomeDir = true;
        private boolean includeJarsInPresentWorkingDir = true;
        private boolean setupLoggingForJavaSubProcess = true;
        private boolean continuousRestart = true;
        private boolean includeWebDriverBinaryPaths = true;

        public <T extends ProcessLauncherOptions> T setIncludeWebDriverBinaryPaths(boolean val) {
            includeWebDriverBinaryPaths = val;
            return (T) this;
        }

        public boolean isIncludeWebDriverBinaryPaths() {
            return includeWebDriverBinaryPaths;
        }

        public <T extends ProcessLauncherOptions> T setIncludeJavaSystemProperties(boolean val) {
            includeSystemProperties = val;
            return (T) this;
        }

        public boolean isIncludeJavaSystemProperties() {
            return includeSystemProperties;
        }

        public <T extends ProcessLauncherOptions> T setIncludeJarsInSeLionHomeDir(boolean val) {
            includeJarsInSeLionHomeDir = val;
            return (T) this;
        }

        public boolean isIncludeJarsInSeLionHomeDir() {
            return includeJarsInSeLionHomeDir;
        }

        public <T extends ProcessLauncherOptions> T setIncludeParentProcessClassPath(boolean val) {
            includeParentProcessClassPath = val;
            return (T) this;
        }

        public boolean isIncludeParentProcessClassPath() {
            return includeParentProcessClassPath;
        }

        public <T extends ProcessLauncherOptions> T setIncludeJarsInPresentWorkingDir(boolean val) {
            includeJarsInPresentWorkingDir = val;
            return (T) this;
        }

        public boolean isIncludeJarsInPresentWorkingDir() {
            return includeJarsInPresentWorkingDir;
        }

        public <T extends ProcessLauncherOptions> T setContinuouslyRestart(boolean val) {
            continuousRestart = val;
            return (T) this;
        }

        public boolean isContinuouslyRestart() {
            return continuousRestart;
        }

        public <T extends ProcessLauncherOptions> T setSetupLoggingForJavaSubProcess(boolean val) {
            setupLoggingForJavaSubProcess = val;
            return (T) this;
        }

        public boolean isSetupLoggingForJavaSubProcess() {
            return setupLoggingForJavaSubProcess;
        }
    }
}
