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

package com.paypal.selion.utils.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.ProcessInfo;
import com.paypal.selion.pojos.ProcessNames;

import sun.management.VMManagement;

/**
 * This class captures most of the heavy lifting required in terms of finding the default set of processes which
 * are to be ear marked for cleansing and also the core part of cleaning up these processes.
 * The sub classes of this class cater to the default recycling logic that comes pre-built in SeLion.
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractProcessHandler {
    protected static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(AbstractProcessHandler.class);

    protected List<ProcessInfo> getProcessInfo(String[] cmd, String delimiter, OSPlatform platform) throws IOException,
            InterruptedException {
        LOGGER.info("Fetching process information using the command : " + Arrays.toString(cmd));
        Process process = Runtime.getRuntime().exec(cmd);
        StreamGobbler output = new StreamGobbler(process.getInputStream());
        StreamGobbler error = new StreamGobbler(process.getErrorStream());
        output.start();
        error.start();
        process.waitFor();
        output.join();
        error.join();
        process.destroy();

        List<ProcessInfo> processToBeKilled = new ArrayList<ProcessInfo>();
        for (String eachLine : output.getContents()) {
            String[] eachProcessData = eachLine.split(delimiter);
            if (eachProcessData != null && eachProcessData.length >= 2) {
                ProcessInfo tProcess = null;
                switch (platform) {
                case UNIX:
                    // In the output process name comes second
                    tProcess = new ProcessInfo(eachProcessData[0], eachProcessData[1]);
                    break;
                default:
                    // (I.e Windows) The machineName, process name, PID.
                    tProcess = new ProcessInfo(eachProcessData[1], eachProcessData[2]);
                    break;
                }
                if (matches(tProcess.getProcessName())) {
                    processToBeKilled.add(tProcess);
                }

            }
        }
        return processToBeKilled;
    }

    protected void killProcess(String[] killCommand, List<ProcessInfo> process) throws ProcessHandlerException {
        try {
            for (ProcessInfo eachProcess : process) {
                LOGGER.info("Killing process: " + eachProcess);
                String[] cmd = Arrays.copyOf(killCommand, killCommand.length + 1);
                cmd[cmd.length - 1] = eachProcess.getProcessId();
                Process output = Runtime.getRuntime().exec(cmd);
                int returnCode = output.waitFor();
                if (returnCode != 0) {
                    LOGGER.info("Printing possible errors " + convertStreamToString(output.getErrorStream()));
                }
                output.destroy();
            }
            LOGGER.info("Successfully killed all stalled processes");

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


    /**
     * Gets the PID for the SeLion-Grid (main) process
     * 
     * @return the PID as an int
     * @throws ProcessHandlerException
     */
    protected int getCurrentProcessID() throws ProcessHandlerException {
        int pid;
        // Not ideal but using JNA failed on RHEL5.
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        Field jvm = null;
        try {
            jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            VMManagement mgmt = (VMManagement) jvm.get(runtime);
            Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
            pid_method.setAccessible(true);
            pid = (Integer) pid_method.invoke(mgmt);
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ProcessHandlerException(e);
        }
        return pid;
    }
}
