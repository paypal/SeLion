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

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.StringUtils;

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

}
