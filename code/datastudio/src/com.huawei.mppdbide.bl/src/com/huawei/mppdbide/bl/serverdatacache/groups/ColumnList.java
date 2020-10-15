/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class ColumnList.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ColumnList extends OLAPObjectList<ColumnMetaData> {

    @Override
    public void addItem(ColumnMetaData item) {

        super.addItem(item);
    }

    @Override
    public void addItemAtIndex(ColumnMetaData item, int index) {

        super.addItemAtIndex(item, index);
    }

    /**
     * Instantiates a new column list.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public ColumnList(OBJECTTYPE type, Object parentObject) {
        super(type, parentObject);
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public TableMetaData getParent() {
        return (TableMetaData) super.getParent();
    }

}
