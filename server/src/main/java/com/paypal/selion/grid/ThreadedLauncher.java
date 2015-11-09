/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import static com.paypal.selion.pojos.SeLionGridConstants.*;

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * A {@link RunnableLauncher} for SeLion Grid. This launcher will perform install operations and then call
 * {@link SeLionGridLauncher}. This launcher does not support continuous restarting of the instance. <br>
 * <br>
 * <strong>Important</strong>: selenium-server and it's dependencies MUST be included in the caller's CLASSPATH before
 * calling {@link #run()} on this object
 */
public final class ThreadedLauncher extends AbstractBaseLauncher {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(ThreadedLauncher.class);
    private SeLionGridLauncher launcher;
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
     * <code>-selionConfig</code>. Uses a default set of {@link LauncherOptions}.
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
     * <code>-selionConfig</code>. Uses a default set of {@link LauncherOptions}.
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
        setLauncherOptions(launcherOptions);

        InstallHelper.firstTimeSetup();

        this.downloadList = downloadList;

        List<String> commands = new LinkedList<String>(Arrays.asList(args));
        setCommands(commands);

        // setup the SeLion config if the user want to override the default
        if (commands.contains(SELION_CONFIG_ARG)) {
            ConfigParser.setConfigFile(commands.get(commands.indexOf(SELION_CONFIG_ARG) + 1));
        }
    }

    @Override
    public final void run() {
        LOGGER.entering();
        try {
            if (!isInitialized() && downloadList != null) {
                FileDownloader.checkForDownloads(downloadList, getLauncherOptions()
                        .isFileDownloadCheckTimeStampOnInvocation(), getLauncherOptions()
                        .isFileDownladCleanupOnInvocation());
            }
            setInitialized(true);

            // Update the program arguments with any defaults
            String[] args = getProgramArguments();

            LOGGER.fine("Invoking " + SeLionGridLauncher.class.getSimpleName() + " with arguments: "
                    + Arrays.asList(args).toString());
            launcher = new SeLionGridLauncher();
            launcher.boot(args);
        } catch (Exception e) { // NOSONAR
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            shutdown();
            throw new IllegalStateException(e);
        }
    }

    /**
     * Shutdown the instance. Calls {@link SeLionGridLauncher#shutdown()} for the instance associated with this object.
     */
    public final void shutdown() {
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
