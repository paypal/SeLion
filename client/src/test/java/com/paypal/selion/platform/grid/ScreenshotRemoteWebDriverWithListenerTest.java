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

package com.paypal.selion.platform.grid;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.testng.annotations.Test;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.asserts.SeLionAsserts;
import com.paypal.selion.platform.grid.Grid;

public class ScreenshotRemoteWebDriverWithListenerTest {

    @Test
    @WebTest
    public void testEventTriggeringMechanism() {
        Grid.driver().get("http://www.google.com");
        SeLionAsserts.assertTrue(MyEventListener.getInstance().getWasListenerInvoked(), "Testing listener invocation");

    }

    //This is really an ugly way of getting hold of the instance of the webdriver event listener
    //There aren't any getter methods defined in EventFiringWebDriver
    public static class MyEventListener extends AbstractWebDriverEventListener {
        private boolean wasListenerInvoked = false;
        private static MyEventListener instanceTracker = null;

        public MyEventListener() {
            setInstance(this);
        }

        private static void setInstance(MyEventListener listener) {
            instanceTracker = listener;
        }

        public static MyEventListener getInstance() {
            return instanceTracker;
        }

        @Override
        public void beforeNavigateTo(String url, WebDriver driver) {
            super.beforeNavigateTo(url, driver);
            wasListenerInvoked = true;
        }

        public boolean getWasListenerInvoked() {
            return wasListenerInvoked;
        }

    }

}
