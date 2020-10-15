/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportimportdsconnectionprofiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportConnectionProfileCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ImportConnectionProfileCore {
    private String sourcePath;
    private double fileSizeLimit;
    private List<IServerConnectionInfo> uniqueList;
    private List<IServerConnectionInfo> sourceList;
    private List<IServerConnectionInfo> destinationList;
    private List<MatchedConnectionProfiles> matchedList;

    /**
     * Instantiates a new import connection profile core.
     *
     * @param sourceFilePath the source file path
     * @param sourceFileSizeLimit the file data size limit
     */
    public ImportConnectionProfileCore(String sourceFilePath, double sourceFileSizeLimit) {
        sourcePath = sourceFilePath;
        fileSizeLimit = sourceFileSizeLimit;
        uniqueList = new LinkedList<IServerConnectionInfo>();
        matchedList = new LinkedList<MatchedConnectionProfiles>();
    }

    /**
     * Import files.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    public void importFiles() throws DatabaseOperationException {
        ConnectionProfileManagerImpl connectionManager = ConnectionProfileManagerImpl.getInstance();
        compareFiles(readSourceFiles(connectionManager), readDestinationFile(connectionManager));
    }

    private List<IServerConnectionInfo> readSourceFiles(ConnectionProfileManagerImpl connectionManager)
            throws DatabaseOperationException {
        sourceList = connectionManager.importConnectionProfiles(sourcePath, fileSizeLimit);

        return sourceList;
    }

    private List<IServerConnectionInfo> readDestinationFile(ConnectionProfileManagerImpl connectionManager)
            throws DatabaseOperationException {
        try {
            destinationList = connectionManager.getAllProfiles();
        } catch (DataStudioSecurityException | IOException exe) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES), exe);
            throw new DatabaseOperationException(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES), exe);
        }

        return destinationList;
    }

    private void compareFiles(List<IServerConnectionInfo> sourceList, List<IServerConnectionInfo> destinationList) {
        Map<String, IServerConnectionInfo> destinationProfileMap = new HashMap<>();
        for (IServerConnectionInfo info : destinationList) {
            destinationProfileMap.put(info.getConectionName(), info);
        }
        for (IServerConnectionInfo info : sourceList) {
            if (destinationProfileMap.containsKey(info.getConectionName())) {
                MatchedConnectionProfiles matchingProfiles = new MatchedConnectionProfiles();
                matchingProfiles.setDestProfile(destinationProfileMap.get(info.getConectionName()));
                matchingProfiles.setSourceProfile(info);
                matchedList.add(matchingProfiles);

            } else {
                uniqueList.add(info);
            }
        }

    }

    /**
     * Gets the original destination list.
     *
     * @return the original destination list
     */
    public List<IServerConnectionInfo> getOriginalDestinationList() {
        return this.destinationList;
    }

    /**
     * Gets the unique list.
     *
     * @return the unique list
     */
    public List<IServerConnectionInfo> getUniqueList() {
        return this.uniqueList;
    }

    /**
     * Gets the matched profiles list.
     *
     * @return the matched profiles list
     */
    public List<MatchedConnectionProfiles> getMatchedProfilesList() {
        return this.matchedList;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class MatchedConnectionProfiles.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public static class MatchedConnectionProfiles {
        private IServerConnectionInfo sourceProfile;
        private IServerConnectionInfo destProfile;

        /**
         * Gets the source profile.
         *
         * @return the source profile
         */
        public IServerConnectionInfo getSourceProfile() {
            return sourceProfile;
        }

        /**
         * Sets the source profile.
         *
         * @param sourceProfile the new source profile
         */
        public void setSourceProfile(IServerConnectionInfo sourceProfile) {
            this.sourceProfile = sourceProfile;
        }

        /**
         * Gets the dest profile.
         *
         * @return the dest profile
         */
        public IServerConnectionInfo getDestProfile() {
            return destProfile;
        }

        /**
         * Sets the dest profile.
         *
         * @param destProfile the new dest profile
         */
        public void setDestProfile(IServerConnectionInfo destProfile) {
            this.destProfile = destProfile;
        }

    }

}
