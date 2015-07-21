/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import org.openqa.selenium.remote.Command;

/**
 * This class is invoked by {@link EventFiringCommandExecutor} when ever selenium command gets executed.
 */
public interface EventListener {
    /**
     * This method will be called by {@link EventFiringCommandExecutor} BEFORE executing each selenium command.
     *
     * @param command - A {@link org.openqa.selenium.remote.Command} that represents the command being executed by
     *                Selenium.
     *                In order to filter out specific commands you may extract the actual command via
     *                {@link org.openqa.selenium.remote.Command#getName()} and then compare it with the predefined set of
     *                commands available as strings in {@link org.openqa.selenium.remote.DriverCommand}
     */
    void beforeEvent(Command command);

    /**
     * This method will be called by {@link EventFiringCommandExecutor} AFTER executing each selenium command.
     *
     * @param command - A {@link org.openqa.selenium.remote.Command} that represents the command being executed by
     *                Selenium.
     *                In order to filter out specific commands you may extract the actual command via
     *                {@link org.openqa.selenium.remote.Command#getName()} and then compare it with the predefined set of
     *                commands available as strings in {@link org.openqa.selenium.remote.DriverCommand}
     */
    void afterEvent(Command command);
}
