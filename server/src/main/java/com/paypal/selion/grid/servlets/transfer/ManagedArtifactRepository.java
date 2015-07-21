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
import java.lang.reflect.InvocationTargetException;
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
 * <code>ManagedArtifactRepository</code> is an implementation of {@link ServerRepository} for {@link ManagedArtifact}
 * and {@link Criteria}. The class is essentially a Singleton pattern. The class implements a {@link Timer} task that
 * will run a cleaner thread that runs every hour to clean the artifacts inside the repository folder. All artifacts
 * that have {@link ManagedArtifact#isExpired()} returning true are considered for removal during the cleaning cycle.
 */
public class ManagedArtifactRepository implements ServerRepository<ManagedArtifact, Criteria> {

    private static final SeLionGridLogger LOGGER = SeLionGridLogger.getLogger(ManagedArtifactRepository.class);

    /*
     * The folder used for storing artifacts. Make sure this folder is available in the classpath or this string is
     * represented as a system property
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

    private File repoFolder = null;

    /*
     * Lock used for synchronizing reading and deletion cycles
     */
    private Lock repositorySynchronizationLock = null;

    /*
     * Cleaner thread timer
     */
    private Timer timer = null;

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
        timer = new Timer();

        // Schedule the cleaner one hour from now and every hour thereafter
        // Can be made configurable - weighing the advantage of making it so over the complexity
        // of adding a new parameter into the config files and reading it in here?
        timer.scheduleAtFixedRate(new RepositoryCleaner(), 60 * 60 * 1000, 60 * 60 * 1000);
    }

    @Override
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

    @Override
    public boolean isArtifactPresent(Criteria requestedCriteria) {

        /*
         * This method returns true if the artifact is present and not expired at the very instant the method is called.
         * The other equivalent getArtifact() method may still throw an ArtifactDownloadException if the artifact has
         * expired or deleted by the cleaner thread after this method is called. The method can give some guarantee for
         * an artifact by caching the request for an artifact for a particular time (use cache of guava lib).
         */
        boolean artifactPresent = false;
        try {
            repositorySynchronizationLock.lock();
            LOGGER.entering(requestedCriteria);
            ManagedArtifact managedArtifact = getMatch(requestedCriteria);
            artifactPresent = !managedArtifact.isExpired();
            LOGGER.exiting(artifactPresent);
        } catch (ArtifactDownloadException exe) {

            // Log and return false
            LOGGER.log(Level.WARNING, "No matching artifact", exe);
        } finally {
            repositorySynchronizationLock.unlock();
        }
        return artifactPresent;
    }

    @Override
    public ManagedArtifact getArtifact(Criteria requestedCriteria) {

        /*
         * This method does not guarantee the presence of an artifact after isArtifactPresent() is called on an
         * artifact. This is because the cleaner thread could have deleted a expired artifact in between the calls.
         */
        try {
            repositorySynchronizationLock.lock();
            LOGGER.entering(requestedCriteria);
            ManagedArtifact managedArtifact = getMatch(requestedCriteria);
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
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(uploadedArtifact.getUserId());
        if (!StringUtils.isBlank(uploadedArtifact.getApplicationFolderName())) {
            stringBuffer.append(uploadedArtifact.getApplicationFolderName());
        }
        return stringBuffer.toString().intern();
    }

    private ManagedArtifact getMatch(final Criteria criteria) {
        List<File> files = (List<File>) FileUtils.listFiles(repoFolder, TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE);
        for (File file : files) {
            ManagedArtifact managedArtifact = getManagedArtifact(file.getAbsolutePath());
            if (managedArtifact.matches(criteria)) {
                return managedArtifact;
            }
        }
        throw new ArtifactDownloadException("No artifact found for criteria, name: " + criteria.getArtifactName()
                + ", userId: " + criteria.getUserId() + ", applicationFolder: " + criteria.getApplicationFolder());
    }

    private File createFileUsing(UploadedArtifact uploadedArtifact) {
        String fileName = null;
        try {
            fileName = uploadedArtifact.getArtifactPartName();
            File file = createUserFolder(uploadedArtifact.getUserId());
            if (!StringUtils.isBlank(uploadedArtifact.getApplicationFolderName())) {
                file = createApplicationFolder(file, uploadedArtifact.getApplicationFolderName());
            }
            file = new File(file, fileName);
            if (!(file.exists() || file.createNewFile())) {
                throw new ArtifactUploadException("Cannot create file with name: " + fileName);
            }
            return file;
        } catch (IOException e) {
            throw new ArtifactUploadException("IOException in creating file: " + fileName, e);
        }
    }

    private File createUserFolder(String userId) {
        File userFolder = new File(repoFolder, userId);
        userFolder.mkdirs();
        return userFolder;
    }

    private File createApplicationFolder(File userFolder, String applicationFolderName) {
        File applicationFolder = new File(userFolder, applicationFolderName);
        applicationFolder.mkdirs();
        return applicationFolder;
    }

    @SuppressWarnings("unchecked")
    private ManagedArtifact getManagedArtifact(String pathName) {
        ManagedArtifact managedArtifact = null;
        try {
            String managedArtifactClassName = ConfigParser.parse().getString(ARTIFACT_CONFIG_PROPERTY);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "ManagedArtifact class name configured in grid: " + managedArtifactClassName);
            }
            Class<? extends ManagedArtifact> managedArtifactClass = (Class<? extends ManagedArtifact>) this.getClass()
                    .getClassLoader().loadClass(managedArtifactClassName);
            managedArtifact = managedArtifactClass.getConstructor(new Class[] { String.class }).newInstance(
                    new Object[] { pathName });
            return managedArtifact;
        } catch (InvocationTargetException exe) {
            throw new ArtifactUploadException(exe.getCause().getMessage(), exe);
        } catch (Exception exe) {
            throw new ArtifactUploadException(exe.getClass().getSimpleName() + " in creating ManagedArtifact : "
                    + ConfigParser.parse().getString(ARTIFACT_CONFIG_PROPERTY), exe);
        }
    }

    /**
     * RepositoryCleaner cleans repository every hour. Recursively finds directories that are empty and deletes it.
     */
    private class RepositoryCleaner extends TimerTask {

        @Override
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
            for (File file : directory.listFiles()) {
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
            return directory.list().length == 0;
        }

    }

}
