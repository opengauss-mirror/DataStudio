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

package org.opengauss.mppdbide.utils;

import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ResultSetDatatypeMapping.
 *
 * @since 3.0.0
 */
public final class ResultSetDatatypeMapping {
    private static boolean isIncludeEncoding;

    /**
     * The Constant INTERVAL_DAY_TO_SECOND.
     */
    public static final int INTERVAL_DAY_TO_SECOND = -104;

    /**
     * The Constant INTERVAL_YEAR_TO_MONTH.
     */
    public static final int INTERVAL_YEAR_TO_MONTH = -103;

    /**
     * The Constant ZERO.
     */
    public static final int ZERO = 0;

    /**
     * The Constant TWO.
     */
    public static final int TWO = 2;

    /**
     * The Constant THREE.
     */
    public static final int THREE = 3;

    /**
     * The Constant SIX.
     */
    public static final int SIX = 6;

    /**
     * Instantiates a new result set datatype mapping.
     */

    private static List<List<Object>> cursorResultVisitedValues;

    private ResultSetDatatypeMapping() {
    }

    /**
     * sets the cursor resultset values
     * 
     * @param cursorResultValues the cursorResultValues
     */
    private static void setCursorResultValues(List<List<Object>> cursorResultValues) {
        cursorResultVisitedValues = cursorResultValues;
    }

    /**
     * gets the cursor resultset values
     * 
     * @return cursorResultVisitedValues the cursorResultVisitedValues
     */
    public static List<List<Object>> getCursorResultValues() {
        return cursorResultVisitedValues;
    }

