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

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ITableMetaData.
 * 
 */

public interface ITableMetaData {

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the name space name.
     *
     * @return the name space name
     */
    String getNameSpaceName();

    /**
     * Gets the database.
     *
     * @return the database
     */
    Database getDatabase();

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    String getServerName();

    /**
     * Exec drop.
     *
     * @param connection the connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    void execDrop(DBConnection connection) throws DatabaseCriticalException, DatabaseOperationException;

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    Object getParent();

    /**
     * Checks if is dropped.
     *
     * @return true, if is dropped
     */
    boolean isDropped();

    /**
     * Gets the ddl.
     *
     * @param db the db
     * @return the ddl
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    default String getDDL(Database db) throws MPPDBIDEException {
        return "-- no DDL";
    }

}
