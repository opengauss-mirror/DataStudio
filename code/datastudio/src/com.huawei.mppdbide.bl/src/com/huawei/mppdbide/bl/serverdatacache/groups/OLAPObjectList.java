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

import com.huawei.mppdbide.bl.serverdatacache.GaussOLAPDBMSObject;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * 
 * Title: class
 * 
 * Description: The Class OLAPObjectList.
 * 
 * @param <E> the element type
 */

public class OLAPObjectList<E extends ServerObject> extends ObjectList<E> implements GaussOLAPDBMSObject {

    /**
     * Instantiates a new OLAP object list.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public OLAPObjectList(OBJECTTYPE type, Object parentObject) {
        super(type, parentObject);
    }

}
