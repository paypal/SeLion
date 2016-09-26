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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.net.PortProber;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(singleThreaded = true, groups = { "android-only", "downloads-dependencies" })
public class SelendroidJarSpawnerTest {
    private Thread thread;
    private RunnableLauncher spawner;

    @BeforeClass
    public void beforeClass() {
        String host = new NetworkUtils().getIpOfLoopBackIp4();
        int port = PortProber.findFreePort();
        spawner = new SelendroidJarSpawner(new String[] { "-host", host, "-port", String.valueOf(port) },
                new ProcessLauncherConfiguration().setContinuouslyRestart(false).setIncludeJavaSystemProperties(false)
                        .setIncludeParentProcessClassPath(false));
        thread = new Thread(spawner);
    }

    @Test
    public void testStartServer() throws Exception {
        thread.start();

        // wait for it to start, max 120 seconds
        int attempts = 0;
        while (!spawner.isRunning() && (attempts < 12)) {
            Thread.sleep(10000);
            attempts += 1;
        }

        if (attempts == 12) {
            fail("SelendroidJarSpawner did not start the server process");
        }
    }

    @Test(dependsOnMethods = { "testStartServer" })
    public void testShutDown() throws Exception {
        spawner.shutdown();
        assertFalse(spawner.isRunning());
    }
}
