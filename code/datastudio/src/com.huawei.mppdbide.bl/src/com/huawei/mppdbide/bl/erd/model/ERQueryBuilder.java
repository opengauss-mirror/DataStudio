/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.erd.model;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: ERQueryBuilder
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author f00512995
 * @version [DataStudio 6.5.1, 17-Oct-2019]
 * @since 06-Nov-2019
 */
public class ERQueryBuilder {
    /**
     * Gets the olap column constraints info query.
     *
     * @param type the type
     * @return the olap column constraints info query
     */
    public static String getOLAPTableConstraintDetailsQuery(ServerObject serverObject) {
        return "SELECT c.contype as constrainttype, c.conkey as columnlist"
                + " FROM pg_constraint c where c.conrelid = " + serverObject.getOid() + ";";
    }

    /**
     * Gets the olap table comments info query.
     *
     * @param type the type
     * @return the olap table comments info query
     */
    public static String getOLAPColumnCommentsQuery(ServerObject serverObject) {
        return "SELECT a.attrelid,a.attname ,d.objsubid, d.description FROM pg_description d "
                + "left join pg_attribute a on (d.objoid = a.attrelid and a.attnum = d.objsubid)" + " where d.objoid = "
                + serverObject.getOid() + ';';
    }

    /**
     * Gets the olap column comments info query.
     *
     * @param type the type
     * @return the olap column constraints info query
     */
    public static String getOLAPTableCommentsQuery(ServerObject serverObject) {
        return "SELECT d.description FROM pg_description d " + "where d.objoid = " + serverObject.getOid()
                + " and d.objsubid = 0;";
    }

}
