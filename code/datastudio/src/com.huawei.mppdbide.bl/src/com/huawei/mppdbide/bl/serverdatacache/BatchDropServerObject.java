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

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.bl.IServerObjectBatchOperations;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropServerObject.
 * 
 */

public abstract class BatchDropServerObject extends ServerObject implements IServerObjectBatchOperations {

    /**
     * Instantiates a new batch drop server object.
     *
     * @param oid the oid
     * @param name the name
     * @param type the type
     * @param privilegeFlag the privilege flag
     */
    public BatchDropServerObject(long oid, String name, OBJECTTYPE type, boolean privilegeFlag) {
        super(oid, name, type, privilegeFlag);
    }

    /**
     * Instantiates a new batch drop server object.
     *
     * @param type the type
     */
    public BatchDropServerObject(OBJECTTYPE type) {
        super(type);
    }

    /**
     * get Drop Query.
     *
     * @param isCascade the is cascade
     * @return the drop query
     */
    @Override
    public abstract String getDropQuery(boolean isCascade);

    @Override
    public String getObjectFullName() {

        return getDisplayName();
    }

    @Override
    public String getObjectTypeName() {

        return getTypeLabel();
    }

    @Override
    public boolean isDropAllowed() {
        return true;
    }

    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {

        return false;
    }

}
