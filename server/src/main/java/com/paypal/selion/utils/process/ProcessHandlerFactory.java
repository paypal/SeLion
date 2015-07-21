/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

import com.google.common.base.Preconditions;
import com.paypal.selion.utils.ConfigParser;

/**
 * A simple factory class that produces instances of {@link ProcessHandler} as provided by the user via the JSON configuration
 * file.
 *
 */
public final class ProcessHandlerFactory {
    private ProcessHandlerFactory() {

    }

    public static ProcessHandler createInstance() {
        String customHandler = "";
        customHandler = ConfigParser.parse().getString("customProcessHandler");
        return createCustomHandlerInstance(customHandler);
    }

    private static ProcessHandler createCustomHandlerInstance(String className) {
        try {
            Class<?> c = Class.forName(className);
            Object instance = c.newInstance();
            Preconditions.checkArgument(instance instanceof ProcessHandler, className + " does not implement "
                    + ProcessHandler.class.getCanonicalName());
            return (ProcessHandler) instance;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
