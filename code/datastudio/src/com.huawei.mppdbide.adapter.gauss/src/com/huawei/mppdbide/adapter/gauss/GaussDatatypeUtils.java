/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.gauss;

import java.util.HashMap;

/**
 * 
 * Title: class
 * 
 * Description: The Class GaussDatatypeUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GaussDatatypeUtils {

    private static HashMap<Integer, Datatype> datatypeMap = new HashMap<Integer, Datatype>(50);

    static {
        addDataTypesFirstList();
        addDataTypeSecondList();
    }

    private static void addDataTypeSecondList() {
        addDatatype(1002, "char[]", true);
        addDatatype(1003, "name[]", true);
        addDatatype(1005, "smallint[]", true);
        addDatatype(1006, "int2vector[]", false);
        addDatatype(1007, "integer[]", true);
        addDatatype(1009, "text[]", true);
        addDatatype(1010, "tid[]", false);
        addDatatype(1011, "xid[]", false);
        addDatatype(1012, "cid[]", false);
        addDatatype(1013, "oidvector[]", false);
        addDatatype(1014, "char[]", true);
        addDatatype(1015, "varchar[]", true);
        addDatatype(1016, "bigint[]", true);
        addDatatype(1028, "oid[]", true);
        addDatatype(1042, "char", true);
        addDatatype(1043, "varchar", true);
        addDatatype(1082, "date", true);
        addDatatype(1083, "time", true);
        addDatatype(1114, "timestamp", true);
        addDatatype(1115, "timestamp[]", true);
        addDatatype(1183, "time[]", true);
        addDatatype(1184, "timestamp with time zone", true);
        addDatatype(1185, "timestamp with time zone[]", true);
        addDatatype(1186, "interval", true);
        addDatatype(1187, "interval[]", true);
        addDatatype(1231, "numeric[]", true);
        addDatatype(1266, "time with time zone", true);
        addDatatype(1270, "time with time zone[]", true);
        addDatatype(1560, "bit", false);
        addDatatype(1561, "bit[]", false);
        addDatatype(1700, "numeric", true);
        addDatatype(1790, "refcursor", true);
        addDatatype(2249, "record", true);
        addDatatype(2278, "void", true);
    }

    private static void addDataTypesFirstList() {
        addDatatype(16, "bool", true);
        addDatatype(17, "bytea", true);
        addDatatype(18, "char", true);
        addDatatype(19, "name", true);
        addDatatype(20, "bigint", true);
        addDatatype(21, "smallint", true);
        addDatatype(22, "int2vector", false);
        addDatatype(23, "integer", true);
        addDatatype(24, "regproc", false);
        addDatatype(25, "text", true);
        addDatatype(26, "oid", true);
        addDatatype(27, "tid", false);
        addDatatype(28, "xid", false);
        addDatatype(29, "cid", false);
        addDatatype(30, "oidvector", false);
        addDatatype(114, "json", false);
        addDatatype(119, "json[]", false);
        addDatatype(142, "xml", false);
        addDatatype(143, "xml[]", false);
        addDatatype(210, "smgr", false);
        addDatatype(600, "point", false);
        addDatatype(601, "lseg", false);
        addDatatype(602, "path", false);
        addDatatype(603, "box", false);
        addDatatype(604, "polygon", false);
        addDatatype(628, "line", false);
        addDatatype(629, "line[]", false);
        addDatatype(650, "cidr", false);
        addDatatype(700, "real", true);
        addDatatype(701, "double precision", true);
        addDatatype(718, "circle", false);
        addDatatype(719, "circle[]", false);
        addDatatype(790, "money", false);
        addDatatype(829, "macaddr", false);
        addDatatype(869, "inet", false);
        addDatatype(1000, "bool[]", false);
        addDatatype(1001, "bytea[]", true);
    }

    /**
     * Adds the datatype.
     *
     * @param oid the oid
     * @param name the name
     * @param isSupported the is supported
     */
    public static void addDatatype(int oid, String name, boolean isSupported) {
        Datatype type = new Datatype(oid, name, isSupported);

        datatypeMap.put(oid, type);
    }

    /**
     * Convert to client type.
     *
     * @param typId the typ id
     * @return the string
     */
    public static String convertToClientType(int typId) {
        Datatype datatype = datatypeMap.get(typId);
        if (null == datatype) {
            return null;
        }

        return datatype.getTypename();
    }

    /**
     * Checks if is supported.
     *
     * @param typId the typ id
     * @return true, if is supported
     */
    public static boolean isSupported(int typId) {
        Datatype datatype = datatypeMap.get(typId);

        if (null == datatype) {
            return false;
        }

        return datatype.isSupported();
    }

    /**
     * Gets the datatype hashmap.
     *
     * @return HashMap<Integer, Datatype> the datatype map
     */
    public static HashMap<Integer, Datatype> getDataTypeHashMap() {
        return datatypeMap;
    }
}
