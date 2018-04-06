/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016-2017 PayPal                                                                                     |
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

import com.beust.jcommander.JCommander;
import org.apache.commons.lang.StringUtils;

/**
 * Class for code generation goals
 */
public class CodeGeneratorMain {

    /**
     * Main Entry
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final CodeGeneratorConfig config = new CodeGeneratorConfig();
        final JCommander commander = new JCommander(config);
        commander.parse(args);

        if (config.help) {
            commander.usage();
            System.exit(0);
        }

        // make sure the logger is initialized
        CodeGeneratorLoggerFactory.setLogger(new CodeGeneratorSimpleLogger());

        // iff baseFolder is null or empty, reset it to the default value
        config.baseFolder = StringUtils.defaultIfEmpty(config.baseFolder, "GUIData");

        // create the helper with our config
        CodeGeneratorHelper helper = new CodeGeneratorHelper(config);

        if (CodeGeneratorLoggerFactory.getLogger().isDebugEnabled()) {
            helper.displayProjectInformation();
        }

        // register all custom element classes
        helper.registerCustomElements();

        // process the files.
        helper.processFiles();
    }
}
