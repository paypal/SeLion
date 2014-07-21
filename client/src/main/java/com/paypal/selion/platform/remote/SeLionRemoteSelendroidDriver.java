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

package com.paypal.selion.platform.remote;

import io.selendroid.SelendroidDriver;
import io.selendroid.SelendroidCapabilities;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.Beta;
import org.openqa.selenium.Capabilities;

/**
 * 
 * Specialized class to make the Selendroid driver transformation from an ordinary remote web driver
 * 
 */
@Beta
public class SeLionRemoteSelendroidDriver extends SelendroidDriver {
    private SelendroidCapabilities selendroidCapabilitiles = null;

    /**
     * This is the Constructor for the actual Selendroid driver transform
     * 
     * @param driver
     *            {@link RemoteWebDriver} with its command executer and session the new {@link SelendroidDriver} will
     *            function
     * @param capabilities
     *            driver capabilities of type {@link Capabilities}
     * @throws Exception
     */
    public SeLionRemoteSelendroidDriver(RemoteWebDriver driver, Capabilities capabilities)
            throws Exception {
        super(((HttpCommandExecutor) driver.getCommandExecutor()).getAddressOfRemoteServer(), null);
        setCommandExecutor(new HttpCommandExecutor(
                ((HttpCommandExecutor) driver.getCommandExecutor()).getAddressOfRemoteServer()));
        setSessionId(driver.getSessionId().toString());
        this.selendroidCapabilitiles = new SelendroidCapabilities(capabilities.asMap());
    }

    /**
     * This is the function Selenium internally uses for getting the driver capabilities
     */
    @Override
    public SelendroidCapabilities getCapabilities() {
        if (selendroidCapabilitiles == null) {
            selendroidCapabilitiles = new SelendroidCapabilities();
        }
        return selendroidCapabilitiles;
    }
    
    @Override
    protected void startClient() {
    }

    @Override
    protected void startSession(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
    }
}