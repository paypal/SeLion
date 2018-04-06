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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.ConfigParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * A {@link RunnableLauncher} for SeLion Grid. This launcher will perform install operations and then call
 * {@link SeLionGridLauncherV3}. This launcher does not support continuous restarting of the instance. <br>
 * <br>
 * <strong>Important</strong>: selenium-server and it's dependencies MUST be included in the caller's CLASSPATH before
 * calling {@link #run()} on this object
 */
public final class ThreadedLauncher extends AbstractBaseLauncher {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(ThreadedLauncher.class);
    private SeLionGridLauncherV3 launcher;
    private List<String> downloadList;

    /**
     * Initialize a new SeLion Grid with the args supplied. Supports SeLion specific args such as
     * <code>-selionConfig</code>. Uses a default set of {@link LauncherOptions}.
     *
     * @param args
     *            The program arguments to use. Can be a mix of SeLion and selenium arguments.
     */
    public ThreadedLauncher(String[] args) {
        this(args, null);
    }

    /**
     * Initialize a new SeLion Grid with the args supplied. Supports SeLion specific args such as
     * <code>-selionConfig</code>. Uses the provided {@link LauncherOptions}. All {@code args} take precedence over the
     * {@code launcherOptions} and/or other values.
     * 
     * @param args
     *            The program arguments to use. Can be a mix of SeLion and selenium arguments.
     * @param launcherOptions
     *            the {@link LauncherOptions} to use
     */
    public ThreadedLauncher(String[] args, LauncherOptions launcherOptions) {
        this(args, launcherOptions, null);
    }

    /**
     * Initialize a new SeLion Grid with the args supplied. Supports SeLion specific args such as
     * <code>-selionConfig</code>. Uses the provided {@link LauncherOptions} and the provided download list. All
     * {@code args} take precedence over the {@code launcherOptions} and/or other values.
     *
     * @param args
     *            The program arguments to use. Can be a mix of SeLion and selenium arguments.
     * @param launcherOptions
     *            the {@link LauncherOptions} to use
     * @param downloadList
     *            The list of binaries to download. These names MUST match the names from the download.json file
     */
    public ThreadedLauncher(String[] args, LauncherOptions launcherOptions, List<String> downloadList) {
        super();
        LauncherConfiguration lc = new LauncherConfiguration();
        lc.merge(launcherOptions);

        JCommander commander = new JCommander();
        commander.setAcceptUnknownOptions(true);
        commander.addObject(lc);
        try {
            commander.parse(args);
            // we need to consider the selionConfig file when the caller is providing
            // a non-default selionConfig file location
            if (lc.getSeLionConfig() != SeLionGridConstants.SELION_CONFIG_FILE) {
                // reload the config from the file
                lc = LauncherConfiguration.loadFromFile(lc.getSeLionConfig());
                // re-merge the launcherOptions
                lc.merge(launcherOptions);
                // re-parse the args
                commander = new JCommander();
                commander.setAcceptUnknownOptions(true);
                commander.addObject(lc);
                commander.parse(args);
            }
        } catch (ParameterException | IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        setLauncherOptions(launcherOptions);

        List<String> commands = new LinkedList<>(Arrays.asList(args));
        setCommands(commands);

        // setup the SeLion config
        ConfigParser.setConfigFile(lc.getSeLionConfig());

        InstallHelper.firstTimeSetup();
        this.downloadList = downloadList;
    }

    public void run() {
        LOGGER.entering();
        try {
            if (!isInitialized() && downloadList != null) {
                FileDownloader.checkForDownloads(downloadList, getLauncherOptions()
                        .isFileDownloadCheckTimeStampOnInvocation(), getLauncherOptions()
                        .isFileDownloadCleanupOnInvocation());
            }
            setInitialized(true);

            // Update the program arguments with any defaults
            String[] args = getProgramArguments();

            LOGGER.fine("Invoking " + SeLionGridLauncherV3.class.getSimpleName() + " with arguments: "
                    + Arrays.asList(args).toString());
            launcher = new SeLionGridLauncherV3();
            launcher.boot(args);
        } catch (Exception e) { // NOSONAR
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            shutdown();
            throw new IllegalStateException(e);
        }
    }

    /**
     * Shutdown the instance. Calls {@link SeLionGridLauncherV3#shutdown()} for the instance associated with this
     * object.
     */
    public void shutdown() {
        LOGGER.entering();
        if (launcher == null) {
            return;
        }

        if (!isRunning()) {
            return;
        }

        try {
            launcher.shutdown();
        } catch (Exception e) { // NOSONAR
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        LOGGER.exiting();
    }
}
