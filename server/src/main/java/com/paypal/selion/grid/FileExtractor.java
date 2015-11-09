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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.Platform;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.pojos.ProcessNames;

final class FileExtractor {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(FileExtractor.class.getName());

    FileExtractor() {
        // Utility class. So hiding the constructor
    }

    static String getFileNameFromPath(String name) {
        String[] path = name.split("/");
        String s = path[path.length - 1];
        return s;
    }

    /**
     * Utility method to return the executable names for the specified platform.
     *
     * @return {@link List} of {@link String} containing the executable file names.
     */
    
    static List<String> getExecutableNames() {
        List<String> executableNames = new ArrayList<String>();
        switch (Platform.getCurrent()) {
        case MAC:
        case UNIX:
        case LINUX: {
            Collections.addAll(executableNames, ProcessNames.PHANTOMJS.getUnixImageName(),
                    ProcessNames.CHROMEDRIVER.getUnixImageName());
            break;
        }
        default: {
            Collections.addAll(executableNames, ProcessNames.PHANTOMJS.getWindowsImageName(),
                    ProcessNames.CHROMEDRIVER.getWindowsImageName(), ProcessNames.IEDRIVER.getWindowsImageName());
            break;
        }
        }
        return executableNames;
    }

    static List<String> extractArchive(String archiveFile) {
        LOGGER.entering(archiveFile);

        LOGGER.info("Extracting " + archiveFile);

        String archiveStreamType;
        boolean isCompressedArchive = false;
        String compressName = null;
        String outputArchiveName = null;
        List<String> files = new ArrayList<String>();

        if (archiveFile.endsWith(".bz2")) {
            isCompressedArchive = true;
            compressName = CompressorStreamFactory.BZIP2;
            outputArchiveName = archiveFile.substring(0, archiveFile.lastIndexOf('.'));
            LOGGER.fine("Output archive name: " + outputArchiveName);
        }

        // TODO: For any other compress format a simple else-if part with proper assignments will suffice
        String workingArchiveFile = archiveFile;
        if (isCompressedArchive) {
            LOGGER.fine("Found a compressed archive");
            CompressorInputStream is;
            try {
                is = new CompressorStreamFactory().createCompressorInputStream(compressName, new FileInputStream(
                        archiveFile));
                FileOutputStream decompressStream = new FileOutputStream(outputArchiveName);
                IOUtils.copy(is, decompressStream);
                is.close();
                decompressStream.close();
                // The archive is de-compressed. Replacing the compressed file name to the archive name
                workingArchiveFile = outputArchiveName;
                // Add the file name to the list
                files.add(outputArchiveName);
            } catch (CompressorException | IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        archiveStreamType = ArchiveStreamFactory.ZIP;
        if (workingArchiveFile.endsWith(".tar")) {
            archiveStreamType = ArchiveStreamFactory.TAR;
        }
        // TODO: For any new archive formats a simple else-if will suffice

        OutputStream outputFileStream = null;
        LOGGER.fine("Getting file list for archive " + workingArchiveFile);

        List<String> executableNameList = FileExtractor.getExecutableNames();
        LOGGER.fine("Executable list: " + executableNameList.toString());

        ArchiveInputStream archiveStream = null;

        try {
            archiveStream = new ArchiveStreamFactory().createArchiveInputStream(archiveStreamType,
                    new FileInputStream(workingArchiveFile));
            ArchiveEntry entry;
            while ((entry = archiveStream.getNextEntry()) != null) {
                String fileNameInEntry = getFileNameFromPath(entry.getName());
                if (!entry.isDirectory() && executableNameList.contains(fileNameInEntry.toLowerCase())) {
                    String filename = SeLionConstants.SELION_HOME_DIR + getFileNameFromPath(entry.getName());
                    File outputFile = new File(filename);
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    outputFile.createNewFile();
                    LOGGER.fine(String.format("Creating output file %s.", outputFile.getAbsolutePath()));
                    outputFileStream = new FileOutputStream(outputFile);
                    IOUtils.copy(archiveStream, outputFileStream);
                    LOGGER.fine("Is binary executable by application: " + outputFile.canExecute());
                    if (!outputFile.canExecute()) {
                        LOGGER.fine("Setting the file to be executable");
                        outputFile.setExecutable(true);
                    }
                    // Adding the binary name or .exe to the list
                    files.add(filename);
                    break; // No point in proceeding further with the loop
                }
            }
        } catch (Exception e) { // NOSONAR
            LOGGER.log(Level.SEVERE, "Unable to extract archive", e);
        } finally {
            try {
                if (archiveStream != null) {
                    archiveStream.close();
                }
                if (outputFileStream != null) {
                    outputFileStream.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error closing streams while extracting archive", e);
            }
        }

        LOGGER.exiting(files.toString());
        return files;
    }
}
