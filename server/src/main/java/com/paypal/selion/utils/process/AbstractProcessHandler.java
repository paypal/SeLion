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

package com.paypal.selion.utils.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.paypal.selion.node.servlets.NodeForceRestartServlet;
import com.paypal.selion.pojos.ProcessInfo;
import com.paypal.selion.pojos.ProcessNames;

/**
 * This class captures most of the heavy lifting required in terms of finding the default set of processes which 
 * are to be ear marked for cleansing and also the core part of cleaning up these processes.
 * The sub classes of this class cater to the default recycling logic that comes pre-built in SeLion.
 *
 */
public abstract class AbstractProcessHandler {
    protected static final Logger log = Logger.getLogger(NodeForceRestartServlet.class.getName());

    protected List<ProcessInfo> getProcessInfo(String[] cmd, String delimiter, OSPlatform platform) throws IOException,
            InterruptedException {
        log.info("Fetching process information using the command : " + Arrays.toString(cmd));
        List<ProcessInfo> processToBeKilled = new ArrayList<ProcessInfo>();
        Process process = Runtime.getRuntime().exec(cmd);
        StreamGobbler output = new StreamGobbler(process.getInputStream());
        StreamGobbler error = new StreamGobbler(process.getErrorStream());
        output.start();
        error.start();
        process.waitFor();
        output.join();
        error.join();
        process.destroy();

        for (String eachLine : output.getContents()) {
            String[] eachProcessData = eachLine.split(delimiter);
            if (eachProcessData != null && eachProcessData.length >= 2) {
                ProcessInfo tProcess = null;
                switch (platform) {
                case NON_WINDOWS:
                    // In the output process name comes second
                    tProcess = new ProcessInfo(eachProcessData[1], eachProcessData[0]);
                    break;
                case WINDOWS:
                    // In the output process name comes first.
                    tProcess = new ProcessInfo(eachProcessData[0], eachProcessData[1]);
                    break;
                default:
                    break;
                }
                if (matches(tProcess.getProcessName())) {
                    processToBeKilled.add(tProcess);
                    break;
                }

            }
        }
        return processToBeKilled;
    }

    protected void killProcess(String[] killCommand, List<ProcessInfo> process) throws ProcessHandlerException {
        try {
            for (ProcessInfo eachProcess : process) {
                log.info("Killing process : " + eachProcess);
                String[] cmd = Arrays.copyOf(killCommand, killCommand.length + 1);
                cmd[cmd.length] = eachProcess.getProcessId();
                Process output = Runtime.getRuntime().exec(cmd);
                int returnCode = output.waitFor();
                if (returnCode != 0) {
                    log.info("Printing possible errors " + convertStreamToString(output.getErrorStream()));
                }
                output.destroy();
            }
            log.info("Successfully killed all stalled processes");

        } catch (IOException | InterruptedException e) {
            throw new ProcessHandlerException(e);
        }

    }

    private String convertStreamToString(InputStream isr) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(isr));
        String eachLine = null;
        StringBuffer sb = new StringBuffer();
        while ((eachLine = br.readLine()) != null) {
            sb.append(eachLine);
        }
        br.close();
        return sb.toString();
    }

    /**
     * @param image - A image name that should be checked against the image names represented by 
     * {@link ProcessNames} enum.
     * @return <code>true</code> if the image name matches for the given operating system.
     */
    protected abstract boolean matches(String image);

}
