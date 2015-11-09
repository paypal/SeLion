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

package com.paypal.selion.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * A configuration utility that is internally used by SeLion to parse SeLion configuration json file.
 *
 */
public final class ConfigParser {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(ConfigParser.class);
    private static ConfigParser parser = new ConfigParser();
    private JsonObject configuration;
    private static String configFile;

    /**
     * @return A {@link ConfigParser} object that can be used to retrieve values from the Configuration object as
     *         represented by the JSON file passed via the JVM argument <b>SeLionConfig</b>
     */
    public static ConfigParser parse() {
        LOGGER.exiting(parser.toString());
        return parser;
    }

    /**
     * Set the config file
     * 
     * @param file
     *            the SeLion Grid config file to use
     */
    public static void setConfigFile(String file) {
        LOGGER.entering(file);
        configFile = file;
    }

    /**
     * @param key
     *            The key for which the value is to be read for.
     * @return an int that represents the value for the key.
     */
    public int getInt(String key) {
        LOGGER.entering(key);
        try {
            return configuration.get(key).getAsInt();
        } catch (JsonSyntaxException | NullPointerException e) { // NOSONAR
            throw new ConfigParserException(e);
        }
    }

    /**
     * @param key
     *            The key for which the value is to be read for.
     * @param defaultVal
     *            default value to use if the key does not exist or has an malformed value.
     * @return an int that represents the value for the key or the defaultVal if no such key exists.
     */
    public int getInt(String key, int defaultVal) {
        LOGGER.entering(new Object[] { key, defaultVal });
        try {
            return configuration.get(key).getAsInt();
        } catch (JsonSyntaxException | NullPointerException e) { // NOSONAR
            return defaultVal;
        }
    }

    /**
     * @param key
     *            The key for which the value is to be read for.
     * @return a long that represents the value for the key.
     */
    public long getLong(String key) {
        LOGGER.entering(key);
        try {
            return configuration.get(key).getAsLong();
        } catch (JsonSyntaxException | NullPointerException e) { // NOSONAR
            throw new ConfigParserException(e);
        }
    }

    /**
     * @param key
     *            The key for which the value is to be read for.
     * @param defaultVal
     *            default value to use if the key does not exist or has an malformed value.
     * @return a long that represents the value for the key or the defaultVal if no such key exists.
     */
    public long getLong(String key, long defaultVal) {
        LOGGER.entering(new Object[] { key, defaultVal });
        try {
            return configuration.get(key).getAsLong();
        } catch (JsonSyntaxException | NullPointerException e) { // NOSONAR
            return defaultVal;
        }
    }

    /**
     * @param key
     *            The key for which the value is to be read for.
     * @return a String that represents the value for the key.
     */
    public String getString(String key) {
        LOGGER.entering(key);
        try {
            return configuration.get(key).getAsString();
        } catch (JsonSyntaxException | NullPointerException e) { // NOSONAR
            throw new ConfigParserException(e);
        }
    }

    /**
     * @param key
     *            The key for which the value is to be read for.
     * @param defaultVal
     *            default value to use if the key does not exist or has an malformed value.
     * @return a String that represents the value for the key or the defaultVal if no such key exists.
     */
    public String getString(String key, String defaultVal) {
        LOGGER.entering(new Object[] { key, defaultVal });
        try {
            return configuration.get(key).getAsString();
        } catch (JsonSyntaxException | NullPointerException e) { // NOSONAR
            return defaultVal;
        }
    }

    /**
     * @param key
     *            The key for which the value is to be read for.
     * @return a {@link JsonObject} that represents the value for the key.
     */
    public JsonObject getJsonObject(String key) {
        LOGGER.entering(key);
        try {
            return configuration.get(key).getAsJsonObject();
        } catch (JsonSyntaxException | NullPointerException e) { // NOSONAR
            throw new ConfigParserException(e);
        }
    }

    /**
     * @param key
     *            The key for which the value is to be read for.
     * @param defaultVal
     *            default value to use if the key does not exist or has an malformed value.
     * @return a {@link JsonObject} that represents the value for the key or the defaultVal if no such key exists.
     */
    public JsonObject getJsonObject(String key, JsonObject defaultVal) {
        LOGGER.entering(new Object[] { key, defaultVal.toString() });
        try {
            return configuration.get(key).getAsJsonObject();
        } catch (JsonSyntaxException | NullPointerException e) { // NOSONAR
            return defaultVal;
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
        LOGGER.entering();
        InputStream stream = null;
        if (StringUtils.isBlank(configFile)) {
            LOGGER.fine("Config file will be loaded as a resource.");
            stream = this.getClass().getResourceAsStream(SeLionGridConstants.SELION_CONFIG_FILE_RESOURCE);
        } else {
            File config = new File(configFile);
            String path = config.getAbsolutePath();
            checkArgument(config.exists(), path + " cannot be found on the local file system.");
            checkArgument(config.isFile(), path + " is not a valid file.");
            LOGGER.fine("Config file will be loaded from file " + configFile);
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
        LOGGER.exiting();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConfigParser [configuration=");
        builder.append(configuration.toString());
        builder.append(", configFile=");
        builder.append(configFile);
        builder.append("]");
        return builder.toString();
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
