/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 eBay Software Foundation                                                                   |
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.openqa.selenium.Platform;

import com.google.common.base.Preconditions;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.ArtifactDetails;
import com.paypal.selion.pojos.ArtifactDetails.URLChecksumEntity;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * File downloader is used to clean up already downloaded files and download all the files specified in the
 * download.properties
 */
final class FileDownloader {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(FileDownloader.class);
    private static List<String> files = new ArrayList<String>();
    private static long lastModifiedTime = 0;
    private static List<String> supportedTypes = Arrays.asList(ArchiveStreamFactory.ZIP, ArchiveStreamFactory.TAR,
            ArchiveStreamFactory.JAR, "bz2");

    private FileDownloader() {
        // Utility class. Hide the constructor
    }

    /**
     * This method is used to cleanup all the files already downloaded
     */
    public static void cleanup() {
        LOGGER.entering();

        for (String temp : files) {
            new File(temp).delete();
        }

        // Cleaning up the files list
        files.clear();
        LOGGER.exiting();
    }

    /**
     * This method will check whether the download.properties file got modified and download all the files in
     * download.properties
     */
    public static void checkForDownloads() {
        LOGGER.entering();

        File downloadFile = new File(SeLionGridConstants.DOWNLOAD_PROPERTIES_FILE);

        if (lastModifiedTime == downloadFile.lastModified()) {
            return;
        }
        lastModifiedTime = downloadFile.lastModified();

        cleanup();

        Properties prop = new Properties();

        try {
            FileInputStream f = new FileInputStream(SeLionGridConstants.DOWNLOAD_PROPERTIES_FILE);
            prop.load(new FileInputStream(SeLionGridConstants.DOWNLOAD_PROPERTIES_FILE));
            f.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to open download properties file", e);
            throw new RuntimeException(e);
        }

        LOGGER.info("Current Platform: " + Platform.getCurrent());
        Map<String, URLChecksumEntity> artifactDetails = ArtifactDetails.getArtifactDetailsForCurrentPlatform(prop);

        for (Entry<String, URLChecksumEntity> artifact : artifactDetails.entrySet()) {
            URLChecksumEntity entity = artifact.getValue();
            String url = entity.getUrl().getValue();
            String checksum = entity.getChecksum().getValue();
            StringBuilder msg = new StringBuilder();
            msg.append("Downloading ").append(artifact.getKey());
            msg.append(" from URL: ").append(url).append("...");
            msg.append("[").append(checksum).append("] will be used for checksum validation.");
            LOGGER.info(msg.toString());
            String result;
            while ((result = downloadFile(url, checksum)) == null) {
                // TODO: Need to add a measurable wait to skip downloading after 'n' tries
                LOGGER.warning("Error downloading the file " + url + ". Retrying....");
            }
            files.add(result);
            if (!result.endsWith(".jar")) {
                List<String> extractedFileList = FileExtractor.extractArchive(result);
                files.addAll(extractedFileList);
            }
        }
        LOGGER.info("Files after download and extract: " + files.toString());
        LOGGER.exiting();
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
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        if (checksum.equals(sb.toString())) {
            LOGGER.info("checksum matched for " + filename);
            return true;
        }
        return false;

    }

    private static String decideFilePath(String fileName) {
        if (fileName.endsWith(".jar")) {
            return new StringBuffer().append(SeLionGridConstants.SELION_HOME_DIR).append(fileName).toString();
        } else {
            // Encountered a archive type: at this point it is sure the valid archive types come in
            return new StringBuffer().append(SeLionGridConstants.DOWNLOADS_DIR).append(fileName).toString();
        }
    }

    private static String downloadFile(String url, String checksum, String algorithm) {

        String filename = decideFilePath(url.substring(url.lastIndexOf("/") + 1));
        if (new File(filename).exists()) {
            // local file exist. no need to download
            if (checkLocalFile(filename, checksum, algorithm)) {
                return filename;
            }
        }
        LOGGER.info("Downloading from " + url + " with checksum " + checksum + "[" + algorithm + "]");
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
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        if (checksum.equals(sb.toString())) {
            LOGGER.info("checksum matched for " + url);
            return filename;
        }
        LOGGER.info("checksum did not match for " + url);
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
        LOGGER.entering(new Object[] { artifactUrl, checksum });
        Preconditions.checkArgument(artifactUrl != null && !artifactUrl.isEmpty(),
                "Invalid URL: Cannot be null or empty");
        Preconditions.checkArgument(checksum != null && !checksum.isEmpty(),
                "Invalid CheckSum: Cannot be null or empty");
        // Making sure only the files supported go through the download and extraction.
        isValidFileType(artifactUrl);
        String algorithm = null;
        if (isValidSHA1(checksum)) {
            algorithm = "SHA1";
        } else if (isValidMD5(checksum)) {
            algorithm = "MD5";
        }
        String result = downloadFile(artifactUrl, checksum, algorithm);
        LOGGER.exiting(result);
        return result;
    }

    private static boolean isValidSHA1(String s) {
        return s.matches("[a-fA-F0-9]{40}");
    }

    private static boolean isValidMD5(String s) {
        return s.matches("[a-fA-F0-9]{32}");
    }

    private static void isValidFileType(String url) {
        // Obtaining only the file extension
        String fileType = url.substring(url.lastIndexOf('.') + 1);
        if (!supportedTypes.contains(fileType)) {
            throw new UnsupportedOperationException("Unsupported file format: " + fileType
                    + ". Supported file types are .zip,.tar and bz2");
        }
    }

}
