/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright 2011 Selenium committers                                                                                 |
|  Copyright 2011 Software Freedom Conservancy                                                                        |
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

/*
 * Modified to change the logging behavior.
 */

package com.paypal.selion.grid;

import java.util.Arrays;
import java.util.logging.Logger;

import org.openqa.grid.common.CommandLineOptionHelper;
import org.openqa.grid.common.GridDocHelper;
import org.openqa.grid.common.GridRole;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.GridConfigurationException;
import org.openqa.grid.internal.utils.GridHubConfiguration;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.grid.selenium.GridLauncher;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.cli.RemoteControlLauncher;

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.ConfigParser;

/**
 * The SeLion version of the {@link GridLauncher}. We have intentionally duplicated the code from {@link GridLauncher}
 * because {@link GridLauncher} is meddling around with the {@link Logger} and thus preventing us from feeding in
 * {@link Logger} properties to control/tweak the File and console level logging. This class represents a stripped
 * version of {@link GridLauncher} without the logging tweaks.
 *
 */
public class SeLionGridLauncher {

    private static final Logger LOGGER = SeLionGridLogger.getLogger(SeLionGridLauncher.class);

    public static void printEnvironment() {
        LOGGER.info("Environment Variables: " + Arrays.asList(System.getenv()));
        LOGGER.info("JVM System Properties: " + Arrays.asList(System.getProperties()));
    }

    public static void main(String[] args) throws Exception {
        CommandLineOptionHelper helper = new CommandLineOptionHelper(args);
        
        if (helper.isParamPresent("-help") || helper.isParamPresent("-h")) {
            String separator = "\n----------------------------------\n";
            RemoteControlLauncher.usage(separator + "To use as a standalone server" + separator);
            GridDocHelper.printHelp(separator + "To use in a grid environment :" + separator, false);
            return;
        }

        if (helper.isParamPresent(SeLionGridConstants.SELION_CONFIG_ARG)) {
            ConfigParser.setConfigFile(helper.getParamValue(SeLionGridConstants.SELION_CONFIG_ARG));
        }

        try {
            GridRole role = GridRole.find(args);
            printEnvironment();

            switch (role) {
            case NOT_GRID:
                LOGGER.info("Launching a standalone server");
                final RemoteControlConfiguration configuration = RemoteControlLauncher.parseLauncherOptions(args);
                final SeleniumServer proxy = new SeleniumServer(configuration);
                proxy.boot();
                break;
            case HUB:
                LOGGER.info("Launching a selenium grid server");
                final GridHubConfiguration ghc = GridHubConfiguration.build(args);
                final Hub h = new Hub(ghc);
                h.start();
                break;
            case NODE:
                LOGGER.info("Launching a selenium grid node");
                final RegistrationRequest rr = RegistrationRequest.build(args);
                final SelfRegisteringRemote remote = new SelfRegisteringRemote(rr);
                remote.startRemoteServer();
                remote.startRegistrationProcess();
                break;
            default:
               throw new RuntimeException("NI");
            }
        } catch (GridConfigurationException e) {
            GridDocHelper.printHelp(e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException rex) {
            rex.printStackTrace();
        } 
    }

}
