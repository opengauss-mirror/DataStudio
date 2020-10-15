/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.keyword;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: KeywordObject
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since 11-Oct-2019
 */

public class KeywordObject extends ServerObject {

    /**
     * Instantiates a new keyword object.
     *
     * @param oid the oid
     * @param name the name
     * @param type the type
     * @param privilegeFlag the privilege flag
     */
    public KeywordObject(long oid, String name, OBJECTTYPE type, boolean privilegeFlag) {
        super(oid, name, type, privilegeFlag);
    }

    /**
     * Gets the search name.
     *
     * @return the search name
     */
    @Override
    public String getSearchName() {
        return getName() + " - " + getTypeLabel();
    }

    /**
     * Gets the qualified object name.
     *
     * @return the qualified object name
     */
    @Override
    public String getQualifiedObjectName() {
        return getName();
    }

}
