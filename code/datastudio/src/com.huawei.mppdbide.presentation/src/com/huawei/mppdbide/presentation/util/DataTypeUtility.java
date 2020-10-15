/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * Title: class
 * 
 * Description: The Class DataTypeUtility.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public abstract class DataTypeUtility {

    /**
     * Convert to date obj.
     *
     * @param value the value
     * @param pattern the pattern
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date convertToDateObj(Object value, String pattern) throws ParseException {
        SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat(pattern);
        Date dateValue = datetimeFormatter1.parse((String) value);
        return dateValue;
    }

    /**
     * Convert to time obj.
     *
     * @param value the value
     * @param pattern the pattern
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date convertToTimeObj(Object value, String pattern) throws ParseException {
        SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat(pattern);
        Date dateValue = datetimeFormatter1.parse((String) value);
        return dateValue;
    }

    /**
     * Convert to display datatype.
     *
     * @param datatypeName the datatype name
     * @return the string
     */
    public static String convertToDisplayDatatype(String datatypeName) {
        switch (datatypeName) {
            case "bpchar": {
                return "char";
            }
            case "bool": {
                return "boolean";
            }
            case "float": {
                return "binary double";
            }
            case "int2": {
                return "smallint";
            }
            case "int4": {
                return "integer";
            }
            case "int8": {
                return "bigint";
            }
            case "float8": {
                return "double precision";
            }
            case "float4": {
                return "real";
            }
            case "timetz": {
                return "time with time zone";
            }
            case "timestamptz": {
                return "timestamp with time zone";
            }
            case "time": {
                return "time without time zone";
            }
            case "timestamp": {
                return "timestamp without time zone";
            }
            default: {
                return datatypeName;
            }

        }
    }
}
