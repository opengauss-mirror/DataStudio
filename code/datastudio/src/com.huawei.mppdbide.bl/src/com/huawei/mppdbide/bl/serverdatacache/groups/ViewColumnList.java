/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewColumnList.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ViewColumnList extends OLAPObjectList<ViewColumnMetaData> {

    /**
     * Instantiates a new view column list.
     *
     * @param parentObject the parent object
     */
    public ViewColumnList(Object parentObject) {
        super(OBJECTTYPE.VIEW_COLUMN_GROUP, parentObject);
    }
}
