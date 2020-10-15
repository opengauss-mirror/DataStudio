/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.erd.model;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;

/**
 * Title: ERAttribute
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author f00512995
 * @version [DataStudio 6.5.1, 17-Oct-2019]
 * @since 26-Oct-2019
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
