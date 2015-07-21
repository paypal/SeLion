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

import static org.testng.Assert.assertTrue;

/**
 * This example will demonstrate a UI Test which always runs on firefox. Instructions on how to run this test locally on
 * your desktop.
 * <ul>
 * <li>If you are using eclipse, then make sure that you have setup the TestNG template XML viz.,
 * src/test/resources/selionConfig.xml via Project &gt; Properties &gt; TestNG.</li>
 * <li>If you are running from the maven command line, use the command
 * <pre>mvn clean test -Dtest=UIFlowDemoTest -DSELION_SELENIUM_RUN_LOCALLY=true </pre></li>
 * </ul>
 */
public class SimpleUIFlowDemoTest {

    @Test
    //You indicate to SeLion that you need a browser to be automatically opened up for your test by specifying
    //the @WebTest annotation. This will cause SeLion to automatically open up a browser for your test.
    //The WebTest annotation has an attribute named "browser" wherein you can specify a browser flavor of your
    //choice. Once a value is specified here, then SeLion guarantees that only that particular browser flavor
    //would be used irrespective of what has been provided via TestNG parameters or via JVM arguments.
    //One of the important things to be noted here is that the browser will get automatically closed
    //after the test method runs to completion (irrespective of whether it has failures or not)
    @WebTest(browser = "*firefox")
    public void openGooglePage () {
        //You can access the webdriver object that is specific to the current test via the
        //Grid.driver() method. But remember, if you use this from within a method which doesn't
        //have a @WebTest annotation, then you are going to run into NullPointerExceptions.
        Grid.driver().get("http://www.google.com");
        assertTrue(Grid.driver().getTitle().equalsIgnoreCase("google"));
        String browserFlavor = (String) Grid.driver().executeScript("return navigator.userAgent;");
        assertTrue(browserFlavor.toLowerCase().contains("firefox"));
    }

}
