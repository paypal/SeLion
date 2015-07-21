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
import com.paypal.selion.platform.grid.WebDriverSessionHandler;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;

/**
 * This test class demonstrates SeLion's abilities to prevent browser time outs due to browser being left idle for a 
 * long time. This normally happens when a UI based test amidst its UI operations is required to perform some non UI operations
 * such as a DB call, or perform a remote ssh operation etc., which may take some time before it completes.
 * In the normal scenario such operations will cause the browser to be auto recycled because they are considered to be stale by the 
 * Grid [ Remember SeLion always works on the Grid model be it tests running on your local desktop or running against a remote execution
 * environment ]
 */
public class WebDriverSessionHandlerTest {

    @Test
    @WebTest
    public void openPage () throws ExecutionException, InterruptedException {
        Grid.driver().get("http://www.paypal.com");
        //We first instantiate the webdriver session handler by passing it the instance of a
        //webdriver that belongs to the current test method.
        WebDriverSessionHandler handler = new WebDriverSessionHandler(Grid.driver());
        //We now have the session handler start the monitoring. This causes the session handler
        //to keep polling the current browser constantly in a different thread and thus prevents
        //the web browser from being automatically cleaned up.
        handler.start();
        //In a real test, your NON UI operations such as interacting with a remote host machine
        // or perhaps doing something else, would go here. But for the sake of simplicity lets resort
        //to just using a Thread.sleep();
        Thread.sleep(1000 * 60); //We sleep for a minute
        //Once we are done with all of our NON UI operations, we stop the web driver session handler.
        handler.stop();
        //We now resume with our regular UI operations on the current browser.
        assertTrue(Grid.driver().getTitle() != null);

    }

}
