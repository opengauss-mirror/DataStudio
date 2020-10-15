/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
