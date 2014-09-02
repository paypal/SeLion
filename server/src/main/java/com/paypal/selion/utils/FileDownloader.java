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

package com.paypal.selion.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.base.Preconditions;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.ArtifactDetails;
import com.paypal.selion.pojos.ArtifactDetails.URLChecksumEntity;

/**
 * File downloader is used to Cleanup already downloaded files and download all the files specified in the
 * download.properties
 * 
 */
public final class FileDownloader {

    private static final Logger logger = SeLionGridLogger.getLogger();
    private static List<String> files = new ArrayList<String>();
    private static long lastModifiedTime = 0;

    private FileDownloader() {
        // Utility class. Hide the constructor
    }

    /**
     * This method is used to cleanup all the files already downloaded
     */
    public static void cleanup() {
        for (String temp : files) {
            new File(temp).delete();
        }

        // Cleaning up the files list
        files.clear();
    }

    /**
     * This method will check whether the download.properties file got modified and download all the files in
     * download.properties
     */
    public static void checkForDownloads() {
        if (lastModifiedTime == new File("download.properties").lastModified()) {
            return;
        }
        lastModifiedTime = new File("download.properties").lastModified();

        cleanup();

        Properties prop = new Properties();

        try {
            FileInputStream f = new FileInputStream("download.properties");
            prop.load(new FileInputStream("download.properties"));
            f.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        Map<String, URLChecksumEntity> artifactDetails = ArtifactDetails.getArtifactDetailsAsMap(prop);
        for (Entry<String, URLChecksumEntity> artifact : artifactDetails.entrySet()) {
            URLChecksumEntity entity = artifact.getValue();
            String url = entity.getUrl().getValue();
            String checksum = entity.getChecksum().getValue();
            StringBuilder msg = new StringBuilder();
            String key = capitalize(artifact.getKey());
            msg.append("Initiating download for '").append(key).append("' ");
            msg.append("artifacts from URL :").append(url).append("...");
            msg.append("[").append(checksum).append("] will be used for checksum validation.");
            logger.info(msg.toString());
            String result = null;
            while ((result = downloadFile(url, checksum)) == null) {
                // TODO: Need to add a measurable wait to skip downloading after 'n' tries
                logger.warning("Error downloading the file : " + url + ". Retrying....");
            }
            files.add(result);
            if (!result.endsWith("jar")) {
                // TODO: Again this is specific to windows , need to tweak for linux environment.
                files.addAll(unZip(result));
            }
        }
    }

    private static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1))
                .toString();
    }

    private static boolean checkLocalFile(String filename, String checksum, String algorithm) {
        InputStream is = null;
        MessageDigest md = null;
        StringBuffer sb = new StringBuffer("");
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e1) {
            // NOSONAR
        }

        try {
            int bytesRead;

            is = new FileInputStream(filename);

            byte[] buf = new byte[1024];
            while ((bytesRead = is.read(buf)) != -1) {
                md.update(buf, 0, bytesRead);
            }

            byte[] mdbytes = md.digest();

            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        if (checksum.equals(sb.toString())) {
            logger.info("checksum matched for " + filename);
            return true;
        }
        return false;

    }

    private static String downloadFile(String url, String checksum, String algorithm) {

        String filename = url.substring(url.lastIndexOf("/") + 1);

        if (new File(filename).exists()) {
            // local file exist. no need to download
            if (checkLocalFile(filename, checksum, algorithm)) {
                return filename;
            }
        }
        logger.info("Downloading from " + url + " with checksum value " + checksum + "[" + algorithm + "]");
        OutputStream outStream = null;
        URLConnection uCon = null;

        InputStream is = null;
        MessageDigest md = null;
        StringBuffer sb = new StringBuffer("");
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e1) {
            // NOSONAR
        }

        try {
            int bytesRead;
            URL Url = new URL(url);
            outStream = new FileOutputStream(filename);

            uCon = Url.openConnection();
            is = uCon.getInputStream();

            byte[] buf = new byte[1024];
            while ((bytesRead = is.read(buf)) != -1) {
                md.update(buf, 0, bytesRead);
                outStream.write(buf, 0, bytesRead);
            }
            outStream.close();

            byte[] mdbytes = md.digest();

            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        if (checksum.equals(sb.toString())) {
            logger.info("checksum matched for " + url);
            return filename;
        }
        logger.info("checksum not matched for the file : " + url);
        return null;
    }

    /**
     * this method is used to download a file from the specified url
     * 
     * @param artifactUrl
     *            - url of the file to be downloaded.
     * @param checksum
     *            - checksum to downloaded file.
     * @return the downloaded file path.
     */
    public static String downloadFile(String artifactUrl, String checksum) {
        Preconditions.checkArgument(artifactUrl != null && !artifactUrl.isEmpty(),
                "Invalid URL: Cannot be null or empty");
        Preconditions.checkArgument(checksum != null && !checksum.isEmpty(),
                "Invalid CheckSum: Cannot be null or empty");
        String algorithm = null;
        if (isValidSHA1(checksum)) {
            algorithm = "SHA1";
        } else if (isValidMD5(checksum)) {
            algorithm = "MD5";
        }
        return downloadFile(artifactUrl, checksum, algorithm);
    }

    private static boolean isValidSHA1(String s) {
        return s.matches("[a-fA-F0-9]{40}");
    }

    private static boolean isValidMD5(String s) {
        return s.matches("[a-fA-F0-9]{32}");
    }

    /**
     * This method is used to unzip the zip file.<br>
     * <b>Note:</b> In WINDOWS environment , If the Zip file has more files along with .exe then only the .exe file will
     * be extracted.
     * 
     * @param fileName
     *            name of the zip file
     * @return list of files that are unzipped.
     */
    public static List<String> unZip(String fileName) {
        List<String> files = new ArrayList<String>();

        ZipInputStream zin = null;
        try {

            byte[] data = new byte[1000];
            int byteRead;
            String entryName;

            BufferedOutputStream bout = null;
            zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(fileName)));
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                byteRead = 0;
                data = new byte[1000];
                entryName = entry.getName();
                // TODO: This is a solution specific to Windows environment. Need to explore options for Linux/Mac
                // environments.
                if (!entry.isDirectory() && entry.getName().endsWith("exe")) {
                    // Create the parent folder if the entry has a parent to
                    // tackle the FileNotFoundException
                    File parentFile = new File(entry.getName()).getParentFile();
                    if (parentFile != null) {
                        // The parent folder must be created in order to extract the
                        // file to that folder
                        parentFile.mkdir();
                        entryName = entryName.substring(entryName.lastIndexOf('/') + 1);
                    }
                    bout = new BufferedOutputStream(new FileOutputStream(entry.getName()), 1000);
                    while ((byteRead = zin.read(data, 0, 1000)) != -1) {
                        bout.write(data, 0, byteRead);
                    }
                    files.add(entryName);
                    bout.flush();
                    bout.close();

                    if (parentFile != null) {
                        // For any files that are extracted inside a folder the
                        // .exe will be moved to Grid2 folder and the parent
                        // folder will be deleted
                        Files.move(FileSystems.getDefault().getPath(entry.getName()),
                                FileSystems.getDefault().getPath(entryName), StandardCopyOption.REPLACE_EXISTING);
                        parentFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                zin.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return files;
    }
}
