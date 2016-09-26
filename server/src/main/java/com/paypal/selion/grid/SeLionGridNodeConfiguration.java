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

import com.beust.jcommander.ParametersDelegate;

import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;

/**
 * An extension of {@link SeLionGridConfiguration} for "nodes" which includes SeLion Grid
 * {@link ProcessLauncherConfiguration} configuration, and Selenium {@link GridNodeConfiguration} configuration
 */
public class SeLionGridNodeConfiguration extends SeLionGridConfiguration {
    /**
     * The Selenium {@link GridNodeConfiguration}
     */
    @ParametersDelegate
    GridNodeConfiguration gridNodeConfiguration = new GridNodeConfiguration();

    public GridNodeConfiguration getGridNodeConfiguration() {
        return gridNodeConfiguration;
    }

    public void setGridNodeConfiguration(GridNodeConfiguration gridNodeConfiguration) {
        this.gridNodeConfiguration = gridNodeConfiguration;
    }

    /**
     * merges the "custom:" { ... } values for this configuration
     */
    protected void mergeCustom() {
        super.mergeCustom(gridNodeConfiguration);
    }
}
