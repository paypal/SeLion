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

package com.paypal.selion.pojos;

/**
 * A simple POJO (Plain Old Java class) that essentially captures the image name and the PID of a process.
 * 
 */
public class ProcessInfo {
    public String getProcessName() {
        return processName.toLowerCase();
    }

    public String getProcessId() {
        return processId;
    }

    @Override
    public String toString() {
        return "ProcessInfo [processName=" + processName + ", processId=" + processId + "]";
    }

    private final String processName;
    private final String processId;

    public ProcessInfo(String processName, String processId) {
        this.processId = processId.replaceAll("\"", "");
        this.processName = processName.replaceAll("\"", "").toLowerCase();
    }

}
