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

package org.opengauss.mppdbide.presentation.grid.resultset;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Comparator;

/**
 * 
 * Title: class
 * 
 * Description: The Class ColumnValueSqlTypeComparator.
 * 
 * @param <T> the generic type
 * @since 3.0.0
 */
public class ColumnValueSqlTypeComparator<T> implements Comparator<T> {
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";
    private static final String BIT_ONE = "1";
    private static final String BIT_ZERO = "0";
    private static final String NULL_VALUE = "";
    private int sqlType;

    /**
     * Instantiates a new column value sql type comparator.
     *
     * @param type the type
     */
    public ColumnValueSqlTypeComparator(int type) {
        this.sqlType = type;
    }

    @Override
    public int compare(T o1, T o2) {
        int result = 0;

        if (o1 == null) {
            result = (null != o2) ? 1 : 0;
        } else {
            result = (null == o2) ? -1 : compareValues(o1, o2);
        }

        return result;
    }

    private int compareValues(T val1, T val2) {
        switch (this.sqlType) {
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER: {
                return compareInteger(val1, val2);
            }
            case Types.BIGINT: {
                return compareLong(val1, val2);
            }
            case Types.FLOAT:
            case Types.REAL: {
                return compareFloat(val1, val2);
            }
            case Types.DOUBLE: {
                return compareDouble(val1, val2);
            }
            case Types.NUMERIC: {
                return compareBigDecimal(val1, val2);
            }
            case Types.BIT:
            case Types.BOOLEAN: {
                return compareBoolean(val1, val2);
            }
            case Types.DATE: {
                return compareDate(val1, val2);
            }
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE: {
                return compareTime(val1, val2);
            }
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE: {
                return compareTimestamps(val1, val2);
            }
            default: {
                return val1.toString().compareTo(val2.toString());
            }
        }
    }

    private int compareDate(Object val1, Object val2) {
        Date dVal1 = convertToDate(val1);
        Date dVal2 = convertToDate(val2);

        if (null == dVal1) {
            return 1;
        }
        if (null == dVal2) {
            return -1;
        }
        return dVal1.compareTo(dVal2);
    }

    private Date convertToDate(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof String) {
            return Date.valueOf((String) value);
        }

        return null;
    }

    private int compareBigDecimal(Object val1, Object val2) {
        BigDecimal dVal1 = convertToBigDecimal(val1);
        BigDecimal dVal2 = convertToBigDecimal(val2);

        if (null == dVal1) {
            return 1;
        } else if (null == dVal2) {
            return -1;
        }

        return dVal1.compareTo(dVal2);
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof String) {
            return new BigDecimal((String) value);
        }

        return null;
    }

    private int compareBoolean(Object val1, Object val2) {
        Boolean dVal1 = convertToBoolean(val1);
        Boolean dVal2 = convertToBoolean(val2);

        return dVal1.compareTo(dVal2);
    }

    private Boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return convertToBooleanInner((String) value);
        }

        return false;
    }

    private Boolean convertToBooleanInner(String value) {
        if (NULL_VALUE.equals(value)) {
            return false;
        }
        if (TRUE_VALUE.equalsIgnoreCase(value) || value.equals(BIT_ONE)) {
            return true;
        }
        if (FALSE_VALUE.equalsIgnoreCase(value) || value.equals(BIT_ZERO)) {
            return false;
        }
        throw new IllegalArgumentException();
    }

    private int compareDouble(Object val1, Object val2) {
        Double dVal1 = convertToDouble(val1);
        Double dVal2 = convertToDouble(val2);

        if (null == dVal1) {
            return 1;
        } else if (null == dVal2) {
            return -1;
        }

        return dVal1.compareTo(dVal2);
    }

    private Double convertToDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof String) {
            /*
             * To handle money data type vlaues, which can be of following
             * format $10.25, $-25.00, 56.89$, etc..
             */
            String strippedString = (String) value;
            strippedString = stripCurrencyChar(strippedString);
            return Double.valueOf(strippedString);
        }

        return null;
    }

    private String stripCurrencyChar(String strippedStringParam) {
        String strippedString = strippedStringParam;
        /* NOTE: This function manipulates the input parameter itself */
        boolean hasSign = strippedString.startsWith("+") || strippedString.startsWith("-");
        char signChar = 0;
        if (hasSign && strippedString.length() > 1) {
            signChar = strippedString.charAt(0);
            strippedString = strippedString.substring(1, strippedString.length() - 1);
        }
        char startChar = strippedString.charAt(0);
        char endChar = strippedString.charAt(strippedString.length() - 1);
        boolean isStripFirstChar = !Character.isDigit(startChar);
        boolean isStripLastChar = !Character.isDigit(endChar);
        if (isStripFirstChar && strippedString.length() > 1) {
            strippedString = strippedString.substring(1, strippedString.length() - 1);
        }

        if (isStripLastChar && strippedString.length() > 1) {
            strippedString = strippedString.substring(0, strippedString.length() - 2);
        }

        if (hasSign && strippedString.length() > 1) {
            strippedString = String.valueOf(signChar).concat(strippedString);
        }
        return strippedString;
    }

    private int compareFloat(Object val1, Object val2) {
        Float dVal1 = convertToFloat(val1);
        Float dVal2 = convertToFloat(val2);

        if (null == dVal1) {
            return 1;
        } else if (null == dVal2) {
            return -1;
        }

        return dVal1.compareTo(dVal2);
    }

    private Float convertToFloat(Object value) {
        if (value instanceof Float) {
            return (Float) value;
        } else if (value instanceof String) {
            return Float.valueOf((String) value);
        }

        return null;
    }

    private int compareLong(Object val1, Object val2) {
        Long dVal1 = convertToLong(val1);
        Long dVal2 = convertToLong(val2);

        if (null == dVal1) {
            return 1;
        } else if (null == dVal2) {
            return -1;
        }

        return dVal1.compareTo(dVal2);
    }

    private Long convertToLong(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            return Long.valueOf((String) value);
        }

        return null;
    }

    private int compareInteger(Object val1, Object val2) {
        Integer dVal1 = convertToInteger(val1);
        Integer dVal2 = convertToInteger(val2);

        if (null == dVal1) {
            return 1;
        } else if (null == dVal2) {
            return -1;
        }

        return dVal1.compareTo(dVal2);
    }

    private Integer convertToInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Short) {
            return ((Short) value).intValue();
        } else if (value instanceof String && !((String) value).isEmpty()) {
            return Integer.valueOf((String) value);
        }

        return null;
    }

    private int compareTime(T val1, T val2) {
        Time time1 = convertToTime(val1);
        Time time2 = convertToTime(val2);

        // Unsupported format. Assuming that this compare is for sorting
        // return 1 to move down the order
        if (null == time1) {
            return 1;
        } else if (null == time2) {
            return -1;
        }

        return time1.compareTo(time2);

    }

    private Time convertToTime(T value) {
        if (value instanceof Time) {
            return (Time) value;
        } else if (value instanceof java.util.Date) {
            return new Time(((java.util.Date) value).getTime());
        }

        return null;
    }

    private int compareTimestamps(T val1, T val2) {
        Timestamp time1 = convertToTimestamp(val1);
        Timestamp time2 = convertToTimestamp(val2);

        // Unsupported format. Assuming that this compare is for sorting
        // return 1 to move down the order
        if (null == time1) {
            return 1;
        } else if (null == time2) {
            return -1;
        }

        return time1.compareTo(time2);
    }

    private Timestamp convertToTimestamp(Object value) {
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        } else if (value instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) value).getTime());
        }

        return null;
    }

}
