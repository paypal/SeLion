/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-2015 PayPal                                                                                     |
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
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Platform;

import com.google.common.base.Preconditions;
import com.paypal.selion.SeLionConstants;
import com.paypal.selion.grid.ArtifactDetails.URLChecksumEntity;
import com.paypal.selion.grid.RunnableLauncher.InstanceType;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.SeLionGridConstants;

/**
 * File downloader is used to clean up files already downloaded and download all the files specified in the
 * download.json file
 */
final class FileDownloader {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(FileDownloader.class);
    private static List<String> files = new ArrayList<String>();
    private static long lastModifiedTime;
    private static final List<String> SUPPORTED_TYPES = Arrays.asList(ArchiveStreamFactory.ZIP, ArchiveStreamFactory.TAR,
            ArchiveStreamFactory.JAR, "bz2");
    private static final File DOWNLOAD_FILE = new File(SeLionGridConstants.DOWNLOAD_JSON_FILE);

    private FileDownloader() {
        // Utility class. Hide the constructor
    }

    /**
     * Cleanup all the files already downloaded within the same JVM process. Automatically called internally.
     */
    static void cleanup() {
        LOGGER.entering();

        for (String temp : files) {
            new File(temp).delete();
        }

        // Cleaning up the files list
        files.clear();
        LOGGER.exiting();
    }

    /**
     * Check download.json and download files based on artifact names. Returns without downloading if it detects a
     * last modified time stamp is unchanged from the last check. Cleans up previous downloads from the same JVM
     * 
     * @param artifactNames
     *            the artifact names to download
     */
    static void checkForDownloads(List<String> artifactNames) {
        checkForDownloads(artifactNames, true);
    }

    /**
     * Check download.json and download files based on artifact names. Cleans up previous downloads from the same
     * JVM
     * 
     * @param artifactNames
     *            the artifact names to download
     * @param checkTimeStamp
     *            whether to check the last modified time stamp of the downlaod.json file. Returns immediately on
     *            subsequent calls if <code>true</code> and last modified is unchanged.
     */
    static void checkForDownloads(List<String> artifactNames, boolean checkTimeStamp) {
        checkForDownloads(artifactNames, true, true);
    }

    /**
     * Check download.json and download files based on artifact names
     * 
     * @param artifactNames
     *            the artifact names to download
     * @param checkTimeStamp
     *            whether to check the last modified time stamp of the downlaod.json file. Returns immediately on
     *            subsequent calls if <code>true</code> and last modified is unchanged.
     * @param cleanup
     *            whether to cleanup previous downloads from a previous call to
     *            checkForDownloads in the same JVM
     */
    static void checkForDownloads(List<String> artifactNames, boolean checkTimeStamp, boolean cleanup) {
        LOGGER.entering();

        if (checkTimeStamp && (lastModifiedTime == DOWNLOAD_FILE.lastModified())) {
            return;
        }
        lastModifiedTime = DOWNLOAD_FILE.lastModified();

        if (cleanup) {
            cleanup();
        }

        List<URLChecksumEntity> artifactDetails = new ArrayList<ArtifactDetails.URLChecksumEntity>();

        try {
            artifactDetails = ArtifactDetails.getArtifactDetailsForCurrentPlatformByNames(DOWNLOAD_FILE, artifactNames);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to open download.json file", e);
            throw new RuntimeException(e);
        }

        downloadAndExtractArtifacts(artifactDetails);
        LOGGER.exiting();
    }

    /**
     * Check download.json and download files based on {@link InstanceType}. Returns without downloading if it detects a
     * last modified time stamp is unchanged from the last check. Cleans up previous downloads from the same JVM
     * 
     * @param instanceType
     *            the {@link InstanceType} to process downloads for
     */
    static void checkForDownloads(InstanceType instanceType) {
        checkForDownloads(instanceType, true);
    }

    /**
     * Check download.json and download files based on {@link InstanceType}. Cleans up previous downloads from the same
     * JVM
     * 
     * @param instanceType
     *            the {@link InstanceType} to process downloads for
     * @param checkTimeStamp
     *            whether to check the last modified time stamp of the downlaod.json file. Returns immediately on
     *            subsequent calls if <code>true</code> and last modified is unchanged.
     */
    static void checkForDownloads(InstanceType instanceType, boolean checkTimeStamp) {
        checkForDownloads(instanceType, checkTimeStamp, true);
    }

