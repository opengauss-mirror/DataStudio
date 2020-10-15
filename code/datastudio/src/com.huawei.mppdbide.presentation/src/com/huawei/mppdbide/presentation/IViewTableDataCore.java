/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IViewTableDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
