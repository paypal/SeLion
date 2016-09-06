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

import com.beust.jcommander.Parameter;
import org.openqa.grid.internal.utils.configuration.GridConfiguration;

public class SeLionGridConfiguration {
    public static final String NODE_RECYCLE_THREAD_WAIT_TIMEOUT = "nodeRecycleThreadWaitTimeout";
    @Parameter(
        names = "-" + NODE_RECYCLE_THREAD_WAIT_TIMEOUT,
        description = "<Integer> in ms : How long to wait for incomplete sessions when the node is attempting to " +
                "shutdown. Default : 0, wait forever"
    )
    public Integer nodeRecycleThreadWaitTimeout = 0;

    public static final String UNIQUE_SESSION_COUNT = "uniqueSessionCount";
    @Parameter(
        names = "-" + UNIQUE_SESSION_COUNT,
        description = "<Integer> value : Number of unique sessions to allow before triggering a node shutdown. " +
                " Default : 0, infinite"
    )
    public Integer uniqueSessionCount = 0;

    // TODO "custom" GridConfiguration values are discouraged by Selenium. Find another path.
    protected void mergeCustom(GridConfiguration gc) {
        gc.custom.put(NODE_RECYCLE_THREAD_WAIT_TIMEOUT, String.valueOf(nodeRecycleThreadWaitTimeout));
        gc.custom.put(UNIQUE_SESSION_COUNT, String.valueOf(uniqueSessionCount));
    }
}
