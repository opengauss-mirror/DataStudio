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

import java.sql.SQLException;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: Class
 * 
 * Description: The Interface ISynonymMetaData.
 *
 * @since 3.0.0
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
