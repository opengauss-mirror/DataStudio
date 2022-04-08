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

package org.opengauss.mppdbide.bl.serverdatacache.groups;

import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.SystemNamespace;

/**
 * 
 * Title: class
 * 
 * Description: The Class SystemNamespaceObjectGroup.
 * 
 */

public class SystemNamespaceObjectGroup extends OLAPObjectGroup<SystemNamespace> {

    /**
     * Instantiates a new system namespace object group.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public SystemNamespaceObjectGroup(OBJECTTYPE type, Database parentObject) {
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
        int sysNsSize = 0;
        List<SystemNamespace> sysNamespaceList = getDatabase().getAllSystemNameSpaces();
        for (Namespace ns : sysNamespaceList) {
            if (ns.isLoaded()) {
                sysNsSize += ns.getChildrenSize();
            }
        }
        return getName() + " (" + sysNsSize + ") ";
    }

}
