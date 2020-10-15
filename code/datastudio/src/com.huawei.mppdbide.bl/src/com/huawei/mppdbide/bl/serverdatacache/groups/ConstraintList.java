/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConstraintList.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ConstraintList extends OLAPObjectList<ConstraintMetaData> {

    /**
     * Instantiates a new constraint list.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public ConstraintList(OBJECTTYPE type, Object parentObject) {
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
