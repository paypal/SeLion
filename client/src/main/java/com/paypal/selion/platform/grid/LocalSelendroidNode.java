/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.grid;

import io.selendroid.SelendroidConfiguration;
import io.selendroid.SelendroidLauncher;
import io.selendroid.grid.SelendroidSessionProxy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.openqa.grid.common.exception.GridException;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A singleton that is responsible for encapsulating all the logic w.r.t starting/shutting down a local android driver
 * node.
 * 
 */
public class LocalSelendroidNode extends AbstractNode implements LocalServerComponent {

    protected boolean isRunning = false;
    private SelendroidLauncher server = null;
    private SelendroidConfiguration sconfig = new SelendroidConfiguration();
    private SimpleLogger logger = SeLionLogger.getLogger();

    @Override
    public void boot(WebDriverPlatform platform) {
        logger.entering(platform);
        if (isRunning) {
            logger.exiting();
            return;
        }
        if (platform != WebDriverPlatform.ANDROID) {
            logger.exiting();
            return;
        }
        String host = "localhost";
        String hubPort = Config.getConfigProperty(ConfigProperty.SELENIUM_PORT);
        String registrationUrl = String.format("http://%s:%s/grid/register", host, hubPort);

        try {
            int port = new LocalGridConfigFileParser().getPort() + 2;
            startSelendroidDriverNode(port);
            waitForNodeToComeUp(port);
            isRunning = true;
            logger.log(Level.INFO, "Attached SelendroidDriver node to local hub " + registrationUrl);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new GridException("Failed to start a local Selendroid Node", e);
        }
    }

    @Override
    public void shutdown() {
        if (server != null) {
            try {
                server.stopSelendroid();
                logger.log(Level.INFO, "Local Selendroid Node has been stopped");
            } catch (Exception e) {
                String errorMsg = "An error occured while attempting to shut down the local Selendroid Node. Root cause: ";
                logger.log(Level.SEVERE, errorMsg, e);
            }
        }

    }

    public void waitForNodeToComeUp(int port) { // this is fine
        String exceptionMsg = "Encountered problems when attempting to register the Selendroid Node to the local Grid";
        waitForNodeToComeUp(port, exceptionMsg);
    }

    private void startSelendroidDriverNode(int port) throws Exception {
        logger.entering(port);
        List<String> args = new ArrayList<String>();
        args.add("-deviceScreenshot");
        args.add("-logLevel");
        args.add("VERBOSE");
        args.add("-hub");
        args.add("http://127.0.0.1:" + Config.getIntConfigProperty(ConfigProperty.SELENIUM_PORT) + "/grid/register");
        args.add("-port");
        args.add(Integer.toString(port));
        args.add("-selendroidServerPort");
        args.add(Integer.toString(Config.getIntConfigProperty(ConfigProperty.SELENDROID_SERVER_PORT)));

        String autFolder = Config.getConfigProperty(ConfigProperty.SELENIUM_NATIVE_APP_FOLDER);
        if ((autFolder != null) && (!autFolder.trim().isEmpty())) {
            File folder = new File(autFolder);
            try {
                Collection<File> files = FileUtils.listFiles(folder,
                    FileFilterUtils.and(FileFilterUtils
                               .notFileFilter(FileFilterUtils
                                        .prefixFileFilter("resigned")),
                        FileFilterUtils.suffixFileFilter(".apk")),
                        FileFilterUtils.notFileFilter(FileFilterUtils
                                .directoryFileFilter()));

                for (File file : files) {
                    args.add("-aut");
                    args.add(file.getAbsolutePath());
                }
            } catch (IllegalArgumentException iae) {
                logger.warning(iae.getMessage());
            }
        }
        
        args.add("-proxy");
        args.add(SelendroidSessionProxy.class.getCanonicalName());
        args.add("-host");
        args.add("127.0.0.1");

        Boolean selendroidForceReinstall = Config.getBoolConfigProperty(ConfigProperty.SELENDROID_SERVER_FORCE_REINSTALL);
        if (selendroidForceReinstall) {
              args.add("-forceReinstall");
        }

        sconfig = SelendroidConfiguration.create(args.toArray(new String[args.size()]));
        server = new SelendroidLauncher(sconfig);

        //HACK :: put the RootLogger back into the original state
         // remove all handlers first
        Handler[] handlers = Logger.getLogger("").getHandlers();
        Level level = Logger.getLogger("").getLevel();

        for (Handler handler : Logger.getLogger("").getHandlers()) {
            Logger.getLogger("").removeHandler(handler);
        }
        // put the original ones back
        for (Handler handler : handlers) {
            Logger.getLogger("").addHandler(handler);
        }
        // reset the log level
        Logger.getLogger("").setLevel(level);

        server.launchSelendroid();
        logger.exiting();
    }

}