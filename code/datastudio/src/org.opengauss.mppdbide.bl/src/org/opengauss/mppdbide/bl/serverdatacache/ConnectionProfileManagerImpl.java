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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.opengauss.mppdbide.bl.connection.IConnectionProfileManager;
import org.opengauss.mppdbide.bl.serverdatacache.ProfileMetaData.ProfileInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import org.opengauss.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.MemoryCleaner;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionProfileManagerImpl.
 * 
 */

public final class ConnectionProfileManagerImpl implements IConnectionProfileManager {

    private HashMap<String, IServerConnectionInfo> profileMap;
    private static volatile ConnectionProfileManagerImpl instance;
    private ProfileDiskUtility diskUtility;
    private static final Object LOCK = new Object();

    /**
     * Gets the exception list.
     *
     * @return the exception list
     */
    public List<String> getExceptionList() {
        return diskUtility.getExceptionList();
    }

    /**
     * Clear exception list.
     */
    public void clearExceptionList() {
        diskUtility.clearExceptionList();
    }

    /**
     * Instantiates a new connection profile manager impl.
     */
    private ConnectionProfileManagerImpl() {
        profileMap = new HashMap<String, IServerConnectionInfo>(1);
        setDiskUtility(new ProfileDiskUtility());
    }

    /**
     * Gets the disk utility.
     *
     * @return the disk utility
     */
    public ProfileDiskUtility getDiskUtility() {
        return diskUtility;
    }

    /**
     * Checks if is profile map empty.
     *
     * @return true, if is profile map empty
     */
    public boolean isProfileMapEmpty() {
        return profileMap.isEmpty();
    }

    /**
     * Sets the disk utility.
     *
     * @param diskUtility the new disk utility
     */
    public void setDiskUtility(ProfileDiskUtility diskUtility) {
        this.diskUtility = diskUtility;
    }

