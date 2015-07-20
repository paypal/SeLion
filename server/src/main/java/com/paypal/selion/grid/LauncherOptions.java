package com.paypal.selion.grid;

/**
 * These options give a programmatic way to influence some of the behaviors of a {@link RunnableLauncher}. These
 * behaviors may or may not have a SeLion Grid dash argument that corresponds to them. Dash arguments override
 * {@link LauncherOptions}.
 */
public interface LauncherOptions {
    /**
     * Enable/Disable clean up of previously downloaded artifacts for subsequent calls to {@link FileDownloader} .
     * Default: enabled.
     */
    <T extends LauncherOptions> T setFileDownloadCleanupOnInvocation(boolean val);

    /**
     * @return the configured state.
     */
    boolean isFileDownladCleanupOnInvocation();

    /**
     * Enable/Disable download.json time stamp check. If enabled, subsequent calls to {@link FileDownloader} will
     * immediately return if the time stamp is unchanged. Default: enabled.
     */
    <T extends LauncherOptions> T setFileDownloadCheckTimeStampOnInvocation(boolean val);

    /**
     * @return the configured state.
     */
    boolean isFileDownloadCheckTimeStampOnInvocation();


    /**
     * Implements {@link LauncherOptions}
     */
    @SuppressWarnings("unchecked")
    class LauncherOptionsImpl implements LauncherOptions {
        private boolean fileDownloadCleanupOnInvocation = true;
        private boolean fileDownloadCheckTimeStampOnInvocation = true;

        public <T extends LauncherOptions> T setFileDownloadCleanupOnInvocation(boolean val) {
            fileDownloadCleanupOnInvocation = val;
            return (T) this;
        }

        public boolean isFileDownladCleanupOnInvocation() {
            return fileDownloadCleanupOnInvocation;
        }

        public <T extends LauncherOptions> T setFileDownloadCheckTimeStampOnInvocation(boolean val) {
            fileDownloadCheckTimeStampOnInvocation = val;
            return (T) this;
        }

        public boolean isFileDownloadCheckTimeStampOnInvocation() {
            return fileDownloadCheckTimeStampOnInvocation;
        }
    }
}