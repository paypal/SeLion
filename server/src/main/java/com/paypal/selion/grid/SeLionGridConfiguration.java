/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016-2017 PayPal                                                                                     |
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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import com.paypal.selion.proxy.SeLionRemoteProxy;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;

/**
 * SeLion grid configuration options which are common to both hubs and nodes
 */
public class SeLionGridConfiguration {
    public static final String NODE_RECYCLE_THREAD_WAIT_TIMEOUT = "nodeRecycleThreadWaitTimeout";
    public static final String NODE_RECYCLE_THREAD_WAIT_TIMEOUT_ARG = "-" + NODE_RECYCLE_THREAD_WAIT_TIMEOUT;

    public static final String UNIQUE_SESSION_COUNT = "uniqueSessionCount";
    public static final String UNIQUE_SESSION_COUNT_ARG = "-" + UNIQUE_SESSION_COUNT;

    private static final int DEFAULT_INT_IFINITY = 0;

    /**
     * How long to wait for incomplete session when a {@link SeLionRemoteProxy} is attempting to shutdown.
     */
    @Parameter(
        names = NODE_RECYCLE_THREAD_WAIT_TIMEOUT_ARG,
        description = "<Integer> in ms : How long to wait for incomplete sessions when the node is attempting to " +
                "shutdown. Default is wait forever."
    )
    Integer nodeRecycleThreadWaitTimeout;

    /**
     * Number of unique session to allow before a {@link SeLionRemoteProxy} triggers a node shutdown
     */
    @Parameter(
        names = UNIQUE_SESSION_COUNT_ARG,
        description = "<Integer> value : Number of unique sessions to allow before triggering a node shutdown. " +
                " Default is unlimited."
    )
    Integer uniqueSessionCount;

    /**
     * The SeLion {@link ProcessLauncherConfiguration}
     */
    @ParametersDelegate
    ProcessLauncherConfiguration processLauncherConfiguration = new ProcessLauncherConfiguration();

    public Integer getNodeRecycleThreadWaitTimeout() {
        return nodeRecycleThreadWaitTimeout != null ? nodeRecycleThreadWaitTimeout : DEFAULT_INT_IFINITY;
    }

    public void setNodeRecycleThreadWaitTimeout(Integer nodeRecycleThreadWaitTimeout) {
        this.nodeRecycleThreadWaitTimeout = nodeRecycleThreadWaitTimeout;
    }

    public Integer getUniqueSessionCount() {
        return uniqueSessionCount != null ? uniqueSessionCount : DEFAULT_INT_IFINITY;
    }

    public void setUniqueSessionCount(Integer uniqueSessionCount) {
        this.uniqueSessionCount = uniqueSessionCount;
    }

    public ProcessLauncherConfiguration getProcessLauncherConfiguration() {
        return processLauncherConfiguration != null ? processLauncherConfiguration : new ProcessLauncherConfiguration();
    }

    public void setProcessLauncherConfiguration(ProcessLauncherConfiguration processLauncherConfiguration) {
        this.processLauncherConfiguration = processLauncherConfiguration;
    }

    // TODO "custom" GridConfiguration values are discouraged by Selenium. Find another path.
    /**
     * merges the "custom:" { ... } values for this configuration
     */
    protected void mergeCustom(GridHubConfiguration gc) {
        if (nodeRecycleThreadWaitTimeout != null) {
            gc.custom.put(NODE_RECYCLE_THREAD_WAIT_TIMEOUT, String.valueOf(nodeRecycleThreadWaitTimeout));
        }
        if (uniqueSessionCount != null) {
            gc.custom.put(UNIQUE_SESSION_COUNT, String.valueOf(uniqueSessionCount));
        }
    }

    // TODO "custom" GridConfiguration values are discouraged by Selenium. Find another path.
    /**
     * merges the "custom:" { ... } values for this configuration
     */
    protected void mergeCustom(GridNodeConfiguration gc) {
        if (nodeRecycleThreadWaitTimeout != null) {
            gc.custom.put(NODE_RECYCLE_THREAD_WAIT_TIMEOUT, String.valueOf(nodeRecycleThreadWaitTimeout));
        }
        if (uniqueSessionCount != null) {
            gc.custom.put(UNIQUE_SESSION_COUNT, String.valueOf(uniqueSessionCount));
        }
    }
}
