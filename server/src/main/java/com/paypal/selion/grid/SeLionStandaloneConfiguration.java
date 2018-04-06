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

import org.openqa.grid.internal.utils.configuration.StandaloneConfiguration;

/**
 * SeLion Grid "standalone" configuration which includes SeLion Grid {@link ProcessLauncherConfiguration} configuration,
 * and Selenium {@link StandaloneConfiguration} configuration
 */
public class SeLionStandaloneConfiguration {
    /**
     * The Selenium {@link StandaloneConfiguration}
     */
    @ParametersDelegate
    StandaloneConfiguration standaloneConfiguration = new StandaloneConfiguration();

    /**
     * The SeLion {@link ProcessLauncherConfiguration}
     */
    @ParametersDelegate
    ProcessLauncherConfiguration processLauncherConfiguration = new ProcessLauncherConfiguration();

    public StandaloneConfiguration getStandaloneConfiguration() {
        return standaloneConfiguration != null ? standaloneConfiguration : new StandaloneConfiguration();
    }

    public void setStandaloneConfiguration(StandaloneConfiguration standaloneConfiguration) {
        this.standaloneConfiguration = standaloneConfiguration;
    }

    public ProcessLauncherConfiguration getProcessLauncherConfiguration() {
        return processLauncherConfiguration != null ? processLauncherConfiguration : new ProcessLauncherConfiguration();
    }

    public void setProcessLauncherConfiguration(ProcessLauncherConfiguration processLauncherConfiguration) {
        this.processLauncherConfiguration = processLauncherConfiguration;
    }
}
