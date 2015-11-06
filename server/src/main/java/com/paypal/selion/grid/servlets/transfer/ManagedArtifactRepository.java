/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion.grid.servlets.transfer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;

import com.paypal.selion.SeLionConstants;
import com.paypal.selion.logging.SeLionGridLogger;
import com.paypal.selion.utils.ConfigParser;

/**
 * <code>ManagedArtifactRepository</code> is an implementation of {@link ServerRepository} for {@link ManagedArtifact}. 
 * The class is essentially a Singleton pattern. The class implements a {@link Timer} task that
 * will run a cleaner thread that runs every hour to clean the artifacts inside the repository folder. All artifacts
 * that have {@link ManagedArtifact#isExpired()} returning true are considered for removal during the cleaning cycle.
 */
public class ManagedArtifactRepository implements ServerRepository {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(ManagedArtifactRepository.class);

    /*
     * The folder used for storing artifacts.
     */
    private static final String REPO_FOLDER_NAME = ManagedArtifactRepository.initializeBaseDir();

    /*
     * Configuration property name for custom managed artifacts.
     */
    private static final String ARTIFACT_CONFIG_PROPERTY = "managedArtifact";

    /*
     * Configuration property name for base directory under {@link SeLionGridConstants#SELION_HOME_DIR} that contains
     * directories and managed artifacts.
     * 
     * Supports a name or a forward slash separated path. E.g. "repository", "repository/subrepository".
     */
    private static final String ARTIFACT_BASE_CONFIG_PROPERTY = "managedArtifactBaseDir";

    private static ManagedArtifactRepository instance = new ManagedArtifactRepository();

    private final File repoFolder;

    /*
     * Lock used for synchronizing reading and deletion cycles
     */
    private final Lock repositorySynchronizationLock;

    public static synchronized ManagedArtifactRepository getInstance() {
        return instance;
    }

    /*
     * Initializes the value of the {@link ManagedArtifactRepository#REPO_FOLDER_NAME} from JVM arguments or
     * SeLionConfig.json. Default value returned is "repository".
     */
    private static String initializeBaseDir() {
        LOGGER.entering();
        String repoFolderName = System.getProperty(ARTIFACT_BASE_CONFIG_PROPERTY,
                ConfigParser.parse().getString(ARTIFACT_BASE_CONFIG_PROPERTY, "repository"));
        repoFolderName = StringUtils.defaultIfEmpty(repoFolderName, "repository");
        LOGGER.exiting(repoFolderName);
        return repoFolderName;
    }

    private ManagedArtifactRepository() {
        repoFolder = new File(SeLionConstants.SELION_HOME_DIR + File.separator + REPO_FOLDER_NAME);
        repositorySynchronizationLock = new ReentrantLock();
        // Cleaner thread timer
        Timer timer = new Timer();

        // Schedule the cleaner one hour from now and every hour thereafter
        // Can be made configurable - weighing the advantage of making it so over the complexity
        // of adding a new parameter into the config files and reading it in here?
        timer.scheduleAtFixedRate(new RepositoryCleaner(), 60 * 60 * 1000, 60 * 60 * 1000);
    }

    public ManagedArtifact saveContents(UploadedArtifact uploadedArtifact) {
        synchronized (getMutex(uploadedArtifact)) {
            LOGGER.entering(uploadedArtifact);
            File file = createFileUsing(uploadedArtifact);
            try {
                FileUtils.writeByteArrayToFile(file, uploadedArtifact.getArtifactContents());
                ManagedArtifact managedArtifact = getManagedArtifact(file.getAbsolutePath());
                LOGGER.exiting(managedArtifact);
                return managedArtifact;
            } catch (IOException e) {
                throw new ArtifactUploadException("IOException in writing file contents", e);
            }
        }
    }

    public ManagedArtifact getArtifact(String pathInfo) {
        LOGGER.entering(pathInfo);
        try {
            repositorySynchronizationLock.lock();
            ManagedArtifact managedArtifact = getMatchedArtifact(pathInfo);
            if (managedArtifact.isExpired()) {
                throw new ArtifactDownloadException("The requested artifact: " + managedArtifact.getArtifactName()
                        + " has expired");
            }
            LOGGER.exiting(managedArtifact);
            return managedArtifact;
        } finally {
            repositorySynchronizationLock.unlock();
        }
    }

