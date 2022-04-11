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

import java.util.SortedMap;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.IViewObjectGroups;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewUtils;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewObjectGroup.
 * 
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
