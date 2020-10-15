/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;

/**
 * 
 * Title: class
 * 
 * Description: The Class PartitionList.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class PartitionList extends OLAPObjectList<PartitionMetaData> {

    /**
     * Instantiates a new partition list.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public PartitionList(OBJECTTYPE type, Object parentObject) {
        super(type, parentObject);
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public PartitionTable getParent() {
        return (PartitionTable) super.getParent();
    }

}
