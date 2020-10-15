/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexList.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class IndexList extends OLAPObjectList<IndexMetaData> {

    /**
     * Instantiates a new index list.
     *
     * @param type the type
     * @param table the table
     */
    public IndexList(OBJECTTYPE type, TableMetaData table) {
        super(type, table);
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
