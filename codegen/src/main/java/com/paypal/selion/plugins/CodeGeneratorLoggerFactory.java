/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2017 PayPal                                                                                     |
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

package com.paypal.selion.plugins;

/**
 * A basic logger for the codegen maven mojo plugin
 */
public class CodeGeneratorLoggerFactory {
    private static CodeGeneratorLogger instance;

    public static void setLogger(CodeGeneratorLogger logger) {
        instance = logger;
    }

    public static CodeGeneratorLogger getLogger() {
        if (instance == null) {
            new IllegalStateException("The logger instance is not yet created.");
        }
        return instance;
    }
}
