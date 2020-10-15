/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DebugObjectGroup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DebugObjectGroup extends OLAPObjectGroup<DebugObjects> {

    /**
     * Instantiates a new debug object group.
     *
     * @param type the type
     * @param namespace the namespace
     */
    public DebugObjectGroup(OBJECTTYPE type, Namespace namespace) {
        super(type, namespace);
    }

    @Override
    public Namespace getNamespace() {
        return (Namespace) getParent();
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return getNamespace().getDatabase();
    }
}
