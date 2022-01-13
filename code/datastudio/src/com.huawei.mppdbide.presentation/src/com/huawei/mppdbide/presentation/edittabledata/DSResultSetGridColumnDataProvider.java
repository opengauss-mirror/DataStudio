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

package com.huawei.mppdbide.presentation.edittabledata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.QueryResult;
import com.huawei.mppdbide.bl.serverdatacache.ResultSetColumn;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.resultset.ColumnValueSqlTypeComparator;
import com.huawei.mppdbide.presentation.grid.resultset.CursorQueryResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSResultSetGridColumnDataProvider.
 * 
 * @since 3.0.0
 */
public class DSResultSetGridColumnDataProvider implements IDSGridColumnProvider {
    private ResultSetColumn[] cols;
    private int columnCount;

    /**
     * Inits the.
     *
     * @param queryResult the query result
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void init(IQueryResult queryResult) throws DatabaseOperationException, DatabaseCriticalException {
        this.columnCount = queryResult.getColumnCount();
        this.cols = queryResult.getColumnMetaData();
    }

    /**
     * initialize column header name
     * 
     * @param colValueList the colValueList
     * @param isCallableStmt the isCallableStmt
     * @param iQueryResult the iQueryResult
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws DatabaseOperationException the DatabaseOperationException
     */
    public void initColHeaderName(Object[] colValueList, boolean isCallableStmt, IQueryResult iQueryResult)
            throws DatabaseCriticalException, DatabaseOperationException {
        List<String> columnHeaderName = new ArrayList<String>(columnCount);
        boolean isCursorType = initColumnHeader(colValueList, columnHeaderName);
        if (colValueList != null) {
            this.columnCount = colValueList.length;
        } else {
            this.columnCount = 4;
        }
        if (iQueryResult instanceof CursorQueryResult) {
            this.cols = ((CursorQueryResult) iQueryResult).getColumnHeaderName(this.columnCount, columnHeaderName,
                    isCallableStmt, isCursorType);
        }
        if (iQueryResult instanceof QueryResult) {
            this.cols = ((QueryResult) iQueryResult).getColumnHeaderName(this.columnCount, columnHeaderName,
                    isCallableStmt, isCursorType);
        }
    }

    /**
     * initialize column header name by visitor
     * 
     * @param colValueList the colValueList
     * @param isCallableStmt the isCallableStmt
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws DatabaseOperationException the DatabaseOperationException
     */
    public void initByVisitorColHeaderName(Object[] colValueList, boolean isCallableStmt, IQueryResult iQryResult)
            throws DatabaseCriticalException, DatabaseOperationException {
        List<String> columnHeaderName = new ArrayList<String>(columnCount);
        boolean isCursorType = initColumnHeader(colValueList, columnHeaderName);
        if (colValueList != null) {
            this.columnCount = colValueList.length;
        } else {
            this.columnCount = 4;
        }
        if (iQryResult != null && iQryResult instanceof CursorQueryResult) {
            this.cols = ((CursorQueryResult) iQryResult).getColumnHeaderName(this.columnCount, columnHeaderName,
                    isCallableStmt, isCursorType);
        }
    }
    
    /**
     * initialize By Visitor PopUp Header Name
     * 
     * @param colValueList the colValueList
     */
    public void initByVisitorPopUpHeaderName(Object[] colValueList) {
        List<String> columnHeaderName = new ArrayList<String>();
        initColumnHeader(colValueList, columnHeaderName);
        if (colValueList != null) {
            this.columnCount = colValueList.length;
        } 
        this.cols = getCursorPopupColheaderName(this.columnCount, columnHeaderName);

    }

    /**
     * gets the cursor popup column header name
     * 
     * @param columnCount the column count
     * @param columnHeaderName the column header name
     * @param cursorPopUpdataType the cursor popup datatype
     * @return metaData the resultset column object
     */
    private ResultSetColumn[] getCursorPopupColheaderName(int columnCount, List<String> columnHeaderName) {
        ResultSetColumn[] metaData = new ResultSetColumn[columnCount];
        for (int i = 0; i < columnCount; i++) {
            metaData[i] = new ResultSetColumn(i + 1);
            metaData[i].setPopUpColumnHeaderName(columnHeaderName.get(i), i + 1);
        }
        return metaData;
    }

