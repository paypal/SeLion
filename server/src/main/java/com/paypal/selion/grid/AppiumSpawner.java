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

import java.io.IOException;

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
}
