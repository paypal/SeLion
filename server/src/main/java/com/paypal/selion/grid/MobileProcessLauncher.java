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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.Parameter;
import com.google.common.annotations.Beta;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * A {@link RunnableLauncher} for mobile WebDrivers (appium, selendroid, and ios-driver). Adds the ability to process
 * default arguments for the mobile WebDrivers (appium, selendroid, and ios-driver) from the SeLion Grid JSON config
 * file.
 */
@Beta
abstract class MobileProcessLauncher extends AbstractBaseProcessLauncher {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(MobileProcessLauncher.class);
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
        List<String> args = new LinkedList<>(Arrays.asList(super.getProgramArguments()));

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

        // filter out all SeLion Grid args -- They do not apply
        // Assume all of the ProcessLauncherConfiguration and LauncherConfiguration arguments should not be forwarded
        Set<Field> fields = new HashSet<Field>();
        fields.addAll(Arrays.asList(ProcessLauncherConfiguration.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(LauncherConfiguration.class.getDeclaredFields()));

        for (Field f : fields) {
            Parameter p = f.getAnnotation(Parameter.class);
            if (p == null) {
                continue;
            }
            if (!f.getType().equals(Boolean.class)) {
                for (String arg : args) {
                    if (Arrays.asList(p.names()).contains(arg)) {
                        args.set(args.indexOf(arg) + 1, "");
                        args.set(args.indexOf(arg), "");
                    }
                }
                args.removeAll(Arrays.asList(""));
            } else {
                args.removeAll(Arrays.asList(p.names()));
            }
        }

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }
}
