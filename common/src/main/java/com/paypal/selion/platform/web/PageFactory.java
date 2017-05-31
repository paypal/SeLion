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

package com.paypal.selion.platform.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * A simple factory of {@link Page} objects
 */
public final class PageFactory {

    private PageFactory() {
        // Utility class. So hide the constructor
    }

    /**
     * Creates a instance of a {@link Page}.
     * 
     * @param in
     *            input stream of the Yaml File
     * @return an instance of the Page object
     * @throws IOException
     *             if unable to read from the {@link InputStream}. Does not close the {@link InputStream} when this
     *             occurs.
     */
    public static Page getPage(InputStream in) throws IOException {
        Constructor constructor = new Constructor(Page.class);

        TypeDescription typeDesc = new TypeDescription(Page.class);
        typeDesc.putListPropertyType("pageValidators", String.class);
        typeDesc.putListPropertyType("pageLoadingValidators", String.class);
        typeDesc.putMapPropertyType("elements", String.class, GUIElement.class);
        constructor.addTypeDescription(typeDesc);

        Yaml yamlFile = new Yaml(constructor);
        Page page = (Page) yamlFile.load(new BufferedReader(new InputStreamReader(in, "UTF-8")));

        try {
            in.close();
        } catch (IOException e) {
            // NOSONAR Do Nothing
        }

        return page;
    }
}
