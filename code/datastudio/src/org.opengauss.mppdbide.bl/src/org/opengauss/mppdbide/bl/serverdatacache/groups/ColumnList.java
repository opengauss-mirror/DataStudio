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

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class ColumnList.
 * 
 */

public class ColumnList extends OLAPObjectList<ColumnMetaData> {

    @Override
    public void addItem(ColumnMetaData item) {

        super.addItem(item);
    }

    @Override
    public void addItemAtIndex(ColumnMetaData item, int index) {

        super.addItemAtIndex(item, index);
    }

    /**
     * Instantiates a new column list.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public ColumnList(OBJECTTYPE type, Object parentObject) {
        super(type, parentObject);
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public TableMetaData getParent() {
        return (TableMetaData) super.getParent();
    }

}
