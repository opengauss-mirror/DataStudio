/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserNamespaceObjectGroup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class UserNamespaceObjectGroup extends OLAPObjectGroup<UserNamespace> {

    /**
     * Instantiates a new user namespace object group.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public UserNamespaceObjectGroup(OBJECTTYPE type, Database parentObject) {
        super(type, parentObject);
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return (Database) getParent();
    }

    /**
     * Gets the object browser label.
     *
     * @return the object browser label
     */
    public String getObjectBrowserLabel() {
        int userNsSize = 0;
        List<UserNamespace> userNamespaceList = getDatabase().getAllUserNameSpaces();
        for (Namespace ns : userNamespaceList) {
            if (ns.isLoaded()) {
                userNsSize += ns.getChildrenSize();
            }
        }
        return getName() + " (" + userNsSize + ") ";
    }

}
