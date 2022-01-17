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

package com.huawei.mppdbide.bl.connection;

import java.io.IOException;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IConnectionProfileManager.
 * 
 */

public interface IConnectionProfileManager {

    /**
     * Delete profile.
     *
     * @param profile the profile
     * @throws DatabaseOperationException the database operation exception
     */
    void deleteProfile(IServerConnectionInfo profile) throws DatabaseOperationException;

    /**
     * Save profile.
     *
     * @param profile the profile
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    void saveProfile(IServerConnectionInfo profile) throws DatabaseOperationException, DataStudioSecurityException;

    /**
     * Gets the all profiles.
     *
     * @return the all profiles
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    List<IServerConnectionInfo> getAllProfiles()
            throws DatabaseOperationException, DataStudioSecurityException, IOException;

    /**
     * Gets the profile.
     *
     * @param id the id
     * @return the profile
     */
    IServerConnectionInfo getProfile(String id);

    /**
     * Rename profile.
     *
     * @param profile the profile
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    void renameProfile(IServerConnectionInfo profile) throws DatabaseOperationException, DataStudioSecurityException;

    /**
     * Clear permanent save pwd.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void clearPermanentSavePwd() throws DatabaseOperationException, DataStudioSecurityException, IOException;

    /**
     * Export connection profiles.
     *
     * @param profiles the profiles
     * @param filePath the file path
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    void exportConnectionProfiles(List<IServerConnectionInfo> profiles, String filePath)
            throws DatabaseOperationException, DataStudioSecurityException;

    /**
     * Import connection profiles.
     *
     * @param path the path
     * @param fileSizeLimit the file data size limit
     * @return the list
     * @throws DatabaseOperationException the database operation exception
     */
    List<IServerConnectionInfo> importConnectionProfiles(String path, double fileSizeLimit)
            throws DatabaseOperationException;

    /**
     * Merge imported profiles.
     *
     * @param profilesList the profiles list
     * @param allProfiles the all profiles
     * @throws DatabaseOperationException the database operation exception
     */
    void mergeImportedProfiles(List<IServerConnectionInfo> profilesList, List<IServerConnectionInfo> allProfiles)
            throws DatabaseOperationException;

    /**
     * Replace with imported profiles.
     *
     * @param profileToDeleted the profile to deleted
     * @param profileToBeCreated the profile to be created
     * @param allProfiles the all profiles
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    void replaceWithImportedProfiles(IServerConnectionInfo profileToDeleted, IServerConnectionInfo profileToBeCreated,
            List<IServerConnectionInfo> allProfiles) throws DatabaseOperationException, DataStudioSecurityException;

}
