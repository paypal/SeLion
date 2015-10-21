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

package com.paypal.selion.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.io.IOUtils;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.logging.SeLionGridLogger;

/**
 * A Helper class that helps do an authentication check for the given user name password. NOTE: This class DOES NOT
 * validate the credentials against LDAP.
 * 
 * The following are some of the assumptions that this helper makes.
 * 
 * <ul>
 * <li>User name will always be <b>admin</admin> and CANNOT be changed.
 * <li>Multiple user names are not supported.
 * </ul>
 * 
 */
public final class AuthenticationHelper {
    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(AuthenticationHelper.class);

    private static final String HASH_ALGORITHM = "SHA-256";

    final static String AUTH_FILE_LOCATION = SeLionConstants.SELION_HOME_DIR + ".authFile";

    final static String DEFAULT_USERNAME = "admin";

    final static String DEFAULT_PASSWORD = "admin";

    private AuthenticationHelper() {
        // defeat instantiation
    }

    /**
     * Tries authenticating a given credentials and returns <code>true</code> if the credentials were valid.
     * 
     * @param userName
     * @param userPassword
     * @return - <code>true</code> if the credentials were valid.
     */
    public static boolean authenticate(String userName, String userPassword) {
        LOGGER.entering(userName, StringUtils.isBlank(userPassword) ? userPassword : userPassword.replaceAll(".", "*"));
        boolean validLogin = false;
        byte[] currentAuthData;
        byte[] hashedInputData;
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(userPassword)) {
            return validLogin;
        }
        try {
            File authFile = new File(AUTH_FILE_LOCATION);
            if (!authFile.exists()) {
                authFile.createNewFile();
                createAuthFile(authFile, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            }
            currentAuthData = getBytesFromFile(authFile);
            hashedInputData = hashData(userName, userPassword);
            validLogin = Arrays.equals(currentAuthData, hashedInputData);
        } catch (Exception e) {
            validLogin = false;
        }
        LOGGER.exiting(validLogin);
        return validLogin;
    }

    /**
     * Changes the password for the given user to the new password
     * 
     * @param userName
     * @param newPassword
     * @return <code>true</code> if the password was successfully changed.
     */
    public static boolean changePassword(String userName, String newPassword) {
        LOGGER.entering(userName, newPassword.replaceAll(".", "*"));
        boolean changeSucceeded = false;
        File authFile = new File(AUTH_FILE_LOCATION);
        try {
            authFile.delete();
            authFile.createNewFile();
            createAuthFile(authFile, userName, newPassword);
            changeSucceeded = true;
        } catch (Exception e) {
            changeSucceeded = false;
        }
        LOGGER.exiting(changeSucceeded);
        return changeSucceeded;
    }

    private static void createAuthFile(File authFile, String userName, String password)
            throws GeneralSecurityException, IOException {
        byte[] encryptedBytes = hashData(userName, password);
        writeBytesToFile(authFile, encryptedBytes);
    }

    public static byte[] hashData(String userName, String password) throws NoSuchAlgorithmException {
        LOGGER.entering(userName, password.replaceAll(".", "*"));
        String data = userName + ":" + password;
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        md.update(data.getBytes());
        byte[] hashedData = md.digest();
        LOGGER.exiting(hashedData);
        return hashedData;
    }

    private static void writeBytesToFile(File file, byte[] bytes) throws IOException {
        FileOutputStream fos = null;
        DataOutputStream dos = null;

        try {
            fos = new FileOutputStream(file);
            dos = new DataOutputStream(fos);
            dos.write(bytes);
        } finally {
            IOUtils.closeQuietly(dos);
            IOUtils.closeQuietly(fos);
        }
    }

    private static byte[] getBytesFromFile(File file) throws IOException {
        byte[] data;
        FileInputStream fis = null;
        DataInputStream ois = null;

        try {
            fis = new FileInputStream(file);
            ois = new DataInputStream(fis);
            int length = (int) file.length();
            data = new byte[length];
            ois.read(data);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(ois);
        }
        return data;
    }

}
