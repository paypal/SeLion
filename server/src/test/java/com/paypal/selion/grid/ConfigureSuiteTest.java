/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015-2016 PayPal                                                                                     |
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

package com.paypal.selion.grid;

import java.io.File;

import org.testng.annotations.BeforeSuite;

public class ConfigureSuiteTest {
    @BeforeSuite(alwaysRun = true)
    public void setUpBeforeSuite() throws Exception {
        // Should compute to "{selion-project-dir}/server/target/.selion"
        System.setProperty("selionHome", new File(ConfigureSuiteTest.class.getResource("/").getPath())
                .getAbsoluteFile().getParent() + "/.selion");

        ThreadedLauncher launcher;

        // Init a launcher to ensure firstTimeSetup() is complete for the new selionHome.
        launcher = new ThreadedLauncher(new String[] { "-role", "hub" });
        // put the hub config files in place too
        launcher.getProgramArguments();

        // also ensure the node files are in place
        launcher = new ThreadedLauncher(new String[] { "-role", "node" });
        launcher.getProgramArguments();

        // also ensure the sauce proxy/hub files are in place
        launcher = new ThreadedLauncher(new String[] { "-role", "hub", "-type", "sauce" });
        launcher.getProgramArguments();
    }
}
