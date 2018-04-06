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

package com.paypal.selion.reader;

import com.paypal.selion.plugins.CodeGeneratorException;

import java.io.IOException;

/**
 * A Factory that is internally responsible for producing {@link AbstractYamlReader} instances which can either process
 * YAML V1 format (or) V2 format.
 * 
 */
// TODO Merge this with "clients" version of a class by the same name.. Move merged result to "common"
public final class YamlReaderFactory {
    private YamlReaderFactory() {
        // Utility class. Hide constructor.
    }

    public static AbstractYamlReader createInstance(String fileName) throws IOException {
        if (!fileName.endsWith("yaml") && !fileName.endsWith("yml")) {
            throw new IllegalArgumentException("Data file not supported : " + fileName);
        }
        AbstractYamlReader provider = new YamlV2Reader(fileName);
        if (!provider.isProcessed()) {
            provider = new YamlV1Reader(fileName);
        }
        if (!provider.isProcessed()) {
            throw new CodeGeneratorException("Error parsing document. Please check file contents for syntax errors.");
        }
        return provider;
    }

}
