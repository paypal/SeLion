/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This test class how to share the same web browser instance between multiple test methods.
 */
@Test(singleThreaded = true)
@WebTest
public class WebSessionSharingTest {

    private String sessionId;
    //need to set priority for each test method. 
    //priority is used to determine the order of execution of each test methods within this class
    @Test(priority = 1)
    public void testMethodA () {
        Grid.driver().get("http://www.paypal.com");
        //Session ID is WebDriver's way of tracking a specific browser instance.
        // Lets save that to the class's data member so that we can run an assert in the next
        // method to prove that its the same browser session.
        sessionId = Grid.driver().getSessionId().toString();
        assertTrue(Grid.driver().getTitle() != null);
    }

    @Test(priority = 2)
    public void testMethodB () {
        Grid.driver().get("http://www.ebay.com");
        assertEquals(Grid.driver().getSessionId().toString(), sessionId);
    }
}
