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
 * @since 3.0.0
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
