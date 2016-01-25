/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright 2011 Selenium committers                                                                                 |
|  Copyright 2011 Software Freedom Conservancy                                                                        |
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
 * 3. Modified to consider and use -selionConfig argument
 */

package com.paypal.selion.grid;

import java.util.Collections;
import java.util.logging.Logger;

import org.openqa.grid.common.CommandLineOptionHelper;
import org.openqa.grid.common.GridDocHelper;
import org.openqa.grid.common.GridRole;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.GridConfigurationException;
import org.openqa.grid.internal.utils.GridHubConfiguration;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.grid.selenium.GridLauncher;
import org.openqa.grid.selenium.GridLauncher.GridItemLauncher;
import org.openqa.grid.shared.CliUtils;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.google.common.collect.ImmutableMap;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;
import com.paypal.selion.utils.ConfigParser;

/**
 * The SeLion version of the {@link GridLauncher}. We have intentionally duplicated the code from {@link GridLauncher}
 * because {@link GridLauncher} is meddling around with the {@link Logger} and thus preventing us from feeding in
 * {@link Logger} properties to control/tweak the File and console level logging. This class represents a stripped
 * version of {@link GridLauncher} without the logging tweaks.</br> </br> Use of this class to launch SeLion Grid
 * requires the caller to supply all arguments including all SeLion specific -servlets, -proxy, etc which if omitted may
 * yield undesired results. Use of {@link ThreadedLauncher} and {@link JarSpawner} is recommended instead.
 */
public class SeLionGridLauncher {

    private static final Logger LOGGER = SeLionGridLogger.getLogger(SeLionGridLauncher.class);
    private Object type;

    private interface SeLionGridItemLauncer extends GridItemLauncher {
        Object getType();
    }

    private static ImmutableMap<GridRole, SeLionGridItemLauncer> launchers = new ImmutableMap.Builder<GridRole, SeLionGridItemLauncer>()
            .put(GridRole.NOT_GRID, new SeLionGridItemLauncer() {
                private Object type;

                @Override
                public void launch(String[] args, Logger log) throws Exception {
                    log.info("Launching a standalone server");
                    final RemoteControlConfiguration configuration = SeleniumServer.parseLauncherOptions(args);
                    final SeleniumServer proxy = new SeleniumServer(configuration);
                    type = proxy;
                    proxy.boot();
                    log.info("Selenium Server is up and running");
                }

                @Override
                public void printUsage() {
                    String separator = "\n-------------------------------\n";
                    SeleniumServer.usage(separator + "Running as a standalone server" + separator);
                }

                @Override
                public Object getType() {
                    return type;
                }
            }).put(GridRole.HUB, new SeLionGridItemLauncer() {
                private Object type;

                @Override
                public void launch(String[] args, Logger log) throws Exception {
                    log.info("Launching a selenium grid server");
                    final GridHubConfiguration ghc = GridHubConfiguration.build(args);
                    final Hub h = new Hub(ghc);
                    type = h;
                    h.start();
                    log.info("Nodes should register to " + h.getRegistrationURL());
                    log.info("Selenium Grid hub is up and running");
                }

                @Override
                public void printUsage() {
                    String separator = "\n-------------------------------\n";
                    GridDocHelper.printHubHelp(separator + "Running as a grid hub" + separator, false);
                }

                @Override
                public Object getType() {
                    return type;
                }
            }).put(GridRole.NODE, new SeLionGridItemLauncer() {
                private Object type;

                @Override
                public void launch(String[] args, Logger log) throws Exception {
                    log.info("Launching a selenium grid node");
                    final RegistrationRequest rr = RegistrationRequest.build(args);
                    final SelfRegisteringRemote remote = new SelfRegisteringRemote(rr);
                    type = remote;
                    remote.setRemoteServer(new SeleniumServer(rr.getConfiguration()));
                    remote.startRemoteServer();
                    log.info("Selenium Grid node is up and ready to register to the hub");
                    remote.startRegistrationProcess();
                }

                @Override
                public void printUsage() {
                    String separator = "\n-------------------------------\n";
                    GridDocHelper.printNodeHelp(separator + "Running as a grid node" + separator, false);
                }

                @Override
                public Object getType() {
                    return type;
                }
            }).build();

    private static void logEnvironment() {
        LOGGER.fine("Environment Variables: " + Collections.singletonList(System.getenv()));
        LOGGER.fine("JVM System Properties: " + Collections.singletonList(System.getProperties()));
    }

    public static void main(String[] args) throws Exception {
        new SeLionGridLauncher().boot(args);
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
        CommandLineOptionHelper helper = new CommandLineOptionHelper(args);
        GridRole role = GridRole.find(args);

        if (role == null) {
            printInfoAboutRoles(helper);
            return;
        }

        if (helper.isParamPresent("-help") || helper.isParamPresent("-h")) {
            if (launchers.containsKey(role)) {
                launchers.get(role).printUsage();
            } else {
                printInfoAboutRoles(helper);
            }
            return;
        }

        if (helper.isParamPresent(SeLionGridConstants.SELION_CONFIG_ARG)) {
            ConfigParser.setConfigFile(helper.getParamValue(SeLionGridConstants.SELION_CONFIG_ARG));
        }

        if (launchers.containsKey(role)) {
            logEnvironment();

            try {
                launchers.get(role).launch(args, LOGGER);
                type = launchers.get(role).getType();
            } catch (Exception e) {
                launchers.get(role).printUsage();
                e.printStackTrace();
            }
        } else {
            throw new GridConfigurationException("Unknown role: " + role);
        }
    }

    private static void printInfoAboutRoles(CommandLineOptionHelper helper) {
        if (helper.hasParamValue("-role")) {
            CliUtils.printWrappedLine("", "Error: the role '" + helper.getParamValue("-role")
                    + "' does not match a recognized server role\n");
        } else {
            CliUtils.printWrappedLine("",
                    "Error: -role option needs to be followed by the value that defines role of this component in the grid\n");
        }
        System.out.println("Selenium server can run in one of the following roles:\n"
                + "  hub         as a hub of a Selenium grid\n" 
                + "  node        as a node of a Selenium grid\n"
                + "  standalone  as a standalone server not being a part of a grid\n" + "\n"
                + "If -role option is omitted the server runs standalone\n");
        CliUtils.printWrappedLine("", "To get help on the options available for a specific role run the server"
                + " with -help option and the corresponding -role option value");
    }
}
