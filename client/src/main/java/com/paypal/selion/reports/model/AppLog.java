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

package com.paypal.selion.reports.model;

import com.paypal.selion.reports.runtime.MobileReporter;

/**
 * This class represents the logs generated against a Native app on a device/simulator via {@link MobileReporter}.
 * 
 */
public class AppLog extends AbstractLog {

    @Override
    protected void parse(String part) {
        // At the moment we dont have anything extra to parse apart from what AbstractLog is already parsing.
    }

    public AppLog() {
    }

    public AppLog(String s) {
        super(s);
    }

    @Override
    public boolean hasLogs() {
        return (msg != null && !msg.trim().isEmpty());
    }
}
