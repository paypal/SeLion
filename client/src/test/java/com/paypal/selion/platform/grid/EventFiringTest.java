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

import static com.paypal.selion.platform.asserts.SeLionAsserts.assertTrue;

import org.openqa.selenium.remote.Command;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.configuration.Config;

public class EventFiringTest {

    @WebTest
    @Test()
    public void testCommandExecutorInstance() {
        assertTrue(Grid.driver().getCommandExecutor() instanceof EventFiringCommandExecutor);
    }
}

class EventFiringListener implements EventListener{

    @Override
    public void beforeEvent(Command command) {
        // TODO Auto-generated method stub
        System.out.println("Before Event:"+command);
        
    }

    @Override
    public void afterEvent(Command command) {
        // TODO Auto-generated method stub
        System.out.println("After Event:"+command);
        
    }

}

