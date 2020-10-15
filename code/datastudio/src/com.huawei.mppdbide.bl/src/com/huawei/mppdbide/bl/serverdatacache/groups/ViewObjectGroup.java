/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import java.util.SortedMap;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IViewObjectGroups;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewUtils;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewObjectGroup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ViewObjectGroup extends OLAPObjectGroup<ViewMetaData> implements IViewObjectGroups {

    /**
     * Instantiates a new view object group.
     *
     * @param parentObject the parent object
     */
    public ViewObjectGroup(Object parentObject) {
        super(OBJECTTYPE.VIEW_GROUP, parentObject);
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return ((Namespace) getParent()).getDatabase();
    }

    @Override
    public String getTemplateCode() {
        Object parent = getParent();
        if (parent instanceof Namespace) {
            return ViewUtils.getCreateViewTemplate((Namespace) parent);
        }
        return "";
    }

    @Override
    public boolean isDbConnected() {
        Object parent = getParent();
        if (parent instanceof Namespace) {
            Namespace ns = (Namespace) parent;
            return ns.getDatabase().isConnected();
        }
        return false;
    }
}
