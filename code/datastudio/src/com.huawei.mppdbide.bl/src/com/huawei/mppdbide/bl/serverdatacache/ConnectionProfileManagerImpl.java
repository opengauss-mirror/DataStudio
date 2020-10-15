/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.huawei.mppdbide.bl.connection.IConnectionProfileManager;
import com.huawei.mppdbide.bl.serverdatacache.ProfileMetaData.ProfileInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionProfileManagerImpl.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
