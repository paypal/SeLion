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

public class SeLionGridHubConfiguration extends SeLionGridConfiguration {
    public static final String TYPE = "type";
    @Parameter(
            names = "-" + TYPE,
            description = "<String> type : Used to start a hub with the on demand sauce proxy. Accepted values : [sauce]"
    )
    public String type;

    @ParametersDelegate
    private GridHubConfiguration ghc = new GridHubConfiguration();

    @ParametersDelegate
    private ProcessLauncherConfiguration plc = new ProcessLauncherConfiguration();

    public GridHubConfiguration getGridHubConfiguration() {
        return ghc;
    }

    public void setGridHubConfiguration(GridHubConfiguration ghc) {
        this.ghc = ghc;
    }

    public ProcessLauncherConfiguration getProcessLauncherConfiguration() {
        return plc;
    }

    protected void mergeCustom() {
        super.mergeCustom(ghc);
    }
}
