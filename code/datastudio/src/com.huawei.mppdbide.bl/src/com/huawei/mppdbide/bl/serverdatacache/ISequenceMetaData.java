/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