    private static boolean isReturnTypeVoid(ResultSet rs) throws SQLException {
        if (MPPDBIDEConstants.VOID.equals(rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount()))) {
            return true;
        }
        return false;
    }
    
    /**
     * return true, if is ReturnType Cursor
     * 
     * @param rs the rs
     * @return return true, if is ReturnType Cursor
     * @throws SQLException the SQLException
     */
    public static boolean isReturnTypeCursor(ResultSet rs) throws SQLException {
        if (MPPDBIDEConstants.REF_CURSOR
                .equals(rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount()))) {
            return true;
        }
        return false;
    }
    
    /**
     * return true, if is ReturnType Record
     * 
     * @param rs the rs
     * @return return true, if is ReturnType Record
     * @throws SQLException the SQLException
     */
    public static boolean isReturnTypeRecord(ResultSet rs) throws SQLException {
        if (MPPDBIDEConstants.RECORD.equals(rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount()))) {
            return true;
        }
        return false;
    }

    /**
     * gets the Read Column Value Object
     * 
     * @param rs the rs
     * @param columnIndex the columnIndex
     * @return the object value
     * @throws NumberFormatException the NumberFormatException
     * @throws SQLException the SQLException
     * @throws DatabaseOperationException the DatabaseOperationException
     */
    public static Object getReadColumnValueObject(ResultSet rs, int columnIndex)
            throws NumberFormatException, SQLException, DatabaseOperationException {
        if (rs.getMetaData() != null) {
            switch (rs.getMetaData().getColumnType(columnIndex)) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.FLOAT:
                case Types.REAL:
                case Types.DOUBLE: {
                    return handleDoubleDataTypeValue(rs, columnIndex);
                }
                case Types.BOOLEAN:
                case Types.BIT: {
                    return getBitValue(rs, columnIndex);
                }
                case Types.NUMERIC: {
                    return rs.getBigDecimal(columnIndex);
                }
                case Types.DATE: {
                    return rs.getDate(columnIndex);
                }
                case Types.TIME:
                case Types.TIME_WITH_TIMEZONE:
                case Types.TIMESTAMP:
                case Types.TIMESTAMP_WITH_TIMEZONE: {
                    String typeName = rs.getMetaData().getColumnTypeName(columnIndex);
                    if ("timetz".equals(typeName) || "timestamptz".equals(typeName)) {
                        return rs.getString(columnIndex);
                    } else {
                        return rs.getTimestamp(columnIndex);
                    }
                }
                case INTERVAL_DAY_TO_SECOND:
                case INTERVAL_YEAR_TO_MONTH: {
                    return convertToInterval(rs, columnIndex);
                }
                case Types.CLOB: {
                    return getCLOBValue(rs, columnIndex);
                }
                case Types.BLOB: {
                    return getBLOBValue(rs, columnIndex);
                }
                case Types.OTHER: {
                    return handleOtherDataTypeValue(rs, columnIndex); 
                }
                case Types.BINARY: {
                    return getByteAValue(rs, columnIndex);
                }
                default: {
                    return resultSetDataTypeMapDefaultAction(rs, columnIndex);
                }
            }
        }
        return "";
    }

    private static Object handleDoubleDataTypeValue(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getObject(columnIndex);
    }

    private static Object handleOtherDataTypeValue(ResultSet rs, int columnIndex) throws SQLException {
        if (isReturnTypeCursor(rs) || isReturnTypeRecord(rs)) {
            return convertResultSetToObject(rs, columnIndex, false, true);
        }
        if (isReturnTypeVoid(rs)) {
            return MPPDBIDEConstants.RETURN_VOID;
        }
        return resultSetDataTypeMapDefaultAction(rs, columnIndex);
    }

    /**
     * converts resultSet values into object
     * 
     * @param rs the rs
     * @param columnIndex the columnIndex
     * @param isCallableStmt the isCallableStmt
     * @param isResultSetVisited the isResultSetVisited
     * @return the list of cursor objects
     */
    public static List<List<Object>> convertResultSetToObject(ResultSet rs, int columnIndex, boolean isCallableStmt,
            boolean isResultSetVisited) {
        List<List<Object>> cursorRowsValue = new ArrayList<List<Object>>();
        int visited = 0;
        try {
            ResultSet rsCursorType = null;
            if (isCallableStmt || isReturnTypeRecord(rs)) {
                rsCursorType = rs;
            } else {
                rsCursorType = (ResultSet) rs.getObject(columnIndex);
            }
            int columnCount = rsCursorType.getMetaData().getColumnCount();
            List<Object> colHeaderValue = new ArrayList<Object>();
            for (int col = 0; col < columnCount; col++) {
                colHeaderValue.add(rsCursorType.getMetaData().getColumnLabel(col + 1));
            }

            cursorRowsValue.add(colHeaderValue);
            if (isReturnTypeRecord(rs)) {
                visited = addCursorRowValueToList(cursorRowsValue, visited, rsCursorType, columnCount);
            }
            while (rsCursorType.next()) {
                visited = addCursorRowValueToList(cursorRowsValue, visited, rsCursorType, columnCount);
            }

            if (!isResultSetVisited) {
                cursorRowsValue = getCursorResultValues();
            }

        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error("Error occured while getting result set values", exe);
        }

        if (isResultSetVisited) {
            setCursorResultValues(cursorRowsValue);
        }

        return cursorRowsValue;

    }

    private static int addCursorRowValueToList(List<List<Object>> cursorRowsValue, int visited, ResultSet rsCursorType,
            int columnCount) throws SQLException {
        List<Object> colValue = new ArrayList<Object>();
        for (int col = 0; col < columnCount; col++) {
            colValue.add(rsCursorType.getObject(col + 1));
        }
        cursorRowsValue.add(colValue);
        visited++;
        return visited;
    }

    /**
     * the result SetDataType Map Default Action
     * 
     * @param rs the rs
     * @param columnIndex the columnIndex
     * @return the result set
     * @throws SQLException the SQLException
     */
    public static Object resultSetDataTypeMapDefaultAction(ResultSet rs, int columnIndex) throws SQLException {
        if (ResultSetDatatypeMapping.isIncludeEncoding) {
            return rs.getBytes(columnIndex);
        }
        return rs.getString(columnIndex);
    }

    private static Object getBLOBValue(ResultSet rs, int columnIndex) throws SQLException {
        switch (rs.getMetaData().getColumnTypeName(columnIndex)) {
            case MPPDBIDEConstants.BLOB: {
                try {
                    byte[] retBytes = null;
                    Blob blob = rs.getBlob(columnIndex);
                    if (null == blob) {
                        return null;
                    }
                    retBytes = blob.getBytes(1, (int) blob.length());
                    blob.free();
                    return retBytes;
                } catch (SQLException exp) {
                    MPPDBIDELoggerUtility.error("error occured when converting to BLOB", exp);
                    return resultSetDataTypeMapDefaultAction(rs, columnIndex);
                }
            }
            default: {
                return resultSetDataTypeMapDefaultAction(rs, columnIndex);
            }
        }
    }

    private static Object getByteAValue(ResultSet rs, int columnIndex) throws SQLException {
        switch (rs.getMetaData().getColumnTypeName(columnIndex)) {
            case MPPDBIDEConstants.BYTEA: {
                try {
                    byte[] retBytes = rs.getBytes(columnIndex);
                    return retBytes;
                } catch (SQLException exp) {
                    MPPDBIDELoggerUtility.error("error while getting bytes", exp);
                    return resultSetDataTypeMapDefaultAction(rs, columnIndex);
                }
            }
            default: {
                return resultSetDataTypeMapDefaultAction(rs, columnIndex);
            }
        }
    }

    /**
     * Gets the CLOB value.
     *
     * @param rs the rs
     * @param columnIndex the column index
     * @return the CLOB value
     * @throws DatabaseOperationException the database operation exception
     */
    private static Object getCLOBValue(ResultSet rs, int columnIndex) throws DatabaseOperationException {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        Reader reader = null;
        MaxLineBufferedReader br = null;
        try {
            Clob clobValue = rs.getClob(columnIndex);
            if (null == clobValue) {
                return null;
            }
            reader = clobValue.getCharacterStream();
            br = new MaxLineBufferedReader(reader);
            int length = 0;
            String line = br.readMaxLenLine();
            while (null != line && length < 5000) {
                sb.append(line);
                length = sb.length();
                line = br.readMaxLenLine();
            }
        } catch (SQLException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.UNKNOW_CLOB_TYPE), exp);
            throw new DatabaseOperationException(IMessagesConstants.UNKNOW_CLOB_TYPE, exp);
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("Error while closing readers", exception);
            }

        }
        return sb.toString();
    }

    /**
     * Convert to interval.
     *
     * @param rs the rs
     * @param columnIndex the column index
     * @return the string builder
     * @throws SQLException the SQL exception
     */
    private static String convertToInterval(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = null;
        obj = rs.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        String objToString = obj.toString();
        String[] interval = objToString.split(" ", TWO);
        if (interval.length < 2) {
            return getIntervalYrToMonthValue(interval[0]);
        }
        StringBuilder intervalDS = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        try {
            if (Integer.parseInt(interval[0]) >= 0) {
                if ("-0".equals(interval[0])) {
                    intervalDS.append("-");
                } else {
                    intervalDS.append("+");
                }
            }
            intervalDS.append(String.format(Locale.ENGLISH, "%07d", Integer.parseInt(interval[0])) + " ");
            String[] time = interval[1].split("\\.", TWO);
            if (time.length < 2) {
                return null;
            }
            String[] timeUnit = time[0].split(":", THREE);
            for (int i = 0; i < timeUnit.length; i++) {
                intervalDS.append(String.format(Locale.ENGLISH, "%02d", Integer.parseInt(timeUnit[i])));
                if (i != timeUnit.length - 1) {
                    intervalDS.append(":");
                }
            }
            intervalDS.append(".");
            if (Integer.parseInt(time[1]) == 0) {
                intervalDS.append(String.format(Locale.ENGLISH, "%06d", ZERO));
            } else {
                intervalDS.append(time[1].substring(ZERO, SIX));
            }

        } catch (NumberFormatException exception) {
            MPPDBIDELoggerUtility.error("Error occured while converting to Time Intervals", exception);
            return null;
        }
        return intervalDS.toString();
    }

    /**
     * returns Interval Year to Month value.
     *
     * @param interval the interval
     * @return String
     */
    private static String getIntervalYrToMonthValue(String interval) {
        StringBuilder intervalDS = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if ('-' == interval.charAt(0)) {
            interval = interval.substring(1);
            intervalDS.append("-");
        }
        String[] period = interval.split("-");
        if (period.length < 2) {
            return null;
        }

        if (Integer.parseInt(period[0]) >= 0 && intervalDS.length() < 1) {
            intervalDS.append("+");
        }
        for (int i = 0; i < period.length; i++) {
            if (period[i].length() <= 2) {
                intervalDS.append(String.format(Locale.ENGLISH, "%02d", Integer.parseInt(period[i])));
            } else {
                intervalDS.append(String.format(Locale.ENGLISH, "%d", Integer.parseInt(period[i])));
            }
            if (i != period.length - 1) {
                intervalDS.append("-");
            }
        }
        return intervalDS.toString();
    }

    /**
     * Gets the bit value.
     *
     * @param resultSet the result set
     * @param colIndex the col index
     * @return the bit value
     * @throws SQLException the SQL exception
     */
    private static Object getBitValue(ResultSet resultSet, int colIndex) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        if (null != metaData && metaData.getPrecision(colIndex) > 1) {
            return resultSetDataTypeMapDefaultAction(resultSet, colIndex);
        }
        return resultSet.getObject(colIndex);
    }

    /**
     * Convert string to value.
     *
     * @param rs the rs
     * @param i the i
     * @return the string
     * @throws SQLException the SQL exception
     */
    public static String convertStringToValue(ResultSet rs, int i) throws SQLException {
        String value = rs.getString(i);
        if (null != value && rs.getMetaData() != null) {
            switch (rs.getMetaData().getColumnType(i)) {
                case Types.TINYINT:
                case Types.SMALLINT: {
                    return String.valueOf(Short.parseShort(value));
                }
                case Types.INTEGER: {
                    return String.valueOf(Integer.parseInt(value));
                }
                case Types.BIGINT: {
                    return String.valueOf(Long.parseLong(value));
                }
                case Types.FLOAT:
                case Types.REAL: {
                    return String.valueOf(Float.parseFloat(value));
                }
                case Types.DOUBLE: {
                    return String.valueOf(Double.parseDouble(value));
                }
                default: {
                    return value;
                }
            }
        } else {
            return value;
        }
    }

    /**
     * Sets the include encoding.
     *
     * @param isDSIncludeEncoding the new include encoding
     */
    public static void setIncludeEncoding(boolean isDSIncludeEncoding) {
        ResultSetDatatypeMapping.isIncludeEncoding = isDSIncludeEncoding;
    }

    /**
     * gets the data type of column
     * 
     * @param rs the rs
     * @return the column data type
     * @throws SQLException the SQLException
     */
    public static Object getColumnDataTypeName(ResultSet rs) throws SQLException {
        Object colDataTypeName = "";
        if (rs.getMetaData() != null) {
            colDataTypeName = rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount());
        }
        return colDataTypeName;
    }

    /**
     * gets the FuncProcColObjectExceptValue
     * 
     * @param rs the rs
     * @param columnIndex the columnIndex
     * @param isCursorTypeResult the isCursorTypeResult
     * @return the column value object
     * @throws NumberFormatException the NumberFormatException
     * @throws DatabaseOperationException the DatabaseOperationException
     */
    public static Object getFuncProcColObjectExceptValue(ResultSet rs, int columnIndex, boolean isCursorTypeResult) 
            throws NumberFormatException, DatabaseOperationException {
        try {
            if (rs.getMetaData() != null) {
                switch (columnIndex) {
                    case 1: {
                        if (isCursorTypeResult) {
                            return MPPDBIDEConstants.RETURN_RESULT_COL_VALUE;
                        }
                        return rs.getMetaData().getColumnName(columnIndex);
                    }
                    case 2: {
                        if (isCursorTypeResult) {
                            return MPPDBIDEConstants.CURSOR;
                        }
                        return getColumnDataTypeName(rs);
                    }
                    case 3: {
                        return MPPDBIDEConstants.OUT;
                    }
                    default: {
                        return "";
                    }
                }
            }
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error("Error occured while getting result set values", exe);
        }
        return "";
    }
}
