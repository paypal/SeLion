package com.paypal.selion.grid;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * These options give a programmatic way to influence some of the behaviors of a process launcher. These behaviors may
 * or may not have a SeLion Grid dash argument that corresponds to them. Dash arguments override ProcessLauncherOptions.
 */
public final class ProcessLauncherOptions {
    private boolean includeSystemProperties = true;
    private boolean includeParentProcessClassPath = true;
    private boolean includeJarsInSeLionHomeDir = true;
    private boolean includeJarsInPresentWorkingDir = true;
    private boolean setupLoggingForJavaSubProcess = true;
    private boolean includeWebDriverBinaryPaths = true;
    private boolean continuousRestart = true;
    private boolean fileDownloadCleanupOnInvocation = true;
    private boolean fileDownloadCheckTimeStampOnInvocation = true;

    /**
     * Enable/Disable clean up of previously downloaded artifacts for subsequent calls to {@link FileDownloader} .
     * Default: enabled.
     */
    public ProcessLauncherOptions setFileDownloadCleanupOnInvocation(boolean val) {
        fileDownloadCleanupOnInvocation = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isFileDownladCleanupOnInvocation() {
        return fileDownloadCleanupOnInvocation;
    }

    /**
     * Enable/Disable download.json time stamp check. If enabled, subsequent calls to {@link FileDownloader} will
     * immediately return if the time stamp is unchanged. Default: enabled.
     */
    public ProcessLauncherOptions setFileDownloadCheckTimeStampOnInvocation(boolean val) {
        fileDownloadCheckTimeStampOnInvocation = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isFileDownloadCheckTimeStampOnInvocation() {
        return fileDownloadCheckTimeStampOnInvocation;
    }

    /**
     * Enable/Disable forwarding of current Java System Properties to sub process. Default: enabled.
     */
    public ProcessLauncherOptions setIncludeJavaSystemProperties(boolean val) {
        includeSystemProperties = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isIncludeJavaSystemProperties() {
        return includeSystemProperties;
    }

    /**
     * Enable/Disable scanning for jar files in the {@link SeLionConstants#SELION_HOME_DIR}. Default: enabled.
     */
    public ProcessLauncherOptions setIncludeJarsInSeLionHomeDir(boolean val) {
        includeJarsInSeLionHomeDir = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isIncludeJarsInSeLionHomeDir() {
        return includeJarsInSeLionHomeDir;
    }

    /**
     * Enable/Disable forwarding of current Java CLASSPATH to sub process. Default: enabled.
     */
    public ProcessLauncherOptions setIncludeParentProcessClassPath(boolean val) {
        includeParentProcessClassPath = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isIncludeParentProcessClassPath() {
        return includeParentProcessClassPath;
    }

    /**
     * Enable/Disable scanning for jar files in present working directory. Default: enabled.
     */
    public ProcessLauncherOptions setIncludeJarsInPresentWorkingDir(boolean val) {
        includeJarsInPresentWorkingDir = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isIncludeJarsInPresentWorkingDir() {
        return includeJarsInPresentWorkingDir;
    }

    /**
     * Enable/Disable continuous restart. Can also be disabled via the dash argument
     * {@link SeLionGridConstants#SELION_NOCONTINUOS_ARG}. Default: enabled.
     */
    public ProcessLauncherOptions setContinuouslyRestart(boolean val) {
        continuousRestart = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isContinuouslyRestart() {
        return continuousRestart;
    }

    /**
     * Enable/Disable setup of logging.propertis file for the Java sub process AND passing the system property
     * <code>-Djava.util.logging.config.file</code>. Default: enabled.
     */
    public ProcessLauncherOptions setSetupLoggingForJavaSubProcess(boolean val) {
        setupLoggingForJavaSubProcess = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isSetupLoggingForJavaSubProcess() {
        return setupLoggingForJavaSubProcess;
    }

    /**
     * Enable/Disable passing of the system properties for IEDriver, Chromedriver, and PhantomJS which establish the
     * binary paths. Default: enabled.
     */
    public ProcessLauncherOptions setIncludeWebDriverBinaryPaths(boolean val) {
        includeWebDriverBinaryPaths = val;
        return this;
    }

    /**
     * @return the configured state.
     */
    public boolean isIncludeWebDriverBinaryPaths() {
        return includeWebDriverBinaryPaths;
    }
}