    private String getMutex(UploadedArtifact uploadedArtifact) {
        StringBuilder buffer = new StringBuilder();
        for (String s : uploadedArtifact.getMetaInfo().keySet()) {
            buffer.append(s);
        }
        return buffer.toString().intern();
    }

    private ManagedArtifact getMatchedArtifact(String pathInfo) {
        List<File> files = (List<File>) FileUtils.listFiles(repoFolder, TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE);
        for (File file : files) {
            ManagedArtifact managedArtifact = getManagedArtifact(file.getAbsolutePath());
            if (managedArtifact.matchesPathInfo(pathInfo)) {
                return managedArtifact;
            }
        }
        throw new ArtifactDownloadException("No artifact found for requested path: " + pathInfo);
    }

    private File createFileUsing(UploadedArtifact uploadedArtifact) {
        try {
            ManagedArtifact instance = getConfiguredManagedArtifactClass().newInstance();
            instance.initFromUploadedArtifact(uploadedArtifact);
            String pathInfo = instance.getAbsolutePath();
            File file = new File(pathInfo);
            File dir = new File(file.getParent());
            dir.mkdirs();
            if (!(file.exists() || file.createNewFile())) {
                throw new ArtifactUploadException("Cannot create file with name: " + file.getName());
            }
            return file;
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            throw new ArtifactUploadException(e.getClass().getSimpleName() + " in creating file.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends ManagedArtifact> getConfiguredManagedArtifactClass() {
        LOGGER.entering();
        try {
            String managedArtifactClassName = ConfigParser.parse().getString(ARTIFACT_CONFIG_PROPERTY);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "ManagedArtifact class name configured in grid: " + managedArtifactClassName);
            }
            Class<? extends ManagedArtifact> managedArtifactClass = (Class<? extends ManagedArtifact>) this.getClass()
                    .getClassLoader().loadClass(managedArtifactClassName);
            LOGGER.exiting(managedArtifactClass.getName());
            return managedArtifactClass;
        } catch (Exception exe) {
            throw new ArtifactUploadException(exe.getClass().getSimpleName() + " in creating ManagedArtifact: "
                    + ConfigParser.parse().getString(ARTIFACT_CONFIG_PROPERTY), exe);
        }
    }

    private ManagedArtifact getManagedArtifact(String pathName) {
        try {
            ManagedArtifact managedArtifact = getConfiguredManagedArtifactClass().newInstance();
            managedArtifact.initFromPath(pathName);
            return managedArtifact;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ArtifactUploadException(e.getClass().getSimpleName() + " in creating ManagedArtifact: "
                    + ConfigParser.parse().getString(ARTIFACT_CONFIG_PROPERTY), e);
        }
    }

    public File getRepositoryFolder() {
        return repoFolder;
    }

    /**
     * RepositoryCleaner cleans repository every hour. Recursively finds directories that are empty and deletes it.
     */
    private class RepositoryCleaner extends TimerTask {

        public void run() {
            try {
                repositorySynchronizationLock.lock();
                deleteExpiredFiles();
                deleteEmptyDirectories(repoFolder);
            } finally {
                repositorySynchronizationLock.unlock();
            }
        }

        private void deleteExpiredFiles() {
            List<File> files = (List<File>) FileUtils.listFiles(repoFolder, TrueFileFilter.INSTANCE,
                    TrueFileFilter.INSTANCE);
            for (File file : files) {
                ManagedArtifact managedArtifact = getManagedArtifact(file.getAbsolutePath());
                if (managedArtifact.isExpired() && !file.delete()) {
                    LOGGER.log(Level.WARNING, "File: " + file.getName() + " not deleted from repository");
                }
            }
        }

        private void deleteEmptyDirectories(File directory) {
            if (directory == null) {
                return;
            }
            File[] files = directory.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!isDirectoryEmpty(file)) {
                        deleteEmptyDirectories(file);
                    }
                    if (isDirectoryEmpty(file)) {
                        if (!file.delete()) {
                            LOGGER.log(Level.WARNING, "Directory: " + file.getName() + " not deleted from repository");
                        }
                    }
                }
            }
        }

        private boolean isDirectoryEmpty(File directory) {
            if (directory == null) {
                return true;
            }
            String[] files = directory.list();
            return files == null || files.length == 0;
        }

    }

}
