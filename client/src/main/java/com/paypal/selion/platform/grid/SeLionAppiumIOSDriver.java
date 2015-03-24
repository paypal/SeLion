/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

import io.appium.java_client.ios.IOSDriver;

import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;

/**
 * This class provide facility to add custom {@link CommandExecutor} to {@link IOSDriver}
 * 
 */
public class SeLionAppiumIOSDriver extends IOSDriver {

    public SeLionAppiumIOSDriver(URL url, Capabilities caps) {
        super(url, caps);
    }

    public SeLionAppiumIOSDriver(CommandExecutor commandExecutor, Capabilities caps, URL url) {
        super(url, caps);
        setCommandExecutor(commandExecutor);
    }

}