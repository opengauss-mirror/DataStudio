/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;


/** 
 * Title: ColumnUtil
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 25-Jun-2020]
 * @since 25-Jun-2020
 */
public class ColumnUtil {
    /**
     * Extract precision scale.
     *
     * @param rs the rs
     * @param col the col
     * @throws SQLException the SQL exception
     */
    public static void extractPrecisionScale(ResultSet rs, ColumnMetaData col) throws SQLException {
        String datatypeName = "";
        String datatypeNameFromSever = "";
        datatypeNameFromSever = col.getDataType().getName();
        int precision = rs.getInt("precision");
        if (precision < 0) {
            col.setLenOrPrecision(-1);
            return;
        }
        int len = rs.getInt("length");

        if (len > 0) {
            col.setLenOrPrecision(len);
            return;
        }

        if (datatypeNameFromSever.startsWith("_")) {
            datatypeName = datatypeNameFromSever.substring(1, datatypeNameFromSever.length());
        } else {
            datatypeName = datatypeNameFromSever;
        }

        if ("bpchar".equals(datatypeName) || "varchar".equals(datatypeName)) {
            precision -= 4;
            col.setLenOrPrecision(precision);
        } else if ("bit".equals(col.getDataType().getName())) {
            col.setLenOrPrecision(precision);
        } else {
            precision -= 4;
            col.setLenOrPrecision(precision >> 16);
            col.setScale(precision % (1 << 16));
        }
    }

    /**
     * Convert to column meta data.
     *
     * @param rs the rs
     * @param type the type
     * @param tableMetaData the table meta data
     * @param database the database
     * @return the column meta data
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static ColumnMetaData convertToColumnMetaData(ResultSet rs, TypeMetaData type, TableMetaData tableMetaData,
            Database database) throws DatabaseCriticalException, DatabaseOperationException {
        try {
            ColumnMetaData col = new ColumnMetaData(tableMetaData, rs.getLong("columnidx"), rs.getString("name"), type);

            extractPrecisionScale(rs, col);

            col.setArrayNDim(rs.getInt("dimentions"));
            col.setNotNull(rs.getBoolean("notnull"));
            col.setHasDefVal(rs.getBoolean("isdefaultvalueavailable"));
            col.setAttDefString(rs.getString("attDefStr"));
            col.setDefaultValue(rs.getString("default_value"));
            col.setLoaded(true);
            col.setDisplayDatatype(rs.getString("displayColumns"));
            return col;
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        }
    }
    
    /**
     * Checks if is column name valid.
     *
     * @param newTempColumn the new temp column
     * @return true, if is column name valid
     */
    public static boolean isColumnNameValid(ColumnMetaData newTempColumn) {
        return null == newTempColumn
                || (null == newTempColumn.getName() || newTempColumn.getName().trim().length() == 0);
    }

    /**
     * Checks if is data type valid.
     *
     * @param newTempColumn the new temp column
     * @return true, if is data type valid
     */
    public static boolean isDataTypeValid(ColumnMetaData newTempColumn) {
        return null != newTempColumn && null == newTempColumn.getDataType();
    }
}
