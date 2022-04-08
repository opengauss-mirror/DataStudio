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

import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IQueryMaterializer.
 * 
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
