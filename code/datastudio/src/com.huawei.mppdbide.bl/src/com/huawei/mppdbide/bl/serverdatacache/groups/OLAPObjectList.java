/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @param <E> the element type
 * @since 17 May, 2019
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
