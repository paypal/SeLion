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

package ${package}.sample.selion;

import com.paypal.selion.annotations.WebTest;
import com.paypal.selion.platform.grid.Grid;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This test class how to share the same web browser instance between multiple test methods.
 */
public class WebSessionSharingTest {

    private String sessionId;

    @Test
    //We indicate to SeLion that the browser window is NOT to be closed by setting the
    // keepSessionOpen attribute to true in the @WebTest annotation.
    @WebTest(keepSessionOpen = true)
    public void testMethodA () {
        Grid.driver().get("http://www.paypal.com");
        //Session ID is WebDriver's way of tracking a specific browser instance.
        // Lets save that to the class's data member so that we can run an assert in the next
        // method to prove that its the same browser session.
        RemoteWebDriver rwd = (RemoteWebDriver) Grid.driver().getWrappedDriver();
        sessionId = rwd.getSessionId().toString();
        assertTrue(Grid.driver().getTitle() != null);
    }

    //For web session sharing to work properly, we need to add a dependency on the method which
    //is going to leave the browser session as is. So in this case, the method is "testMethodA"
    @Test(dependsOnMethods = {"testMethodA"})
    //We now need to indicate to SeLion that it shouldn't open up a new browser instance, but
    //it should instead use the browser that was left open by the method on which the current
    //test method depends on viz., "testMethodA". We do this by setting the openNewSession
    //attribute to false.
    @WebTest(openNewSession = false)
    public void testMethodB () {
        Grid.driver().get("http://www.ebay.com");
        RemoteWebDriver rwd = (RemoteWebDriver) Grid.driver().getWrappedDriver();
        assertEquals(rwd.getSessionId().toString(), sessionId);
    }
}
