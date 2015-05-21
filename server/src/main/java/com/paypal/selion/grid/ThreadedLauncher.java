/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

import org.apache.commons.lang.SystemUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;

/**
 * A {@link Runnable} launcher for SeLion Grid. This launcher will perform all install operations and then call
 * {@link SeLionGridLauncher}. This launcher does not support continuous restarting of the instance. </br> </br>
 * <strong>Important</strong>: selenium-server and it's dependencies MUST be included in the caller's CLASSPATH before
 * calling {@link #run()} on this object
 */
public class ThreadedLauncher extends AbstractBaseLauncher {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(ThreadedLauncher.class);
    private static volatile boolean intialized = false;
    private SeLionGridLauncher launcher;

    /**
     * Initialize a new SeLion Grid with the args supplied. Supports SeLion specific args such as
     * <code>-selionConfig</code>
     * 
     * @param args
     *            The program arguments to use. Can be a mix of SeLion and selenium arguments.
     */
    public ThreadedLauncher(String[] args) {
        if (!intialized) {
            InstallHelper.firstTimeSetup();
        }

        commands = new LinkedList<String>(Arrays.asList(args));

        // setup the SeLion config if the user want to override the default
        if (commands.contains(SELION_CONFIG_ARG)) {
            ConfigParser.setConfigFile(commands.get(commands.indexOf(SELION_CONFIG_ARG) + 1));
        }
    }

    @Override
    public void run() {
        LOGGER.entering();
        try {
            InstanceType type = getType();
            if (!intialized) {
                FileDownloader.checkForDownloads(type);
                intialized = true;
            }

            // Setup the WebDriver binary paths
            if (type.equals(InstanceType.SELENIUM_NODE) || type.equals(InstanceType.SELENIUM_STANDALONE)) {
                // Make sure we setup WebDriver binary paths for the child process
                if (SystemUtils.IS_OS_WINDOWS && System.getProperty("webdriver.ie.driver") == null) {
                    System.setProperty("webdriver.ie.driver", SeLionConstants.SELION_HOME_DIR
                            + SeLionConstants.IE_DRIVER);
                }
                if (System.getProperty("webdriver.chrome.driver") == null) {
                    System.setProperty("webdriver.chrome.driver", SeLionConstants.SELION_HOME_DIR
                            + SeLionConstants.CHROME_DRIVER);
                }
                if (System.getProperty("phantomjs.binary.path") == null) {
                    System.setProperty("phantomjs.binary.path", SeLionConstants.SELION_HOME_DIR
                            + SeLionConstants.PHANTOMJS_DRIVER);
                }
            }

            // Update the program arguments with any defaults
            String[] args = getProgramArguments();

            LOGGER.info("Invoking " + SeLionGridLauncher.class.getSimpleName() + " with arguments: "
                    + Arrays.asList(args).toString());
            launcher = new SeLionGridLauncher();
            launcher.boot(args);
        } catch (Exception e) { // NOSONAR
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            shutdown();
        }
    }

    /**
     * Shutdown the instance. Calls {@link SeLionGridLauncher#shutdown()} for the instance associated with this object.
     */
    public void shutdown() {
        LOGGER.entering();
        try {
            launcher.shutdown();
        } catch (Exception e) { // NOSONAR
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        LOGGER.exiting();
    }
}
