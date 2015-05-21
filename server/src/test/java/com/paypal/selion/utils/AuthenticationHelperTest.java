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

import static org.powermock.api.mockito.PowerMockito.*;
import static org.testng.Assert.*;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.pojos.SeLionGridConstants;

@PrepareForTest(SeLionGridConstants.class)
public class AuthenticationHelperTest extends PowerMockTestCase {

    @BeforeClass
    public void mockTestPath() throws Exception {
        mockStatic(SeLionGridConstants.class);
        Whitebox.setInternalState(SeLionGridConstants.class, "SELION_HOME_DIR", SystemUtils.USER_DIR + "/target/temp/");
        new File(SeLionConstants.SELION_HOME_DIR).mkdirs();
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

    @AfterClass(alwaysRun = true)
    public void cleanUpAuthFile() {
        File authFile = new File(SeLionConstants.SELION_HOME_DIR + ".authFile");
        authFile.delete();
    }
}
