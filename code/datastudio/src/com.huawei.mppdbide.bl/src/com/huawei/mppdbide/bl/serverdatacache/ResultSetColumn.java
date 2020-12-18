/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.adapter.gauss.StmtExecutor;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ResultSetColumn.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ResultSetColumn {

    private int columnIndex;
    private String columnName;
    private String datatypeName;
    private int dataType;
    private int precision;
    private int scale;
    private int maxLength;
    private String comment;
    private String defaultValue;

    /**
     * Instantiates a new result set column.
     *
     * @param columnIndex the column index
     */
    public ResultSetColumn(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * Gets the column index.
     *
     * @return the column index
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Sets the column index.
     *
     * @param columnIndex the new column index
     */
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * Gets the datatype name.
     *
     * @return the datatype name
     */
    public String getDataTypeName() {
        return datatypeName;
    }

    /**
     * Sets the datatype name.
     *
     * @param datatypeName the new datatype name
     */
    public void setDatatypeName(String datatypeName) {
        this.datatypeName = datatypeName;
    }

    /**
     * Gets the column name.
     *
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the column name.
     *
     * @param columnName the new column name
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets the data type.
     *
     * @return the data type
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType the new data type
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * Gets the precision.
     *
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Sets the precision.
     *
     * @param precession the new precision
     */
    public void setPrecision(int precession) {
        this.precision = precession;
    }

    /**
     * Gets the scale.
     *
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * Sets the scale.
     *
     * @param scale the new scale
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * Gets the max length.
     *
     * @return the max length
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the max length.
     *
     * @param maxLength the new max length
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Gets the comment.
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment.
     *
     * @param comment the new comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the default value.
     *
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Collect column data.
     *
     * @param resultStmt the result stmt
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void collectColumnData(StmtExecutor resultStmt, Map<String, String> columnComment)
            throws DatabaseOperationException, DatabaseCriticalException {
        int index = getColumnIndex();
        setColumnName(resultStmt.getColumnName(index));
        setDataType(resultStmt.getColumnDataTypeStmt(index));
        setDatatypeName(resultStmt.getColumnTypeName(index));
        setPrecision(resultStmt.getPrecision(index));
        setScale(resultStmt.getScale(index));
        setMaxLength(resultStmt.getMaxLength(index));
    }

    /**
     * sets the column header name
     * 
     * @param rs the result set
     * @param columnHeaderName the column header name
     * @param index the index
     * @param isCursorType the cursor type
     * @param isStatementNull the statement null
     * @throws DatabaseCriticalException
     * @throws DatabaseOperationException
     */
    public void setColumnHeaderName(ResultSet rs, String columnHeaderName, int index, boolean isCursorType,
            boolean isStatementNull) throws DatabaseOperationException, DatabaseCriticalException {
        if (index <= 3) {
            setDataType(Types.VARCHAR);
        } else {
            if (isStatementNull) {
                setDataType(Types.OTHER);
            } else {
                setValueColumnDataType(rs);
            }
        }
        if (isCursorType) {
            setDatatypeName(MPPDBIDEConstants.VAR_CHAR);
        }
        setColumnName(columnHeaderName);
    }

    /**
     * sets the data type of value column
     * 
     * @param rs the result set
     * @throws DatabaseOperationException
     * @throws DatabaseCriticalException
     */
    private void setValueColumnDataType(ResultSet rs) throws DatabaseOperationException, DatabaseCriticalException {
        if (null == rs) {
            return;
        }
        try {
            ResultSetMetaData md = rs.getMetaData();
            if (md != null) {
                setDataType(md.getColumnType(md.getColumnCount()));
            }
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        }
    }

    /**
     * Collect column data.
     *
     * @param stmt the stmt
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void collectColumnData(Statement stmt, Map<String, String> columnComment)
            throws DatabaseOperationException, DatabaseCriticalException {
        try {
            ResultSetMetaData md = stmt.getResultSet().getMetaData();

            if (md != null) {
                int index = getColumnIndex();
                setColumnName(md.getColumnLabel(index));
                setDataType(md.getColumnType(index));
                setDatatypeName(md.getColumnTypeName(index));
                setPrecision(md.getPrecision(index));
                setScale(md.getScale(index));
                setMaxLength(md.getColumnDisplaySize(index));
                setComment(columnComment.get(md.getSchemaName(index) + md.getTableName(index)
                        + MPPDBIDEConstants.COLUMN_KEY_SIGN + md.getColumnName(index)));
            }
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        }
    }

    /**
     * sets popup column header name
     * 
     * @param cursorPopUpdataType the cursor popup data type object
     * @param columnHeaderName the column header name
     * @param i the index
     */
    public void setPopUpColumnHeaderName(String columnHeaderName, int index) {
        setColumnName(columnHeaderName);
        setDatatypeName(MPPDBIDEConstants.VAR_CHAR);
    }

}
