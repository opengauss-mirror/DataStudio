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

package org.opengauss.mppdbide.presentation.grid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSResultRowVisitor.
 * 
 * @since 3.0.0
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
