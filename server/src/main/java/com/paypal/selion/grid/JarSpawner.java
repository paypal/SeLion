/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.SystemUtils;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * This is a stand alone class which sets up SeLion dependencies and spawns {@link SeLionGridLauncher}.
 */
public final class JarSpawner extends AbstractBaseProcessLauncher {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(JarSpawner.class);
    private static final String SEPARATOR = "\n----------------------------------\n";

    public JarSpawner(String[] args) {
        this(args, null);
    }

    public JarSpawner(String[] args, ProcessLauncherOptions options) {
        super();
        init(args, options);
    }

    public static final void main(String[] args) {
        new JarSpawner(args).run();
    }

    /**
     * Print the usage of SeLion Grid jar
     */
    final void printUsageInfo() {
        StringBuilder usage = new StringBuilder();
        usage.append(SEPARATOR);
        usage.append("To use SeLion Grid");
        usage.append(SEPARATOR);
        usage.append("\n");
        usage.append("Usage: java [system properties] -jar SeLion-Grid.jar [options] \n");
        usage.append("            [selenium options] \n");
        usage.append("\n");
        usage.append("  Options:\n");
        usage.append("    " + TYPE_ARG + " <sauce>: \n");
        usage.append("       Used with '-role hub' to start a hub with the on demand \n");
        usage.append("       sauce proxy \n");
        usage.append("    " + SELION_CONFIG_ARG + " <config file name>: \n");
        usage.append("       A SeLion Grid configuration JSON file \n");
        usage.append("    " + SELION_NOCONTINUOUS_ARG + "\n");
        usage.append("       Disable continuous restarting of node/hub sub-process \n");
        usage.append("\n");
        usage.append("  Selenium Options: \n");
        usage.append("    Any valid Selenium standalone or grid dash option(s). \n");
        usage.append("       E.g. '-role hub -port 2054' -- To start a hub on port 2054. \n");
        usage.append("\n");
        usage.append("  System Properties: \n");
        usage.append("    -DselionHome=<folderPath>: \n");
        usage.append("       Path of SeLion home directory. Defaults to \n");
        usage.append("       <user.home>/.selion/ \n");
        usage.append("    -D[property]=[value]: \n");
        usage.append("       Any other System Property you wish to pass to the JVM \n");

        System.out.print(usage.toString());
    }

    @Override
    final void startProcess(boolean squelch) throws IOException {
        setCommandLine(createJavaCommandForChildProcess());
        super.startProcess(squelch);
    }

    /**
     * This method load the default arguments required to spawn SeLion Grid/Node
     *
     * @return {@link CommandLine}
     * @throws IOException
     */
    private CommandLine createJavaCommandForChildProcess() throws IOException {
        LOGGER.entering();

        // start command with java
        CommandLine cmdLine = CommandLine.parse("java");

        // add the -D system properties
        cmdLine.addArguments(getJavaSystemPropertiesArguments());

        // Set the classpath
        cmdLine.addArguments(getJavaClassPathArguments("selenium-", SeLionGridLauncher.class.getName()));

        // add the program argument / dash options
        cmdLine.addArguments(getProgramArguments());

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }

    @Override
    String[] getJavaSystemPropertiesArguments() throws IOException {
        LOGGER.entering();
        List<String> args = new LinkedList<String>();
        
        // include everything a typical process launcher would add for a java process
        args.addAll(Arrays.asList(super.getJavaSystemPropertiesArguments()));

        // include the WebDriver binary paths for Chromedriver, IEDriver, and PhantomJs
        args.addAll(Arrays.asList(getWebDriverBinarySystemPropertiesArguments()));

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    private String[] getWebDriverBinarySystemPropertiesArguments() {
        LOGGER.entering();
        List<String> args = new LinkedList<String>();
        if (getLauncherOptions().isIncludeWebDriverBinaryPaths()
                && (getType().equals(InstanceType.SELENIUM_NODE) || getType().equals(InstanceType.SELENIUM_STANDALONE))) {
            // Make sure we setup WebDriver binary paths for the child process
            if (SystemUtils.IS_OS_WINDOWS && System.getProperty(SeLionConstants.WEBDRIVER_IE_DRIVER_PROPERTY) == null) {
                args.add("-D" + SeLionConstants.WEBDRIVER_IE_DRIVER_PROPERTY + "="
                        + SeLionConstants.SELION_HOME_DIR + SeLionConstants.IE_DRIVER);
            }
            if (System.getProperty(SeLionConstants.WEBDRIVER_CHROME_DRIVER_PROPERTY) == null) {
                args.add("-D" + SeLionConstants.WEBDRIVER_CHROME_DRIVER_PROPERTY + "="
                        + SeLionConstants.SELION_HOME_DIR + SeLionConstants.CHROME_DRIVER);
            }
            if (System.getProperty(SeLionConstants.WEBDRIVER_PHANTOMJS_DRIVER_PROPERTY) == null) {
                args.add("-D" + SeLionConstants.WEBDRIVER_PHANTOMJS_DRIVER_PROPERTY + "="
                        + SeLionConstants.SELION_HOME_DIR + SeLionConstants.PHANTOMJS_DRIVER);
            }
        }
        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }
}
