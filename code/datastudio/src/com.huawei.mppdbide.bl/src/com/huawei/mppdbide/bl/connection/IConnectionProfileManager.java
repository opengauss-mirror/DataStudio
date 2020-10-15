/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
