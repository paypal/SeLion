/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;

/**
 * An extension of {@link SeLionGridConfiguration} for the "hub" which includes SeLion Grid
 * {@link ProcessLauncherConfiguration} configuration, and Selenium {@link GridHubConfiguration} configuration
 */
public class SeLionGridHubConfiguration extends SeLionGridConfiguration {
    public static final String TYPE = "type";
    public static final String TYPE_ARG =  "-" + TYPE;

    /**
     * The SeLion hub type.
     */
    @Parameter(
            names = TYPE_ARG,
            description = "<String> type : Used to start a hub with the on demand sauce proxy. Accepted values : [sauce]"
    )
    String type;

    /**
     * The Selenium {@link GridHubConfiguration}
     */
    @ParametersDelegate
    GridHubConfiguration gridHubConfiguration = new GridHubConfiguration();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GridHubConfiguration getGridHubConfiguration() {
        return gridHubConfiguration;
    }

    public void setGridHubConfiguration(GridHubConfiguration gridHubConfiguration) {
        this.gridHubConfiguration = gridHubConfiguration;
    }

    /**
     * merges the "custom:" { ... } values for this configuration
     */
    protected void mergeCustom() {
        super.mergeCustom(gridHubConfiguration);
    }
}
