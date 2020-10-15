/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableObjectGroup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class TableObjectGroup extends OLAPObjectGroup<TableMetaData> {

    /**
     * Instantiates a new table object group.
     *
     * @param type the type
     * @param nm the nm
     */
    public TableObjectGroup(OBJECTTYPE type, Namespace nm) {
        super(type, nm);
    }

    @Override
    public Namespace getNamespace() {
        return (Namespace) getParent();
    }

    /**
     * Gets the display label.
     *
     * @return the display label
     */
    public String getDisplayLabel() {
        return this.getName() + " (" + this.getSize() + ')';
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
