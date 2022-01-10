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
