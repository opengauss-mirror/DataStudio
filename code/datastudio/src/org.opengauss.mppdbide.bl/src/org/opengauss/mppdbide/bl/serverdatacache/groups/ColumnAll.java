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

package org.opengauss.mppdbide.bl.serverdatacache.groups;

import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;

/**
 *
 * Title: ColumnAll
 *
 * Description: Get all the current columns from the table and store them
 *
 */
public class ColumnAll extends ServerObject {
    private int id;
    private String name;

    /**
     * Instantiates a new ColumnsAll object.
     *
     * @param oid the oid
     * @param name the name
     * @param type the type
     * @param isPrivilegeFlag is the privilege flag
     */
    public ColumnAll(long oid, String name, OBJECTTYPE type, boolean isPrivilegeFlag) {
        super(oid, name, type, false);
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}