    /**
     * Gets the single instance of ConnectionProfileManagerImpl.
     *
     * @return single instance of ConnectionProfileManagerImpl
     */
    public static ConnectionProfileManagerImpl getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ConnectionProfileManagerImpl();
                }
            }
        }

        return instance;
    }

    @Override
    public void deleteProfile(IServerConnectionInfo profile) throws DatabaseOperationException {
        ISqlHistoryManager historyManager = SQLHistoryFactory.getInstance();
        historyManager.removeHistoryManagementForProfile(profile.getProfileId());

        if (profileMap.containsKey(profile.getConectionName())) {
            getDiskUtility().dropProfileFolder(getDiskUtility().getUserProfileFolderName(profile.getProfileId()),
                    profile.getConectionName());
            profileMap.remove(profile.getConectionName());
        }
        MPPDBIDELoggerUtility.operationInfo("Deleted user profile :" + profile.getProfileId());
    }

    /**
     * Generate profile id.
     *
     * @return the int
     */
    private int generateProfileId() {
        int profileId = 0;
        List<Integer> profileIds = getDiskUtility().getMetaData().getAllProfileIds();
        if (profileIds.size() > 0) {
            Collections.sort(profileIds);
            // Get the last element in the list. Implies the highest of all
            // values
            int localProfileId = profileIds.get(profileIds.size() - 1);
            profileId = localProfileId;
        }

        return ++profileId;
    }

    @Override
    public void saveProfile(IServerConnectionInfo profile)
            throws DatabaseOperationException, DataStudioSecurityException {
        if (profile != null && profile.getConectionName() != null && !("".equals(profile.getConectionName().trim()))) {

            String profId = profile.getProfileId();
            getDiskUtility().writeProfileToDisk(profile);

            ISqlHistoryManager historyManager = SQLHistoryFactory.getInstance();
            String path = getDiskUtility().getHistoryProfilepath(getDiskUtility().getUserProfileFolderName(profId))
                    .toString();
            historyManager.doHistoryManagementForProfile(profId, path);

            try {
                // load the profiles from HDD after save operation
                getAllProfiles();
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_WRITE_INVALID), exception);
            }
        }
    }

    private String generateAndSetProfileId(IServerConnectionInfo profile) {
        String conectionName = profile.getConectionName();
        int profileId = getDiskUtility().getMetaData().getProfileId(conectionName);
        if (profileId == -1) {
            // Profile id doesnt exist. Generate new profileId
            profileId = generateProfileId();
        }
        String profId = Integer.toString(profileId);
        profile.setProfileId(profId);
        return profId;
    }

    @Override
    public void renameProfile(IServerConnectionInfo profile)
            throws DatabaseOperationException, DataStudioSecurityException {
        getDiskUtility().renameProfileOnDisk(profile);
        try {
            // load the profiles from HDD after rename operation
            getAllProfiles();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_WRITE_INVALID),
                    exception);
        }
    }

    @Override
    public IServerConnectionInfo getProfile(String name) {
        IServerConnectionInfo connInfo = null;
        if (profileMap.containsKey(name)) {
            IServerConnectionInfo serverInfo = profileMap.get(name);
            if (serverInfo != null) {
                connInfo = serverInfo.getClone();
                connInfo.setPrd(serverInfo.getPrd());
            }
        }
        return connInfo;
    }

    @Override
    public List<IServerConnectionInfo> getAllProfiles()
            throws DatabaseOperationException, DataStudioSecurityException, IOException {
        List<IServerConnectionInfo> list = getDiskUtility().getProfiles();
        profileMap.clear();

        for (IServerConnectionInfo info : list) {
            if (info != null) {
                profileMap.put(info.getConectionName(), info);
            }
        }
        return list;
    }

    /**
     * Clear permanent save pwd.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void clearPermanentSavePwd() throws DatabaseOperationException, DataStudioSecurityException, IOException {
        List<IServerConnectionInfo> list = getAllProfiles();
        for (IServerConnectionInfo info : list) {
            info.setPrd(new char[0]);
            MemoryCleaner.cleanUpMemory();
            saveProfile(info);
        }
    }

    @Override
    public void exportConnectionProfiles(List<IServerConnectionInfo> profiles, String filePath)
            throws DatabaseOperationException, DataStudioSecurityException {
        getDiskUtility().writeExportedProfileToDisk(profiles, filePath);
    }

    @Override
    public List<IServerConnectionInfo> importConnectionProfiles(String path, double fileSizeLimit)
            throws DatabaseOperationException {
        List<IServerConnectionInfo> readImportedFile;
        readImportedFile = getDiskUtility().readImportedFile(path, fileSizeLimit);
        return readImportedFile;
    }

    @Override
    public void mergeImportedProfiles(List<IServerConnectionInfo> profilesList, List<IServerConnectionInfo> allProfiles)
            throws DatabaseOperationException {
        ProfileDiskUtility diskUtilityProfiles = getDiskUtility();
        try {
            for (IServerConnectionInfo info : profilesList) {
                diskUtilityProfiles.writeProfileToDisk(info);
                // the check is to ensure that no two callers adds the same
                // profile multiple times
                if (!allProfiles.contains(info)) {
                    allProfiles.add(info);
                }
            }

        } catch (DataStudioSecurityException exp) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES), exp);
            throw new DatabaseOperationException(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES), exp);
        }

    }

    @Override
    public void replaceWithImportedProfiles(IServerConnectionInfo profToBeDeleted,
            IServerConnectionInfo profToBeCreated, List<IServerConnectionInfo> allProfiles)
            throws DatabaseOperationException, DataStudioSecurityException {
        ProfileDiskUtility diskUtilityProfiles = getDiskUtility();
        deleteProfile(profToBeDeleted);
        diskUtilityProfiles.writeProfileToDisk(profToBeCreated);
        allProfiles.remove(profToBeDeleted);
        allProfiles.add(profToBeCreated);
    }

    /**
     * Checks if is profile info available in meta data.
     *
     * @param connName the conn name
     * @return true, if is profile info available in meta data
     */
    public boolean isProfileInfoAvailableInMetaData(String connName) {
        ProfileInfo prof = getDiskUtility().getMetaData().getAllProfiles().get(connName);
        return (prof != null);
    }

    /**
     * Sets the profile path.
     * 
     * @param info the new profile path
     * @return path profile path
     */
    public String getProfilePath(IServerConnectionInfo info) {
        String profilePath = getDiskUtility()
                .getConnectionProfilepath(getDiskUtility().getUserProfileFolderName(info.getProfileId())).toString();
        return profilePath;
    }

    /**
     * Generate security folder inside profile.
     *
     * @param connInfo the conn info
     * @return the path
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws FileOperationException the file operation exception
     * @throws DataStudioSecurityException the data studio security exception
     * @throws DatabaseOperationException
     */
    public Path generateSecurityFolderInsideProfile(IServerConnectionInfo connInfo)
            throws IOException, FileOperationException, DataStudioSecurityException, DatabaseOperationException {
        Path folderPath;
        String profId;

        /* Get profile Id */
        profId = generateAndSetProfileId(connInfo);

        /* Create folder Structure */
        folderPath = getDiskUtility().createFolderStructure(getDiskUtility().getUserProfileFolderName(profId));
        runPreEncryptionTask(folderPath);
        return folderPath;
    }

    /** 
     * runPreEncryptionTask
     *
     * @param folderPath folder path
     * @throws DataStudioSecurityException exception
     */
    public void runPreEncryptionTask(Path folderPath) throws DataStudioSecurityException {
        SecureUtil secureUtil = new SecureUtil();
        secureUtil.setPackagePath(folderPath.toString());
        secureUtil.runPreEncryptionTask();
    }
}
