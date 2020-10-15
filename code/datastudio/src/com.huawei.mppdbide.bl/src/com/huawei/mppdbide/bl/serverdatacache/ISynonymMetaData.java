/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.SQLException;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: Class
 * 
 * Description: The Interface ISynonymMetaData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author c00550043
 * @version
 * @since Mar7, 2020
 */
public interface ISynonymMetaData {
    /**
     * Drop synonym.
     *
     * @param conn the conn
     * @param isAppendCascade the is append cascade
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException the SQL exception
     */
    void dropSynonym(DBConnection conn, boolean isAppendCascade)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException;

    /**
     * Refresh sequence.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    void refreshSynonym(DBConnection dbConnection) throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the qualified object name.
     *
     * @return the qualified object name
     */
    String getQualifiedObjectName();

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    INamespace getParent();

    /**
     * Gets the database.
     *
     * @return the database
     */
    Database getDatabase();

    /**
     * Gets the server.
     *
     * @return the server
     */
    Server getServer();
}
