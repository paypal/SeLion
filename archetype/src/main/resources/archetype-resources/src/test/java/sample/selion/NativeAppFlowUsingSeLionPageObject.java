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

import org.testng.annotations.Test;
import com.paypal.selion.reports.runtime.MobileReporter;
import static org.testng.Assert.assertTrue;
import com.paypal.selion.annotations.MobileTest;
import ${package}.sample.NativeAppTestPage;

/**
 * This test class demonstrates how to use SeLion PageObject model for running tests against a Native  iOS app.
 *
 */
public class NativeAppFlowUsingSeLionPageObject {

    // Through this annotation we let SeLion know that we would be needing an iOS Simulator spawned and made ready.
    @MobileTest(appName = "InternationalMountains")
    @Test
    public void readTextOnApp() {
        // We are now creating a page object that represents the actual test page on the IOS App.
        // In our yaml file which resides under src/main/resources/GUIData our localization value is being
        // given as "english". The default value that SeLion assumes would be "US". So we have two options
        // 1. We instantiate the page by passing in the value of 'US' which is how we are going to be
        // dealing with our localizations.
        // 2. We can set this at the entire JVM level by passing in the value via the JVM argument
        // -DSELION_SITE_LOCALE=<locale_value>
        // 3. We can set this at a specific <test> level by setting the parameter
        // <parameter name="siteLocale" value="locale_value_to_be_set"/> in the suite xml file.
        NativeAppTestPage samplePage = new NativeAppTestPage("english");
        
        //The NativeAppTestPage.java gets its data from NativeAppTestPage.yaml"

        // Navigating to the Mountain List page to click on the first Mountain
        samplePage.getSampleUIATableCell().click();

        // Once landed on the Mountain 1 page, reading the text
        assertTrue(samplePage.getSampleUIAStaticText().getAttribute("name")!=null);
        MobileReporter.log(samplePage.getSampleUIAStaticText().getAttribute("name"), true);
    }

}
