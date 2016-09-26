/*-------------------------------------------------------------------------------------------------------------------*\
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

package com.paypal.selion.grid;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.SystemUtils;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * This is a stand alone class which sets up SeLion dependencies and spawns {@link SeLionGridLauncherV3}.
 */
public final class JarSpawner extends AbstractBaseProcessLauncher {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(JarSpawner.class);

    public JarSpawner(String[] args) {
        this(args, null);
    }

    public JarSpawner(String[] args, ProcessLauncherOptions options) {
        super();
        init(args, options);
    }

    public static void main(String[] args) {
        new JarSpawner(args).run();
    }

    /**
     * Print the usage of SeLion Grid jar
     */
    void printUsageInfo() {
        StringBuilder usage = new StringBuilder();
        usage.append("  System Properties: \n");
        usage.append("    -DselionHome=<folderPath>: \n");
        usage.append("       Path of SeLion home directory. Defaults to <user.home>/.selion/ \n");
        usage.append("    -D[property]=[value]: \n");
        usage.append("       Any other System Property you wish to pass to the JVM \n");

        System.out.print(usage.toString());
    }

    @Override
    void startProcess(boolean squelch) throws IOException {
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
        cmdLine.addArguments(getJavaClassPathArguments("selenium-", SeLionGridLauncherV3.class.getName()));

        // add the program argument / dash options
        cmdLine.addArguments(getProgramArguments());

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }

    @Override
    String[] getJavaSystemPropertiesArguments() throws IOException {
        LOGGER.entering();
        List<String> args = new LinkedList<>();

        // include everything a typical process launcher would add for a java process
        args.addAll(Arrays.asList(super.getJavaSystemPropertiesArguments()));

        // include the WebDriver binary paths for Chromedriver, IEDriver, and PhantomJs, GeckoDriver
        args.addAll(Arrays.asList(getWebDriverBinarySystemPropertiesArguments()));

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    private String[] getWebDriverBinarySystemPropertiesArguments() {
        LOGGER.entering();
        List<String> args = new LinkedList<>();
        if (getLauncherOptions().isIncludeWebDriverBinaryPaths()
                && (getType().equals(InstanceType.SELENIUM_NODE) || getType().equals(InstanceType.SELENIUM_STANDALONE))) {
            // Make sure we setup WebDriver binary paths for the child process
            if (SystemUtils.IS_OS_WINDOWS && System.getProperty(SeLionConstants.WEBDRIVER_IE_DRIVER_PROPERTY) == null) {
                args.add("-D" + SeLionConstants.WEBDRIVER_IE_DRIVER_PROPERTY + "="
                        + SeLionConstants.SELION_HOME_DIR + SeLionConstants.IE_DRIVER);
            }
            if (SystemUtils.IS_OS_WINDOWS && System.getProperty(SeLionConstants.WEBDRIVER_EDGE_DRIVER_PROPERTY) == null) {
                args.add("-D" + SeLionConstants.WEBDRIVER_EDGE_DRIVER_PROPERTY + "=" + SeLionConstants.SELION_HOME_DIR
                        + SeLionConstants.EDGE_DRIVER);
            }
            if (System.getProperty(SeLionConstants.WEBDRIVER_CHROME_DRIVER_PROPERTY) == null) {
                args.add("-D" + SeLionConstants.WEBDRIVER_CHROME_DRIVER_PROPERTY + "="
                        + SeLionConstants.SELION_HOME_DIR + SeLionConstants.CHROME_DRIVER);
            }
            if (System.getProperty(SeLionConstants.WEBDRIVER_PHANTOMJS_DRIVER_PROPERTY) == null) {
                args.add("-D" + SeLionConstants.WEBDRIVER_PHANTOMJS_DRIVER_PROPERTY + "="
                        + SeLionConstants.SELION_HOME_DIR + SeLionConstants.PHANTOMJS_DRIVER);
            }
            if (System.getProperty(SeLionConstants.WEBDRIVER_GECKO_DRIVER_PROPERTY) == null) {
                args.add("-D" + SeLionConstants.WEBDRIVER_GECKO_DRIVER_PROPERTY + "="
                        + SeLionConstants.SELION_HOME_DIR + SeLionConstants.GECKO_DRIVER);
            }

        }
        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }
}
