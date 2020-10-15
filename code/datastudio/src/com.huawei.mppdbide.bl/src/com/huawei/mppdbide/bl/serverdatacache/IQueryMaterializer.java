/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.SQLException;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IQueryMaterializer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IQueryMaterializer {

    /**
     * Materialize query result.
     *
     * @param irq the irq
     * @return the object
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    Object materializeQueryResult(IQueryResult irq) throws DatabaseCriticalException, DatabaseOperationException;

    /**
     * Gets the materialized query result.
     *
     * @return the materialized query result
     */
    Object getMaterializedQueryResult();

    /**
     * Materialize query result.
     * 
     * @param irq the irq
     * @param isCallableStmt flag that indicates callable statement
     * @return the object
     * @throws DatabaseCriticalException
     * @throws DatabaseOperationException
     * @throws SQLException
     */
    Object materializeQueryResult(IQueryResult irq, boolean isCallableStmt)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException;
}
