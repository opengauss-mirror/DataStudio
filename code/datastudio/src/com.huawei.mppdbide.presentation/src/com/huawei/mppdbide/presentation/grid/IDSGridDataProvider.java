/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.grid;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.observer.IDSListenable;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridDataProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IDSGridDataProvider extends IDSListenable {

    /**
     * Inits the.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    void init() throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Close.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    default void close() throws DatabaseOperationException, DatabaseCriticalException {

    }

    /**
     * Gets the next batch.
     *
     * @return the next batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    default List<IDSGridDataRow> getNextBatch() throws DatabaseOperationException, DatabaseCriticalException {
        return null;
    }

    /**
     * Gets the all fetched rows.
     *
     * @return the all fetched rows
     */
    List<IDSGridDataRow> getAllFetchedRows();

    /**
     * Checks if is end of records.
     *
     * @return true, if is end of records
     */
    boolean isEndOfRecords();

    /**
     * Gets the record count.
     *
     * @return the record count
     */
    int getRecordCount();

    /**
     * Gets the column data provider.
     *
     * @return the column data provider
     */
    IDSGridColumnProvider getColumnDataProvider();

    /**
     * Pre destroy.
     */
    default void preDestroy() {

    }

    /**
     * Gets the column group provider.
     *
     * @return the column group provider
     */
    default IDSGridColumnGroupProvider getColumnGroupProvider() {
        return null;
    }

    /**
     * Gets the table.
     *
     * @return the table
     */
    ServerObject getTable();

    /**
     * Gets the data base.
     *
     * @return the data base
     */
    Database getDatabse();

    /**
     * Gets the result tab dirty flag.
     *
     * @return the result tab dirty flag
     */
    boolean getResultTabDirtyFlag();

    /**
     * Sets the result tab dirty flag.
     *
     * @param flag the new result tab dirty flag
     */
    void setResultTabDirtyFlag(boolean flag);

    /**
     * init
     * 
     * @param irq the irq
     * @param debugInputValueList the debugInputValueList
     * @param isCallableStmt the isCallableStmt
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws SQLException the SQLException
     */
    void init(IQueryResult irq, ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException;

    /**
     * get next batch
     * 
     * @param debugInputValueList the debugInputValueList
     * @return next batch object
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws SQLException the SQLException
     */
    List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValueList)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException;

    /**
     * sets the setFuncProcExport flag
     * 
     * @param isFuncProcExport the isFuncProcExport flag
     */
    void setFuncProcExport(boolean isFuncProcExport);

    /**
     * gets the setFuncProcExport flag
     * 
     * @return isFuncProcExport the isFuncProcExport flag
     */
    boolean isFuncProcExport();
}
