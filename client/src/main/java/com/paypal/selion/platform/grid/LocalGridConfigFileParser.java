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

package com.paypal.selion.platform.grid;

import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.exception.GridConfigurationException;

import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.utilities.FileAssistant;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * A local parser that is used internally for parsing the configuration.
 * 
 */
class LocalGridConfigFileParser {

    private JSONObject request = null;
    private SimpleLogger logger = SeLionLogger.getLogger();

    public LocalGridConfigFileParser() {
        String fileName = Config.getConfigProperty(ConfigProperty.SELENIUM_LOCAL_GRID_CONFIG_FILE);
        logger.entering(fileName);
        try {
            request = new JSONObject(FileAssistant.readFile(fileName));
            logger.exiting(request);
        } catch (Exception e) {// NOSONAR
            // intentionally catching all exceptions here.
            String errorMsg = "An error occured while working with the JSON file : " + fileName + ". Root cause: ";
            logger.log(Level.SEVERE, errorMsg, e);
            throw new GridConfigurationException(errorMsg, e);
        }
    }

    /**
     * @return - retrieves the port from the local node configuration object.
     */
    int getPort() {
        try {
            JSONObject jsonConfig = request.getJSONObject("configuration");
            return jsonConfig.getInt("port");
        } catch (JSONException e1) {
            String errorMsg = "An error occured while working with the JSON file. Root cause: ";
            logger.log(Level.SEVERE, errorMsg, e1);
            throw new RuntimeException(errorMsg, e1);
        }
    }

    /**
     * @return - A {@link JSONObject} that represents the entire JSON config file contents.
     */
    JSONObject getRequest() {
        return request;
    }

}
