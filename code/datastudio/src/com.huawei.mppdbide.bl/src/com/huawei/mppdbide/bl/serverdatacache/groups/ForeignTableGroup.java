/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ForeignTableGroup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ForeignTableGroup extends TableObjectGroup {

    /**
     * Instantiates a new foreign table group.
     *
     * @param type the type
     * @param nm the nm
     */
    public ForeignTableGroup(OBJECTTYPE type, Namespace nm) {
        super(type, nm);
    }

    /**
     * Gets the display label.
     *
     * @return the display label
     */
    public String getDisplayLabel() {
        return super.getDisplayLabel();
    }
}
