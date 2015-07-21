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

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.Response;

/**
 * This class is a decorator for any {@link CommandExecutor} and calls the instance of {@link EventListener} before and
 * after each command gets executed
 * 
 */
public class EventFiringCommandExecutor implements CommandExecutor {
    private List<EventListener> listeners;
    private CommandExecutor commandExecutor;
    
    /**
     * @return the {@link EventListener}s
     */
    protected List<EventListener> getListeners() {
        return listeners;
    }

    /**
     * Set the {@link EventListener}s
     * @param listeners the {@link EventListener}s to set
     */
    protected void setListeners(List<EventListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * @return the {@link CommandExecutor}
     */
    protected CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    /**
     * Set the {@link CommandExecutor}
     * @param commandExecutor the {@link CommandExecutor} to set
     */
    protected void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public EventFiringCommandExecutor(CommandExecutor commandExecutor, List<EventListener> listeners) {
        this.listeners = listeners;
        this.commandExecutor = commandExecutor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.remote.CommandExecutor#execute(org.openqa.selenium.remote.Command)
     */
    public Response execute(Command command) throws IOException {
        for (EventListener temp : listeners) {
            temp.beforeEvent(command);
        }
        Response res = commandExecutor.execute(command);

        for (EventListener temp : listeners) {
            temp.afterEvent(command);
        }

        return res;
    }
}
