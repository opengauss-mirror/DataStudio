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

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;

/**
 * Title: ERAttribute
 * 
 */
public class ERAttribute extends AbstractERAttribute {

    public ERAttribute(ColumnMetaData serverObject, boolean inPrimaryKey, boolean inForeignKey) {
        super(serverObject, inPrimaryKey, false);
    }

    @Override
    public String getNullability() {
        return ((ColumnMetaData) serverObject).isNotNull() ? "NOT NULL" : "";
    }

    @Override
    public ColumnMetaData getServerObject() {
        return (ColumnMetaData) serverObject;
    }

    @Override
    public String getDataTypes() {
        return ((ColumnMetaData) serverObject).getDisplayDatatype();
    }
}
