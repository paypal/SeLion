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
import com.paypal.selion.pojos.SeLionGridConstants;

public class ProcessLauncherConfiguration {
    public static final String NO_CONTINUOUS_RESTART = "noContinuousRestart";
    @Parameter(
        names = "-" + NO_CONTINUOUS_RESTART,
        description = "<Boolean> : Disables continuous restart of the SeLion grid sub-process"
    )
    public boolean noContinuousRestart;

    public static final String SELION_CONFIG = "selionConfig";
    @Parameter(
        names = "-" + SELION_CONFIG,
        description = "<String> filename : A SeLion Grid configuration JSON file"
    )
    public String selionConfig = SeLionGridConstants.SELION_CONFIG_FILE;
}
