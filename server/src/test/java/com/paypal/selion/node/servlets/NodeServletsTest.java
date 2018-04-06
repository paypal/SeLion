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

package com.paypal.selion.node.servlets;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.security.Permission;

public class NodeServletsTest {

    @BeforeTest
    public void setup() {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @AfterTest
    public void tearDown() {
        System.setSecurityManager(null);
    }

    // Various node servlet tests will cause a System.exit() which fouls up our test execution.
    // So install a security mgr to prevent actual exit from jvm.
    protected static class ExitException extends SecurityException {
        private static final long serialVersionUID = 4720346323475334961L;

        public final int status;
        public ExitException(int status) {
            super("There is no escape!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context) {
            // allow anything.
        }
        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }
}
