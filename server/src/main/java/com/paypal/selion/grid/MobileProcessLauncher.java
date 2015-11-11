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
/*-------------------------------------------------------------------------------------------------------------------*\
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

import static com.paypal.selion.pojos.SeLionGridConstants.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paypal.selion.grid.ProcessLauncherOptions;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * A {@link RunnableLauncher} for mobile WebDrivers (appium, selendroid, and ios-driver). Adds the ability to process
 * default arguments for the mobile WebDrivers (appium, selendroid, and ios-driver) from the SeLion Grid JSON config
 * file.
 */
@Beta
class MobileProcessLauncher extends AbstractBaseProcessLauncher {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(MobileProcessLauncher.class);
    private static final String SEPARATOR = "\n----------------------------------\n";
    JsonObject defaultArgs;

    public MobileProcessLauncher(String[] args) {
        this(args, null);
    }

    public MobileProcessLauncher(String[] args, ProcessLauncherOptions options) {
        super();
        init(args, options);
    }
    
    /**
     * Get program arguments to pass
     *
     * @return the program arguments to pass represented as an array of {@link String}
     * @throws IOException
     */
    @Override
    String[] getProgramArguments() throws IOException {
        LOGGER.entering();
        List<String> args = new LinkedList<String>(Arrays.asList(super.getProgramArguments()));

        // add the defaults which we don't already have a value for
        for (Entry<String, JsonElement> entry : defaultArgs.entrySet()) {
            String key = entry.getKey();
            if (!args.contains(key)) {
                args.add(key);
                String val = defaultArgs.get(key).getAsString();
                if (StringUtils.isNotBlank(val)) {
                    args.add(val);
                }
            }
        }

        // filter out SeLion Grid specific args which do not apply
        List<String> filteredArgs = new LinkedList<String>();
        filteredArgs.add(SELION_CONFIG_ARG);
        filteredArgs.add(SELION_NOCONTINUOUS_ARG);

        for (String filter : filteredArgs) {
            if (!filter.equals(SELION_NOCONTINUOUS_ARG)) {
                args.remove(args.indexOf(filter) + 1);
            }
            args.remove(filter);
        }

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    void printUsageInfo() {
        StringBuilder usage = new StringBuilder();
        usage.append(SEPARATOR);
        usage.append("To use SeLion Grid");
        usage.append(SEPARATOR);
        usage.append("\n");
        usage.append("Usage: java [system properties] -jar SeLion-Grid.jar [options] \n");
        usage.append("            [driver options] \n");
        usage.append("\n");
        usage.append("  Options:\n");
        usage.append("    " + SELION_CONFIG_ARG + " <config file name>: \n");
        usage.append("       A SeLion Grid configuration JSON file \n");
        usage.append("    " + SELION_NOCONTINUOUS_ARG + "\n");
        usage.append("       Disable continuous restarting of node/hub sub-process \n");
        usage.append("\n");
        usage.append("  Driver Options: \n");
        usage.append("    Any valid driver (appium, selendroid, or ios-driver) dash option(s). \n");
        usage.append("\n");
        usage.append("  System Properties: \n");
        usage.append("    -DselionHome=<folderPath>: \n");
        usage.append("       Path of SeLion home directory. Defaults to \n");
        usage.append("       <user.home>/.selion/ \n");
        usage.append("    -D[property]=[value]: \n");
        usage.append("       Any other System Property you wish to pass to the JVM \n");

        System.out.print(usage.toString());
    }
}
