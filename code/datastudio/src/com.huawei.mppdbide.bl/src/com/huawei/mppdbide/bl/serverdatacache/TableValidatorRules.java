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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableValidatorRules.
 * 
 */

public final class TableValidatorRules {

    private TableMetaData selTable;

    // : List needs to be updated as and when new pseudo types are added.
    // To get the list of pseudo-types run the following query
    // select * from pg_type where typtype = 'p';
    private static ArrayList<String> pseudoTypes = new ArrayList<String>(20);

    static {
        pseudoTypes.add("any");
        pseudoTypes.add("anyelement");
        pseudoTypes.add("anyarray");
        pseudoTypes.add("anynonarray");
        pseudoTypes.add("anyenum");
        pseudoTypes.add("anyrange");
        pseudoTypes.add("cstring");
        pseudoTypes.add("internal");
        pseudoTypes.add("language_handler");
        pseudoTypes.add("fdw_handler");
        pseudoTypes.add("record");
        pseudoTypes.add("_record");
        pseudoTypes.add("trigger");
        pseudoTypes.add("void");
        pseudoTypes.add("opaque");
    }

    /**
     * Instantiates a new table validator rules.
     *
     * @param tableMetaData the table meta data
     */
    public TableValidatorRules(TableMetaData tableMetaData) {
        this.selTable = tableMetaData;
    }

    /**
     * Enable disable.
     *
     * @return true, if successful
     */
    public boolean enableDisable() {
        if (null != selTable.getOrientation()) {

            if (selTable.getOrientation() == TableOrientation.ROW) {
                return true;
            }

        }
        return false;
    }

    /**
     * Gets the data type list.
     *
     * @param db the db
     * @param column the column
     * @return the data type list
     */
    public ArrayList<TypeMetaData> getDataTypeList(Database db, boolean column) {
        ArrayList<TypeMetaData> types = null;

        types = db.getDefaultDatatype().getList();

        // remove pseudo types when listing data types for column
        if (column) {
            Iterator<TypeMetaData> objectIter = types.iterator();

            TypeMetaData typename = null;
            boolean hasMore = objectIter.hasNext();
            String name = null;

            while (hasMore) {
                typename = objectIter.next();
                name = typename.getName();

                if (pseudoTypes.contains(name)) {
                    objectIter.remove();
                }

                hasMore = objectIter.hasNext();
            }
        }

        return types;
    }

}
