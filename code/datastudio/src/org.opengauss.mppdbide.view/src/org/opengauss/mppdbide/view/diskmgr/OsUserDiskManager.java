/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.view.diskmgr;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.utils.EnvirnmentVariableValidator;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import org.opengauss.mppdbide.view.autosave.AutoSaveManager;
import org.opengauss.mppdbide.view.data.DSViewDataManager;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class OsUserDiskManager.
 *
 * @since 3.0.0
 */
public final class OsUserDiskManager {
    private static final String LOGS = "logs";
    private static final String DATA_STUDIO_LOG = "Data Studio.log";
    private static final String DATA_STUDIO_SECURITY_LOG = "security.log";
    private static final String DATA_STUDIO_OPERATION_LOG = "operation.log";
    private String userDataFolder = MPPDBIDEConstants.USER_DATA_FOLDER;
    private String usrFolderName = "";
    private ISetFilePermission filePermissions = FilePermissionFactory.getFilePermissionInstance();
    private static volatile OsUserDiskManager instance;
    private static final Object LOCK = new Object();
    private String currentOsUserPath;

    /**
     * Gets the single instance of OsUserDiskManager.
     *
     * @return single instance of OsUserDiskManager
     */
    public static OsUserDiskManager getInstance() {

        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new OsUserDiskManager();
                }
            }

        }

        return instance;
    }

    private OsUserDiskManager() {

    }

    private void initializeOsUserName() throws DataStudioSecurityException {
        String usrName = EnvirnmentVariableValidator.validateAndGetUserName();
        if (usrName.isEmpty()) {
            throw new DataStudioSecurityException("Not able to get the OS Username.");
        }
        usrFolderName = usrName;
    }

    /**
     * Creates the parent folder structure.
     *
     * @param isReadFilePath the is read file path
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void createParentFolderStructure(String isReadFilePath) throws MPPDBIDEException {

        Path folderPath = getUserDataFolderPath();
        String normalizedFolderPath = (folderPath != null && folderPath.normalize() != null)
                ? folderPath.normalize().toString()
                : "";
        filePermissions.createFileWithPermission(normalizedFolderPath, true, null, false);

        // Create osUserFolder
        SecureUtil secureUtil = new SecureUtil();
        createOsUserFolderStructure(folderPath, secureUtil);

        // Create the log folder
        createLogFolder(isReadFilePath);

        // Get Initial Instance of SecureUtil and Create User level security
        // folder
        secureUtil.runPreEncryptionTask();

    }

    private void createOsUserFolderStructure(Path folderPath, SecureUtil secureUtil) throws MPPDBIDEException {

        currentOsUserPath = getOsUserFolderPath(folderPath);
        DSViewDataManager.getInstance().setCurrentOsUserPath(currentOsUserPath);

        filePermissions.createFileWithPermission(currentOsUserPath, true, null, true);

        secureUtil.setPackagePath(currentOsUserPath);

        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(currentOsUserPath);
        AutoSaveManager.getInstance().getDiskUtility().setOsCurrentUserFolderPath(currentOsUserPath);
    }

    private String getOsUserFolderPath(Path folderPath) throws DataStudioSecurityException {
        if ("".equals(usrFolderName)) {
            initializeOsUserName();
            if (null == usrFolderName || "".equals(usrFolderName)) {
                MPPDBIDELoggerUtility.error("Not able to get the OS Username.");
                throw new DataStudioSecurityException("Not able to get the OS Username.");
            }
        }
        Path osUserFolderPath = Paths.get(userDataFolder, usrFolderName);
        return osUserFolderPath.toString();
    }

    private void createLogFolder(String logFolder) throws MPPDBIDEException {

        try {
            String logPath = null;
            if (null != logFolder && !".".equals(logFolder)) {
                File file = new File(logFolder);
                if (!file.exists()) {
                    createFileIfNotExiste();
                    logPath = currentOsUserPath;
                } else {
                    createPermissionIfFileExists(logFolder, file);
                    logPath = logFolder + File.separator + usrFolderName;
                }

            } else {
                createNewLogFolder();
                logPath = currentOsUserPath;
            }
            // add to system property.
            System.setProperty("mppide.log",
                    logPath + File.separator + LOGS + File.separator + DATA_STUDIO_LOG);
            System.setProperty("mppidesecurity.log",
                    logPath + File.separator + LOGS + File.separator + DATA_STUDIO_SECURITY_LOG);
            System.setProperty("mppideoperation.log",
                    logPath + File.separator + LOGS + File.separator + DATA_STUDIO_OPERATION_LOG);
        } catch (DatabaseOperationException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.LOG_ERR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.LOG_ERR_MSG, e.getMessage()));
        }

        MPPDBIDELoggerUtility.checkAndCreateLogger(true);
    }

    private void createNewLogFolder() throws MPPDBIDEException {
        File file = new File(currentOsUserPath);
        // Check if folder has write permission
        boolean canWrite = Files.isWritable(file.toPath());

        if (canWrite) {
            String logsFolder = currentOsUserPath + File.separator + LOGS;
            ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();
            Path pathLogsFolder = withPermission.createFileWithPermission(logsFolder, true, null, true);

            if (Files.isWritable(pathLogsFolder) || pathLogsFolder.toFile().exists()) {
                String logFileName = pathLogsFolder + File.separator + DATA_STUDIO_LOG;
                createLogFile("mppide.log", logFileName);
                String securitylogFileName = pathLogsFolder + File.separator + DATA_STUDIO_SECURITY_LOG;
                createLogFile("mppidesecurity.log", securitylogFileName);
                String operationlogFileName = pathLogsFolder + File.separator + DATA_STUDIO_OPERATION_LOG;
                createLogFile("mppideoperation.log", operationlogFileName);
            }
        }
    }

    private void createLogFile(String propertyName, String logFileName)
            throws DatabaseOperationException, MPPDBIDEException {
        boolean canWrite;

        ISetFilePermission withPermissionLogFileName = FilePermissionFactory.getFilePermissionInstance();
        Path pathLogFileName = withPermissionLogFileName.createFileWithPermission(logFileName, false, null, true);

        // check if the file has write permission
        canWrite = Files.isWritable(pathLogFileName);
        // If if file has write permission or file does not exist,
        // set
        // the user mentioned path
        // If file does not exist, log4j framework will create the
        // file
        if (canWrite || !pathLogFileName.toFile().exists()) {
            System.setProperty(propertyName, logFileName);
        } else {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_CREATE_FILE_DIRECTORY));
            throw new MPPDBIDEException(IMessagesConstants.ERR_BL_CREATE_FILE_DIRECTORY);
        }
    }

    private void createPermissionIfFileExists(String logFolder, File file)
            throws DatabaseOperationException, MPPDBIDEException {
        // Check if folder has write permission
        boolean canWrite = Files.isWritable(file.toPath());

        if (canWrite) {
            String userDirectory = logFolder + File.separator + usrFolderName + File.separator;
            filePermissions.createFileWithPermission(userDirectory, true, null, true);
        } else {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_CREATE_FILE_DIRECTORY));
            throw new MPPDBIDEException(IMessagesConstants.ERR_BL_CREATE_FILE_DIRECTORY);
        }
    }

    private void createFileIfNotExiste() throws DatabaseOperationException {
        String logsDir;
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.INCORRECT_PATH),
                MessageConfigLoader.getProperty(IMessagesConstants.INCORRECT_PATH_MSG));
        logsDir = currentOsUserPath + File.separator + LOGS;

        filePermissions.createFileWithPermission(logsDir, true, null, true);
    }

    /**
     * Gets the user data folder path.
     *
     * @return the user data folder path
     */
    public Path getUserDataFolderPath() {
        Path parentPath = Paths.get(BLUtils.getInstance().getInstallationLocation(),
                MPPDBIDEConstants.USER_DATA_FOLDER);
        return (parentPath.toAbsolutePath() != null) ? parentPath.toAbsolutePath().normalize() : null;
    }
}
