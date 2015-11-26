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

package com.paypal.selion;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class TestServerListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        try {
            TestServerUtils.startServer();
        } catch (Exception e) { // NOSONAR
            System.err.println(" Unable to start test server");
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        try {
            TestServerUtils.stopServer();
        } catch (Exception e) { // NOSONAR
            System.err.println(" Unable to stop test server");
        }

    }

}
