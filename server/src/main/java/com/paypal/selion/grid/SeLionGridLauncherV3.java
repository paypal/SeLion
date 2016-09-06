/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright 2016 Software Freedom Conservancy                                                                        |
|  Copyright (C) 2014-2016 PayPal                                                                                     |
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
 * 1. Modified to change the logging behavior.
 * 2. Modified to support instancing and a shutdown() method.
 * 3. Modified to consider and use SeLion-Grid -argument(s)
 */

package com.paypal.selion.grid;

import java.util.Collections;
import java.util.logging.Logger;

import com.beust.jcommander.JCommander;
import com.paypal.selion.utils.ConfigParser;
import org.openqa.grid.common.GridRole;
import org.openqa.grid.common.JSONConfigurationUtils;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.grid.internal.utils.configuration.StandaloneConfiguration;
import org.openqa.grid.selenium.GridLauncherV3;
import org.openqa.grid.shared.CliUtils;
import org.openqa.grid.web.Hub;

import com.google.common.collect.ImmutableMap;
import com.paypal.selion.logging.SeLionGridLogger;
import org.openqa.selenium.internal.BuildInfo;
import org.openqa.selenium.remote.server.SeleniumServer;

/**
 * The SeLion version of the {@link GridLauncherV3}. We have intentionally duplicatedcode from {@link GridLauncherV3}
 * because {@link GridLauncherV3} is meddling around with the {@link Logger} and thus preventing us from feeding in
 * {@link Logger} properties to control/tweak the File and console level logging. This class represents a stripped
 * version of {@link GridLauncherV3} without the logging tweaks.</br> </br> Use of this class to launch SeLion Grid
 * requires the caller to supply all arguments including all SeLion specific -servlets, -proxy, etc which if omitted may
 * yield undesired results. Use of {@link ThreadedLauncher} and {@link JarSpawner} is recommended instead.
 */
public class SeLionGridLauncherV3 {

    private static final Logger LOGGER = SeLionGridLogger.getLogger(SeLionGridLauncherV3.class);
    private Object type;

    private static abstract class SeLionGridItemLauncher {
        protected StandaloneConfiguration configuration;
        protected boolean helpRequested;
        protected Object type;
        abstract void setConfiguration(String[] args);
        abstract void launch() throws Exception;
        abstract void printUsage();
    }

    private static ImmutableMap<GridRole, SeLionGridItemLauncher> launchers =
        new ImmutableMap.Builder<GridRole, SeLionGridItemLauncher>()
        .put(GridRole.NOT_GRID, new SeLionGridItemLauncher() {
            public void setConfiguration(String[] args) {
                SeLionStandaloneConfiguration ssc = new SeLionStandaloneConfiguration();
                new JCommander(ssc, args);
                configuration = ssc.getStandaloneConfiguration();
                helpRequested = configuration.help;
            }

            public void launch() throws Exception {
                LOGGER.info("Launching a standalone Selenium Server");
                SeleniumServer server = new SeleniumServer(configuration);
                type = server;
                server.boot();
                LOGGER.info("Selenium Server is up and running");
            }

            @Override
            void printUsage() {
                new JCommander(new SeLionStandaloneConfiguration()).usage();
            }
        }).put(GridRole.HUB, new SeLionGridItemLauncher() {
            public void setConfiguration(String[] args) {
                SeLionGridHubConfiguration sghc = new SeLionGridHubConfiguration();
                new JCommander(sghc, args);

                GridHubConfiguration pending = sghc.getGridHubConfiguration();
                //re-parse the args using any -hubConfig specified to init
                if (pending.hubConfig != null) {
                    sghc.setGridHubConfiguration(GridHubConfiguration
                        .loadFromJSON(JSONConfigurationUtils.loadJSON(pending.hubConfig)));
                    new JCommander(sghc, args);
                }
                sghc.mergeCustom();
                configuration = sghc.getGridHubConfiguration();
                helpRequested = configuration.help;
            }

            public void launch() throws Exception {
                LOGGER.info("Launching Selenium Grid hub");
                Hub h = new Hub((GridHubConfiguration) configuration);
                type = h;
                h.start();
                LOGGER.info("Nodes should register to " + h.getRegistrationURL());
                LOGGER.info("Selenium Grid hub is up and running");
            }

            @Override
            void printUsage() {
                new JCommander(new SeLionGridHubConfiguration()).usage();
            }
        }).put(GridRole.NODE, new SeLionGridItemLauncher() {
            public void setConfiguration(String[] args) {
                SeLionGridNodeConfiguration sgnc = new SeLionGridNodeConfiguration();
                new JCommander(sgnc, args);

                GridNodeConfiguration pending = sgnc.getGridNodeConfiguration();
                //re-parse the args using any -nodeConfig specified to init
                if (pending.nodeConfigFile != null) {
                    sgnc.setGridNodeConfiguration(GridNodeConfiguration
                            .loadFromJSON(JSONConfigurationUtils.loadJSON(pending.nodeConfigFile)));
                    new JCommander(sgnc, args);
                }
                sgnc.mergeCustom();
                configuration = sgnc.getGridNodeConfiguration();
                helpRequested = configuration.help;
                if (configuration.port == null) {
                    configuration.port = 5555;
                }
            }

            public void launch() throws Exception {
                LOGGER.info("Launching a Selenium Grid node");
                RegistrationRequest
                        c =
                        RegistrationRequest.build((GridNodeConfiguration) configuration);
                SelfRegisteringRemote remote = new SelfRegisteringRemote(c);
                type = remote;
                remote.setRemoteServer(new SeleniumServer(configuration));
                remote.startRemoteServer();
                LOGGER.info("Selenium Grid node is up and ready to register to the hub");
                remote.startRegistrationProcess();
            }

            @Override
            void printUsage() {
                new JCommander(new SeLionGridNodeConfiguration()).usage();
            }
        }).build();

