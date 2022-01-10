/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
