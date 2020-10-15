/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.sql.Types;

import org.apache.commons.lang3.BooleanUtils;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConvertValueToInsertSqlFormat.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ConvertValueToInsertSqlFormat {

    /**
     * Convert value to SQL.
     *
     * @param dataType the data type
     * @param value the value
     * @param checkTypeName the check type name
     * @return the string
     */
    public String convertValueToSQL(int dataType, Object value, String checkTypeName) {
        String dataValue = null;
        String aimValue = String.valueOf(value);
        if (aimValue.indexOf("'") > -1) {
            aimValue = aimValue.replace("\'", "\'\'");
        }
        if (null == value || "".equals(aimValue)) {
            return null;
        }
        switch (dataType) {
            case Types.NULL: {
                dataValue = null;
                break;
            }
            case Types.BIGINT:
            case Types.TINYINT:
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.FLOAT:
            case Types.REAL: {
                dataValue = aimValue;
                break;
            }
            case Types.DOUBLE: {
                if ("money".equalsIgnoreCase(checkTypeName)) {
                    dataValue = "\'" + aimValue + "\'";
                } else {
                    dataValue = aimValue;
                }
                break;
            }
            case Types.BIT: {
                dataValue = convertBitValue(checkTypeName, aimValue);
                break;
            }
            default: {
                dataValue = "\'" + aimValue + "\'";
                break;
            }
        }

        return dataValue;

    }

    private String convertBitValue(String checkTypeName, String aimValue) {
        String dataValue;
        if ("bool".equalsIgnoreCase(checkTypeName)) {
            dataValue = getBoolValue(aimValue);
        } else {
            dataValue = getBitValue(aimValue);
        }
        return dataValue;
    }

    private String getBitValue(String aimValue) {
        if (isBoolTypeInBit(aimValue)) {
            if ("true".equalsIgnoreCase(aimValue)) {
                return "\'" + "1" + "\'";
            } else {
                return "\'" + "0" + "\'";
            }
        }
        StringBuffer buff = new StringBuffer();
        buff.append("'");
        buff.append(aimValue);
        buff.append("'");
        return buff.toString();
    }

    private boolean isBoolTypeInBit(String aimValue) {
        if ("true".equalsIgnoreCase(aimValue) || "false".equalsIgnoreCase(aimValue)) {
            return true;
        }
        return false;
    }

    private String getBoolValue(String aimValue) {
        String dataValue;
        Boolean boolValue = BooleanUtils.toBooleanObject(aimValue);
        if (boolValue != null) {
            dataValue = boolValue.toString();
        } else {
            dataValue = aimValue;
        }
        return dataValue;
    }
}
