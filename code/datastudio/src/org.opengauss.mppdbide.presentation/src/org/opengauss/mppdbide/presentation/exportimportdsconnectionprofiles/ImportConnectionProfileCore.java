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

package org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportConnectionProfileCore.
 *
 * @since 3.0.0
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
