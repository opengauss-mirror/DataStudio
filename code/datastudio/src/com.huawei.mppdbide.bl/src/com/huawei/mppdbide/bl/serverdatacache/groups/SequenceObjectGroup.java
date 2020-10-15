/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;

/**
 * 
 * Title: class
 * 
 * Description: The Class SequenceObjectGroup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class SequenceObjectGroup extends OLAPObjectGroup<SequenceMetadata> {

    /**
     * Instantiates a new sequence object group.
     *
     * @param type the type
     * @param nm the nm
     */
    public SequenceObjectGroup(OBJECTTYPE type, Namespace nm) {
        super(type, nm);

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
     * Gets the namespace.
     *
     * @return the namespace
     */
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
