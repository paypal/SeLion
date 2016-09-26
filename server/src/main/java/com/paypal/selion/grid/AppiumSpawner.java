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

import com.beust.jcommander.JCommander;
import com.google.common.annotations.Beta;

import org.apache.commons.exec.CommandLine;

import com.google.gson.JsonParser;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

/**
 * A {@link MobileProcessLauncher} for appium. Requires appium to be installed and in the PATH.
 */
@Beta
final class AppiumSpawner extends MobileProcessLauncher {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(AppiumSpawner.class);

    public AppiumSpawner(String[] args) {
        this(args, null);
    }

    public AppiumSpawner(String[] args, ProcessLauncherOptions options) {
        super(args, options);
        setType(InstanceType.APPIUM);
    }

    public static void main(String[] args) {
        new AppiumSpawner(args).run();
    }

    @Override
    public void run() {
        defaultArgs = ConfigParser.parse().getJsonObject("appiumDefaultArgs",
                new JsonParser().parse("{}").getAsJsonObject());
        super.run();
    }

    @Override
    void startProcess(boolean squelch) throws IOException {
        setCommandLine(createCommandForChildProcess());
        super.startProcess(squelch);
    }

    /**
     * This method loads the default arguments required to spawn appium
     *
     * @return {@link CommandLine}
     * @throws IOException
     */
    private CommandLine createCommandForChildProcess() throws IOException {
        LOGGER.entering();

        CommandLine cmdLine = CommandLine.parse("appium");

        // add the program argument / dash options
        cmdLine.addArguments(getProgramArguments());

        LOGGER.exiting(cmdLine.toString());
        return cmdLine;
    }
    

    void printUsageInfo() {
        StringBuilder usage = new StringBuilder();
        usage.append(SEPARATOR);
        usage.append("To use SeLion Grid with Appium");
        usage.append(SEPARATOR);
        
        usage.append("Usage: java [system properties] \\\n");
        usage.append("            -cp SeLion-Grid.jar \\\n");
        usage.append("            com.paypal.selion.grid.AppiumSpawner \\\n");
        usage.append("            [options] [driver options] \n");

        new JCommander(new ProcessLauncherConfiguration()).usage(usage);
        final int start = usage.indexOf("Usage: <main class> [options]");
        final int length = "Usage: <main class> [options]".length();
        usage.replace(start, start + length, "");

        usage.append("  Driver Options: \n");
        usage.append("    Any valid Appium dash option(s). \n");
        usage.append("\n");
        usage.append("  System Properties: \n");
        usage.append("    -DselionHome=<folderPath>: \n");
        usage.append("       Path of SeLion home directory. Defaults to <user.home>/.selion/ \n");
        usage.append("    -D[property]=[value]: \n");
        usage.append("       Any other System Property you wish to pass to the JVM \n");

        System.out.print(usage.toString());
    }
}
