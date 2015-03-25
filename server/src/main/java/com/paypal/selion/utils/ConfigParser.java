/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * A configuration utility that is internally used by SeLion to parse SeLion configuration json file.
 *
 */
public final class ConfigParser {
    private static ConfigParser parser = null;
    private JsonObject configuration = null;
    private static String CONFIG_FILE = null;

    /**
     * @return - A {@link ConfigParser} object that can be used to retrieve values from the Configuration
     * object as represented by the JSON file passed via the command line argument <b>-config</b>
     */
    public static ConfigParser getInstance() {
        if (parser == null) {
            parser = new ConfigParser();
        }
        return parser;
    }

    /**
     * Set the config file
     * @param file
     */
    public static void setConfigFile(String file) {
        CONFIG_FILE = file;
    }

    /**
     * @param key - The key for which the value is to be read for.
     * @return - an int that represents the value for the key
     */
    public int getInt(String key) {
        try {
            return configuration.get(key).getAsInt();
        } catch (JsonSyntaxException e) {
            throw new ConfigParserException(e);
        }
    }

    /**
     * @param key - The key for which the value is to be read for.
     * @return - a long that represents the value for the key
     */
    public long getLong(String key) {
        try {
            return configuration.get(key).getAsLong();
        } catch (JsonSyntaxException e) {
            throw new ConfigParserException(e);
        }
    }

    /**
     * @param key - The key for which the value is to be read for.
     * @return - a String that represents the value for the key
     */
    public String getString(String key) {
        try {
            return configuration.get(key).getAsString();
        } catch (JsonSyntaxException e) {
            throw new ConfigParserException(e);
        }
    }

    private ConfigParser() {
        try {
            readConfigFileContents();
        } catch (IOException e) {
            throw new ConfigParserException(e);
        }

    }

    private void readConfigFileContents() throws IOException {
        InputStream stream = null;
        if (isBlank(CONFIG_FILE)) {
            stream = this.getClass().getResourceAsStream(SeLionGridConstants.SELION_CONFIG);
        } else {
            File config = new File(CONFIG_FILE);
            String path = config.getAbsolutePath();
            checkArgument(config.exists(), path + " cannot be found on the local file system.");
            checkArgument(config.isFile(), path + " is not a valid file.");
            stream = new FileInputStream(config);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
        } finally {
            IOUtils.closeQuietly(br);
        }
        try {
            configuration = new JsonParser().parse(builder.toString()).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new ConfigParserException(e);
        }

    }

    private static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    private static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * A custom exception that represents all problems arising out of parsing configurations via {@link ConfigParser}
     * 
     */
    public static class ConfigParserException extends RuntimeException {

        private static final long serialVersionUID = 6165338826147933550L;

        public ConfigParserException(Throwable cause) {
            super(cause);
        }

    }

}
