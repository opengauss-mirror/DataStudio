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

package org.opengauss.mppdbide.presentation;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IViewTableDataCore.
 *
 * @since 3.0.0
 */
public interface IViewTableDataCore {

    /**
     * Inits the.
     *
     * @param obj the obj
     */
    void init(ServerObject obj);

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    ServerObject getServerObject();

    /**
     * Gets the window details.
     *
     * @return the window details
     */
    IWindowDetail getWindowDetails();

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    String getWindowTitle();

    /**
     * Gets the progress bar label.
     *
     * @return the progress bar label
     */
    String getProgressBarLabel();

    /**
     * Gets the query.
     *
     * @return the query
     * @throws DatabaseOperationException 
     */
    String getQuery() throws DatabaseOperationException;

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    TerminalExecutionConnectionInfra getTermConnection();

    /**
     * Checks if is table dropped.
     *
     * @return true, if is table dropped
     */
    default boolean isTableDropped() {
        return true;
    }

    /**
     * Refresh table/view.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    default void refreshTable(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
    }
}
