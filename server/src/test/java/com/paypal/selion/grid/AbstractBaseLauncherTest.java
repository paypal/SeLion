/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

import static com.paypal.selion.pojos.SeLionGridConstants.*;
import static com.paypal.selion.grid.RunnableLauncher.InstanceType.*;
import static com.paypal.selion.grid.ProcessLauncherConfiguration.SELION_CONFIG_ARG;
import static com.paypal.selion.grid.AbstractBaseLauncher.ROLE_ARG;
import static com.paypal.selion.grid.AbstractBaseLauncher.HUB_CONFIG_ARG;
import static com.paypal.selion.grid.AbstractBaseLauncher.NODE_CONFIG_ARG;

public class AbstractBaseLauncherTest {
    private class DummyProcessLauncher extends AbstractBaseLauncher {
        public DummyProcessLauncher() {
            this(new String[] {});
        }

        public DummyProcessLauncher(String[] args) {
            List<String> commands = new LinkedList<>(Arrays.asList(args));
            setCommands(commands);
        }

        @Override
        public void shutdown() {
            // do nothing
        }

        @Override
        public void run() {
            // do nothing
        }
    }

    @BeforeClass
    public void beforeClass() {
        assertTrue(new File(NODE_CONFIG_FILE).isFile(),
                "the default nodeConfig.json file must be present before proceeding");
        assertTrue(new File(HUB_CONFIG_FILE).isFile(),
                "the default hubConfig.json file must be present before proceeding");
    }

    @Test
    public void testGetHost() {
        DummyProcessLauncher launcher;

        // default and "standalone" case.
        launcher = new DummyProcessLauncher();
        assertEquals(launcher.getHost(), "localhost");

        // attempt to read from SeLion's default hubConfig.json
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "hub" });
        assertEquals(launcher.getHost(), "localhost");

        // attempt to read from provided hubConfig.json
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "hub", HUB_CONFIG_ARG, HUB_CONFIG_FILE });
        assertEquals(launcher.getHost(), "localhost");

        // attempt to read from SeLion's default nodeConfig.json
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "node" });
        assertEquals(launcher.getHost(), "localhost");

        // attempt to read from provided nodeConfig.json
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "node", NODE_CONFIG_ARG, NODE_CONFIG_FILE });
        assertEquals(launcher.getHost(), "localhost");

        // host provided case
        launcher = new DummyProcessLauncher(new String[] { "-host", "dummyhost" });
        assertEquals(launcher.getHost(), "dummyhost");
    }

    @Test
    public void testGetPort() {
        DummyProcessLauncher launcher;

        // default and "standalone" case.
        launcher = new DummyProcessLauncher();
        assertEquals(launcher.getPort(), 4444);

        // attempt to read from SeLion's default hubConfig.json
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "hub" });
        assertEquals(launcher.getPort(), 4444);

        // attempt to read from provided hubConfig.json
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "hub", HUB_CONFIG_ARG, HUB_CONFIG_FILE });
        assertEquals(launcher.getPort(), 4444);

        // attempt to read from SeLion's default nodeConfig.json
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "node" });
        assertEquals(launcher.getPort(), 5555);

        // attempt to read from provided nodeConfig.json
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "node", NODE_CONFIG_ARG, NODE_CONFIG_FILE });
        assertEquals(launcher.getPort(), 5555);

        // port provided case
        launcher = new DummyProcessLauncher(new String[] { "-port", "1234" });
        assertEquals(launcher.getPort(), 1234);
    }

    @Test
    public void testGetProgramArguments() throws Exception {
        DummyProcessLauncher launcher;

        // default and "standalone" case.
        launcher = new DummyProcessLauncher();
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(SELION_CONFIG_ARG));
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(SELION_CONFIG_FILE));

        // "node" case
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "node" });
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(SELION_CONFIG_ARG));
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(SELION_CONFIG_FILE));
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(NODE_CONFIG_ARG));
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(NODE_CONFIG_FILE));

        // "hub" case
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "hub" });
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(SELION_CONFIG_ARG));
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(SELION_CONFIG_FILE));
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(HUB_CONFIG_ARG));
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(HUB_CONFIG_FILE));

        // -selionConfig already provided case
        launcher = new DummyProcessLauncher(new String[] { SELION_CONFIG_ARG, "mySeLionConfig.json" });
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains(SELION_CONFIG_ARG));
        assertTrue(Arrays.asList(launcher.getProgramArguments()).contains("mySeLionConfig.json"));
    }

    @Test
    public void testGetCommands() {
        DummyProcessLauncher launcher;

        launcher = new DummyProcessLauncher();
        assertTrue(launcher.getCommands().isEmpty());

        launcher = new DummyProcessLauncher(new String[] { "-foo", "bar", "-bar", "baz" });
        assertEquals(launcher.getCommands().size(), 4);
        assertTrue(launcher.getCommands().contains("-foo"));
    }

    @Test
    public void testGetType() {
        DummyProcessLauncher launcher;

        // default and "standalone" case.
        launcher = new DummyProcessLauncher();
        assertEquals(launcher.getType(), SELENIUM_STANDALONE);

        // "node" case
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "node" });
        assertEquals(launcher.getType(), SELENIUM_NODE);

        // "hub" case
        launcher = new DummyProcessLauncher(new String[] { ROLE_ARG, "hub" });
        assertEquals(launcher.getType(), SELENIUM_HUB);
    }

    @Test
    public void testGetLauncherOptions() {
        LauncherOptions options = new LauncherConfiguration();
        options.setFileDownloadCheckTimeStampOnInvocation(true);
        DummyProcessLauncher launcher = new DummyProcessLauncher();
        launcher.setLauncherOptions(options);

        assertSame(launcher.getLauncherOptions(), options);
    }
}
