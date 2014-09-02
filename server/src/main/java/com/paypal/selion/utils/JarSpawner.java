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

package com.paypal.selion.utils;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;

import com.paypal.selion.logging.SeLionGridLogger;

/**
 * This is a standalone class that essentially helps spawn a jar given via the commandline along with the arguments that
 * are required by the jar.
 * 
 */
public class JarSpawner {

    private static final Logger logger = SeLionGridLogger.getLogger();

    public static void main(String[] args) throws ExecuteException, IOException, InterruptedException {
        ;
        long interval = ConfigParser.getInstance().getLong("defaultInterval");
        logger.info("Default interval check will be every " + interval + " ms");
        while (true) {
            FileDownloader.checkForDownloads();
            continuouslyRestart(args[0], interval);
            Thread.sleep(interval);
            logger.info("Application exited. Respawning the application again");
        }
    }

    /**
     * This method spawns a jar, and waits for it to exit [either cleanly or forcibly]
     * 
     * @param cmd
     *            - command to execute using jarspawner
     * @param interval
     *            - How often should the application check if the jar is still running or if it exit.
     * @throws ExecuteException
     * @throws IOException
     * @throws InterruptedException
     */
    public static void continuouslyRestart(String cmd, long interval) throws ExecuteException, IOException,
            InterruptedException {
        StringTokenizer st = new StringTokenizer(cmd, " ");
        boolean bCommand = false;
        CommandLine cmdLine = null;
        while (st.hasMoreTokens()) {
            if (!bCommand) {
                cmdLine = CommandLine.parse(st.nextToken());
                bCommand = true;
            } else {
                cmdLine.addArgument(st.nextToken(), false);
            }
        }
        logger.info("Executing command " + cmdLine.toString());

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler());
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        executor.execute(cmdLine, handler);
        while (!handler.hasResult()) {
            Thread.sleep(interval);
        }
        logger.info("Waking up");

        if (handler.hasResult()) {
            ExecuteException e = handler.getException();
            if (e != null) {
                logger.log(Level.SEVERE, handler.getException().getMessage(), handler.getException());
            }
        }
    }
}