    private static void logEnvironment() {
        LOGGER.fine("Environment Variables: " + Collections.singletonList(System.getenv()));
        LOGGER.fine("JVM System Properties: " + Collections.singletonList(System.getProperties()));
    }

    public static void main(String[] args) throws Exception {
        new SeLionGridLauncherV3().boot(args);
    }

    /**
     * Shutdown the instance
     *
     * @throws Exception
     */
    public void shutdown() throws Exception {
        if (type == null) {
            return;
        }
        if (type instanceof Hub) {
            ((Hub) type).stop();
        }
        if (type instanceof SelfRegisteringRemote) {
            ((SelfRegisteringRemote) type).stopRemoteServer();
        }
        if (type instanceof SeleniumServer) {
            ((SeleniumServer) type).stop();
        }
        LOGGER.info("Selenium is shut down");
    }

    /**
     * Boot the instance base on the arguments supplied
     *
     * @param args
     *            the arguments to use
     * @throws Exception
     */
    public void boot(String[] args) throws Exception {
        SeLionStandaloneConfiguration configuration = new SeLionStandaloneConfiguration();
        JCommander commander = new JCommander();
        commander.setAcceptUnknownOptions(true);
        commander.addObject(configuration);
        commander.parse(args);

        ProcessLauncherConfiguration plc = configuration.getProcessLauncherConfiguration();
        if (plc.selionConfig != null) {
            ConfigParser.setConfigFile(plc.selionConfig);
        }

        StandaloneConfiguration sc = configuration.getStandaloneConfiguration();
        String role = sc.role.toLowerCase();
        GridRole gridRole = GridRole.get(role);
        if (launchers.containsKey(gridRole)) {
            SeLionGridItemLauncher launcher = launchers.get(gridRole);
            launcher.setConfiguration(args);

            if (launcher.helpRequested) {
                launcher.printUsage();
                return;
            }

            logEnvironment();

            BuildInfo buildInfo = new BuildInfo();
            LOGGER.info(String.format(
                    "Selenium build info: version: '%s', revision: '%s'",
                    buildInfo.getReleaseLabel(),
                    buildInfo.getBuildRevision()));

            try {
                launcher.launch();
                type = launcher.type;
            } catch (Exception e) {
                launcher.printUsage();
                e.printStackTrace();
            }
        } else {
            printInfoAboutRoles(role);
            return;
        }
    }

    public static void printInfoAboutRoles(String roleCommandLineArg) {
        CliUtils.printWrappedLine("", "Error: the role '" + roleCommandLineArg
                    + "' does not match a recognized server role\n");
        System.out.println("Selenium server can run in one of the following roles:\n"
                + "  hub         as a hub of a Selenium grid\n"
                + "  node        as a node of a Selenium grid\n"
                + "  standalone  as a standalone server not being a part of a grid\n" + "\n"
                + "If -role option is omitted the server runs standalone\n");
        CliUtils.printWrappedLine("", "To get help on the options available for a specific role run the server"
                + " with -help option and the corresponding -role option value");
    }
}
