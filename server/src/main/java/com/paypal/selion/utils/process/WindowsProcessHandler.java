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

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.paypal.selion.pojos.ProcessInfo;
import com.paypal.selion.pojos.ProcessNames;

/**
 * This class provides for a simple implementation that aims at providing the logic to fetch processes as represented by
 * {@link ProcessNames} and also in forcibly killing them on a Windows like environment.
 *
 */
public class WindowsProcessHandler extends AbstractProcessHandler implements ProcessHandler {
    private static final String DELIMITER = ",";

    public WindowsProcessHandler() {
        super();
        LOGGER.info("You have chosen to use a Windows Process Handler.");
    }

    @Override
    public List<ProcessInfo> potentialProcessToBeKilled() throws ProcessHandlerException {
        LOGGER.entering();
        int ourPid = getCurrentProcessID();

        // Find all processes names that are our direct children using our PID as the parent pid using wmic.
        // As well as iexplore process that are orphaned.
        // We want to kill only the direct child processes we have started. ("More +2" drops the csv header from output)
        // Note that we plan to kill all child process descendants in killProcess (with /T)
        String cmd = String.format("wmic process where (parentprocessid=%s or name=\"%s\") " +
                        "get name,processid /format:csv | more +2",
                String.valueOf(ourPid), ProcessNames.INTERNET_EXPLORER.getWindowsImageName());
        try {
            List<ProcessInfo> processToBeKilled = getProcessInfo(new String[] { "cmd.exe", "/C", cmd }, DELIMITER,
                    OSPlatform.WINDOWS);
            LOGGER.exiting(processToBeKilled.toString());
            return processToBeKilled;
        } catch (IOException | InterruptedException e) {
            throw new ProcessHandlerException(e);
        }
    }

    @Override
    public void killProcess(List<ProcessInfo> processes) throws ProcessHandlerException {
        String[] cmd = { "cmd.exe", "/C", "taskkill /F /T /PID" };
        super.killProcess(cmd, processes);
    }

    /**
     * @param image
     *            - The image name of the process
     * @return - <code>true</code> If the image name begins with any of the image names that are part of
     *         {@link ProcessNames} enum.
     */
    @Override
    protected boolean matches(String image) {
        LOGGER.entering(image);
        if (StringUtils.isEmpty(image)) {
            LOGGER.exiting(false);
            return false;
        }
        for (ProcessNames eachImage : ProcessNames.values()) {
            if (image.startsWith(eachImage.getWindowsImageName())) {
                LOGGER.exiting(true);
                return true;
            }
        }
        LOGGER.exiting(false);
        return false;
    }

}
