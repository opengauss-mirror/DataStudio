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

import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ViewColumnMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewColumnList.
 * 
 */

public class ViewColumnList extends OLAPObjectList<ViewColumnMetaData> {

    /**
     * Instantiates a new view column list.
     *
     * @param parentObject the parent object
     */
    public ViewColumnList(Object parentObject) {
        super(OBJECTTYPE.VIEW_COLUMN_GROUP, parentObject);
    }
}
