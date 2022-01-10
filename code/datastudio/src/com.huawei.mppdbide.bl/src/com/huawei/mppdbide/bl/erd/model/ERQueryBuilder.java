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

package com.huawei.mppdbide.bl.erd.model;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: ERQueryBuilder
 * 
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
