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

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableObjectGroup.
 * 
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
