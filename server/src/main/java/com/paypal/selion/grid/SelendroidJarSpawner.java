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

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.JCommander;
import com.google.common.annotations.Beta;
import com.google.gson.JsonParser;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

/**
 * A {@link MobileProcessLauncher} for Selendroid stand alone. This class launches the selendroid main class as
 * specified in the SeLion Grid JSON config file. If no class is defined, it attempts to launch
 * <strong>io.selendroid.standalone.SelendroidLauncher</strong>.
 */
@Beta
public final class SelendroidJarSpawner extends MobileProcessLauncher {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(SelendroidJarSpawner.class);
    private String mainClass;

    public SelendroidJarSpawner(String[] args) {
        this(args, null);
    }

    public SelendroidJarSpawner(String[] args, ProcessLauncherOptions options) {
        super(args, options);
        setType(InstanceType.SELENDROID);
    }

    public static void main(String[] args) {
        new SelendroidJarSpawner(args).run();
    }

    @Override
    public void run() {
        mainClass = ConfigParser.parse()
                .getString("selendroidMainClass", "io.selendroid.standalone.SelendroidLauncher");
        defaultArgs = ConfigParser.parse().getJsonObject("selendroidDefaultArgs",
                new JsonParser().parse("{}").getAsJsonObject());

        // if we have an empty or null mainClass, then don't continue
        if (StringUtils.isEmpty(mainClass)) {
            throw new IllegalStateException(SelendroidJarSpawner.class.getSimpleName() + " requires a main class.");
        }
        super.run();
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
        cmdLine.addArguments(getJavaClassPathArguments("selendroid-", mainClass));

        // add the program argument / dash options
        cmdLine.addArguments(getProgramArguments());

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }

    void printUsageInfo() {
        StringBuilder usage = new StringBuilder();
        usage.append(SEPARATOR);
        usage.append("To use SeLion Grid with Selendroid");
        usage.append(SEPARATOR);

        usage.append("Usage: java [system properties] \\\n");
        usage.append("            -cp SeLion-Grid.jar \\\n");
        usage.append("            com.paypal.selion.grid.SelendroidJarSpawner \\\n");
        usage.append("            [options] [driver options] \n");

        new JCommander(new ProcessLauncherConfiguration()).usage(usage);
        final int start = usage.indexOf("Usage: <main class> [options]");
        final int length = "Usage: <main class> [options]".length();
        usage.replace(start, start + length, "");

        usage.append("  Driver Options: \n");
        usage.append("    Any valid Selendroid dash option(s). \n");
        usage.append("\n");
        usage.append("  System Properties: \n");
        usage.append("    -DselionHome=<folderPath>: \n");
        usage.append("       Path of SeLion home directory. Defaults to <user.home>/.selion2/ \n");
        usage.append("    -D[property]=[value]: \n");
        usage.append("       Any other System Property you wish to pass to the JVM \n");

        System.out.print(usage.toString());
    }
}
