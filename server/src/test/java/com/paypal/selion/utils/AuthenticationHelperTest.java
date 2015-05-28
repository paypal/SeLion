/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.utils;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import com.paypal.selion.pojos.SeLionGridConstants;

public class AuthenticationHelperTest {

    @BeforeClass
    public void mockTestPath() {
        SeLionGridConstants.SELION_HOME_DIR = SystemUtils.USER_DIR + "/src/test/resources/";
    }

    @Test
    public void testAuthentication() {
        boolean isValidLogin = AuthenticationHelper.authenticate("admin", "admin");
        assertTrue(isValidLogin);
    }

    @Test(dependsOnMethods = "testAuthentication")
    public void testPasswordChange() {
        boolean isPasswordChanged = AuthenticationHelper.changePassword("admin", "dummy");
        assertTrue(isPasswordChanged);
    }

    @Test(dependsOnMethods = "testPasswordChange")
    public void authenticateNewPassword() {
        boolean val = AuthenticationHelper.authenticate("admin", "dummy");
        assertTrue(val);
    }

    @Test(dependsOnMethods = "authenticateNewPassword")
    public void authenticateWrongPassword() {
        boolean isValidPassword = AuthenticationHelper.authenticate("admin", "dummy123");
        assertFalse(isValidPassword);
    }

    @Test(dependsOnMethods = "authenticateWrongPassword")
    public void authenticateWrongUsername() {
        boolean isValidUser = AuthenticationHelper.authenticate("dummy", "dummy");
        assertFalse(isValidUser);
    }

    @AfterClass(alwaysRun=true)
    public void cleanUpAuthFile() {
        File authFile = new File(SeLionGridConstants.SELION_HOME_DIR + ".authFile");
        authFile.delete();
    }
}
