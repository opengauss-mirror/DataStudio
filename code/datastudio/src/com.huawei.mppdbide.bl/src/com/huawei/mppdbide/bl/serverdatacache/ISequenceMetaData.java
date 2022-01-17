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

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.SQLException;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISequenceMetaData.
 * 
 */

public interface ISequenceMetaData {

    /**
     * Drop sequence.
     *
     * @param conn the conn
     * @param isAppendCascade the is append cascade
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException the SQL exception
     */
    public void dropSequence(DBConnection conn, boolean isAppendCascade)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException;

    /**
     * Refresh sequence.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshSequence(DBConnection dbConnection) throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the name space.
     *
     * @return the name space
     */
    public INamespace getSeqNameSpace();

    /**
     * Gets the object full name.
     *
     * @return the object full name
     */
    public String getObjectFullName();

    /**
     * Gets the qualified object name.
     *
     * @return the qualified object name
     */
    public String getQualifiedObjectName();

}