    /**
     * init column header
     * 
     * @param colValueList the colValueList
     * @param columnHeaderName the columnHeaderName
     * @return return true, if is cursor type
     */
    private boolean initColumnHeader(Object[] colValueList, List<String> columnHeaderName) {
        boolean isCursorType = false;
        if (colValueList != null) {
            for (int i = 0; i < colValueList.length; i++) {
                columnHeaderName.add((String) colValueList[i]);
            }
            isCursorType = true;
        } else {
            columnHeaderName.add(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_COL_NAME));
            columnHeaderName.add(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_COL_DATA_TYPE));
            columnHeaderName.add(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_COL_PARAMETER_TYPE));
            columnHeaderName.add(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_COL_VALUE));
        }
        return isCursorType;
    }

    /**
     * Inits the.
     *
     * @param colCount the col count
     * @param rcols the rcols
     */
    public void init(int colCount, ResultSetColumn[] rcols) {
        this.columnCount = colCount;
        this.cols = rcols.clone();
    }

    @Override
    public int getColumnCount() {
        return this.columnCount;
    }

    @Override
    public String[] getColumnNames() {
        String[] names = new String[this.columnCount];
        for (int index = 0; index < this.columnCount; index++) {
            names[index] = this.cols[index].getColumnName();
        }

        return names;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return this.cols[columnIndex].getColumnName();
    }

    @Override
    public String getColumnDesc(int columnIndex) {
        return getColumnName(columnIndex) + " - " + getDataTypeName(columnIndex);
    }

    /**
     * Gets the data type name.
     *
     * @param columnIndex the column index
     * @return the data type name
     */
    public String getDataTypeName(int columnIndex) {
        return this.cols[columnIndex].getDataTypeName();
    }

    /**
     * Gets the data type names.
     *
     * @return the data type names
     */
    public String[] getDataTypeNames() {
        String[] typeNames = new String[this.columnCount];
        for (int index = 0; index < this.columnCount; index++) {
            typeNames[index] = this.cols[index].getDataTypeName();
        }

        return typeNames;
    }

    @Override
    public int getColumnIndex(String columnLabel) {
        // No idea, who and when this code will be used.
        for (int index = 0; index < this.columnCount; index++) {
            if (columnLabel.equals(this.cols[index].getColumnName())) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public Comparator<Object> getComparator(int columnIndex) {
        return new ColumnValueSqlTypeComparator<Object>(this.cols[columnIndex].getDataType());
    }

    @Override
    public int getColumnDatatype(int columnIndex) {
        return this.cols[columnIndex].getDataType();
    }

    /**
     * Gets the column data type name.
     *
     * @param columnIndex the column index
     * @return the column data type name
     */
    public String getColumnDataTypeName(int columnIndex) {
        return getDataTypeName(columnIndex);
    }

    /**
     * Gets the precision.
     *
     * @param columnIndex the column index
     * @return the precision
     */
    public int getPrecision(int columnIndex) {
        return this.cols[columnIndex].getPrecision();
    }

    /**
     * Gets the scale.
     *
     * @param columnIndex the column index
     * @return the scale
     */
    public int getScale(int columnIndex) {
        return this.cols[columnIndex].getScale();
    }

    @Override
    public int getMaxLength(int columnIndex) {
        return this.cols[columnIndex].getMaxLength();
    }

    @Override
    public String getColumnComment(int columnIndex) {
        return (0 <= columnIndex && columnIndex < this.cols.length) ? this.cols[columnIndex].getComment() : "";
    }

    /**
     * Adds the default values.
     *
     * @param defaultValueMap the default value map
     */
    public void addDefaultValues(Map<String, String> defaultValueMap) {
        String colName = null;
        for (int i = 0; i < this.cols.length; i++) {
            colName = this.cols[i].getColumnName();
            String defaultVal = defaultValueMap.get(colName);
            this.cols[i].setDefaultValue(defaultVal);
        }
    }

    @Override
    public String getDefaultValue(int columnIndex) {
        return this.cols[columnIndex].getDefaultValue();
    }
}
