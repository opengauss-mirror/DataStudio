/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ITableMetaData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