    /**
     * Check download.json and download files based on {@link InstanceType}
     * 
     * @param instanceType
     *            the {@link InstanceType} to process downlaods for
     * @param checkTimeStamp
     *            whether to check the last modified time stamp of the downlaod.json file. Returns immediately on
     *            subsequent calls if <code>true</code> and last modified is unchanged.
     * @param cleanup
     *            whether to cleanup previous downloads from a previous call to
     *            checkForDownloads in the same JVM
     */
    static void checkForDownloads(InstanceType instanceType, boolean checkTimeStamp, boolean cleanup) {
        LOGGER.entering();

        if (checkTimeStamp && (lastModifiedTime == DOWNLOAD_FILE.lastModified())) {
            return;
        }
        lastModifiedTime = DOWNLOAD_FILE.lastModified();

        if (cleanup) {
            cleanup();
        }

        List<URLChecksumEntity> artifactDetails = new ArrayList<ArtifactDetails.URLChecksumEntity>();

        try {
            artifactDetails = ArtifactDetails.getArtifactDetailsForCurrentPlatformByRole(DOWNLOAD_FILE, instanceType);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to open download.json file", e);
            throw new RuntimeException(e);
        }

        downloadAndExtractArtifacts(artifactDetails);
        LOGGER.exiting();
    }

    private static void downloadAndExtractArtifacts(List<URLChecksumEntity> artifactDetails) {
        LOGGER.fine("Current Platform: " + Platform.getCurrent());

        for (Iterator<URLChecksumEntity> iterator = artifactDetails.iterator(); iterator.hasNext();) {
            URLChecksumEntity entity = (URLChecksumEntity) iterator.next();
            String url = entity.getUrl().getValue();
            String checksum = entity.getChecksum().getValue();
            StringBuilder msg = new StringBuilder();
            msg.append("Downloading from URL: ").append(url).append("...");
            msg.append("[").append(checksum).append("] will be used for checksum validation.");
            LOGGER.fine(msg.toString());
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
        LOGGER.fine("Files after download and extract: " + files.toString());
    }

    private static boolean checkLocalFile(String filename, String checksum, String algorithm) {
        MessageDigest md = null;
        StringBuilder sb = new StringBuilder("");
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e1) {
            // NOSONAR
        }

        try {
            byte[] mdbytes = md.digest(FileUtils.readFileToByteArray(new File(filename)));
            sb.append(Hex.encodeHexString(mdbytes));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        if (checksum.equals(sb.toString())) {
            LOGGER.fine("checksum matched for " + filename);
            return true;
        }
        LOGGER.fine("checksum did not match for " + filename);
        return false;

    }

    private static String decideFilePath(String fileName) {
        if (fileName.endsWith(".jar")) {
            return new StringBuilder().append(SeLionConstants.SELION_HOME_DIR).append(fileName).toString();
        } else {
            // Encountered a archive type: at this point it is sure the valid archive types come in
            return new StringBuilder().append(SeLionGridConstants.DOWNLOADS_DIR).append(fileName).toString();
        }
    }

    private static String downloadFile(String url, String checksum, String algorithm) {
        Preconditions.checkArgument(StringUtils.isNotBlank(algorithm), "Invalid Algorithm: Cannot be null or empty");
        String filename = decideFilePath(url.substring(url.lastIndexOf("/") + 1));
        if (new File(filename).exists()) {
            // local file exist. no need to download
            if (checkLocalFile(filename, checksum, algorithm)) {
                return filename;
            }
        }
        LOGGER.info("Downloading from " + url + " with checksum " + checksum + "[" + algorithm + "]");

        try {
            FileUtils.copyURLToFile(new URL(url), new File(filename), 10000, 60000);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        if (checkLocalFile(filename, checksum, algorithm)) {
            return filename;
        }
        return null;
    }

    /**
     * Download a file from the specified url
     *
     * @param artifactUrl
     *            url of the file to be downloaded.
     * @param checksum
     *            checksum to downloaded file.
     * @return the downloaded file path.
     */
    static String downloadFile(String artifactUrl, String checksum) {
        LOGGER.entering(new Object[] { artifactUrl, checksum });
        Preconditions.checkArgument(StringUtils.isNotBlank(artifactUrl), "Invalid URL: Cannot be null or empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(checksum), "Invalid CheckSum: Cannot be null or empty");
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
        if (!SUPPORTED_TYPES.contains(fileType)) {
            throw new UnsupportedOperationException("Unsupported file format: " + fileType
                    + ". Supported file types are .zip,.tar and bz2");
        }
    }

}
