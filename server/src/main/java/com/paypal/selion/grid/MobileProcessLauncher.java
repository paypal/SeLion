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

    MobileProcessLauncher(String[] args) {
        this(args, null);
    }

    MobileProcessLauncher(String[] args, ProcessLauncherOptions options) {
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

        removeSeLionArgumentsAndValues(args);

        LOGGER.exiting(args.toString());
        return args.toArray(new String[args.size()]);
    }

    /**
     * Filter out all SeLion Grid args -- They do not apply. Finds the argument names and how many values it can
     * have through reflection.
     *
     * Note: this code does not concern itself with {@link Parameter}s that have
     * {@link Parameter#variableArity()}}
     */
    private List<String> removeSeLionArgumentsAndValues(List<String> args) {
        // assume all of the ProcessLauncherConfiguration and LauncherConfiguration arguments should not be forwarded
        Set<Field> fields = new HashSet<>();
        fields.addAll(Arrays.asList(ProcessLauncherConfiguration.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(LauncherConfiguration.class.getDeclaredFields()));

        for (Field field : fields) {
            // we need jcommander parameter fields only
            Parameter parameter = field.getAnnotation(Parameter.class);
            if (parameter == null) {
                continue;
            }

            // get the "arity" (how many values it can have on the command line) of the parameter/argument.
            // for example "-foo bar bar2"  --> argument = -foo --> arity = 2 --> values = {bar, bar2}
            final Class<?> fieldType = field.getType();
            final int arity = (parameter.arity() != -1) ? parameter.arity() :
                (fieldType.equals(Integer.class) || fieldType.equals(Long.class) || fieldType.equals(String.class) ||
                    fieldType.equals(int.class) || fieldType.equals(long.class)) ? 1 : 0;

            if (arity > 0) {
                for (String arg : args) {
                    // when the arg we are processing is one of the @Parameter names
                    if (Arrays.asList(parameter.names()).contains(arg)) {
                        // replace each value with ""
                        for (int x = 1; x <= arity; x += 1 ) {
                            args.set(args.indexOf(arg) + x, "");
                        }
                        // replace the argument with ""
                        args.set(args.indexOf(arg), "");
                    }
                }
                // remove all ""
                args.removeAll(Arrays.asList(""));
            } else {
                // the "arity" of the argument 0. there are no values to worry about.
                // remove all instances of the argument (and/or one of its names) from args
                args.removeAll(Arrays.asList(parameter.names()));
            }
        }

        return args;
    }
}
