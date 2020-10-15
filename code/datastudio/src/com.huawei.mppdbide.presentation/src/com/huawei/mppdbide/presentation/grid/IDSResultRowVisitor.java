/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.grid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSResultRowVisitor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IDSResultRowVisitor {

    /** 
     * the visit
     * 
     * @param rs the rs
     * @param isfuncProcResultFlow the isfuncProcResultFlow
     * @param isCursorType the isCursorType
     * @param arrayList the arrayList
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws SQLException the SQLException
     */
    void visit(ResultSet rs, boolean isfuncProcResultFlow, boolean isCursorType, Object[] arrayList)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException;

    /** 
     * the visit
     * 
     * @param rs the rs
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws SQLException the SQLException
     */
    void visit(ResultSet rs) throws DatabaseOperationException, DatabaseCriticalException, SQLException;

    /** 
     * the visit
     * 
     * @param gridDataRow the gridDataRow
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws SQLException the SQLException
     */
    void visit(DSResultSetGridDataRow gridDataRow)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException;

    /**
     * Sets the end of records.
     */
    void setEndOfRecords();

    /**
     * Checks if is end of records.
     *
     * @return true, if is end of records
     */
    boolean isEndOfRecords();

    /**
     * visit input values
     * 
     * @param dp  the dp
     * @param index the 
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     */
    void visitInputValues(DefaultParameter dp, int index) throws DatabaseOperationException, DatabaseCriticalException;

    /** 
     * visits the UnNameCursor
     * 
     * @param rs the rs
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws SQLException the SQLException
     */
    void visitUnNameCursor(ResultSet rs) throws DatabaseOperationException, DatabaseCriticalException, SQLException;
}
