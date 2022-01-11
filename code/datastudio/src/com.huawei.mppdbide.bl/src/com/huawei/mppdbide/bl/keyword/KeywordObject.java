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
 
package com.huawei.mppdbide.bl.keyword;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: KeywordObject
 * 
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
