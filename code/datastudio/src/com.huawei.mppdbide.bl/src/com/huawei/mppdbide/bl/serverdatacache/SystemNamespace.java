/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class SystemNamespace.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class SystemNamespace extends Namespace {

    /**
     * Instantiates a new system namespace.
     *
     * @param oid the oid
     * @param name the name
     * @param parentDb the parent db
     */
    public SystemNamespace(long oid, String name, Database parentDb) {
        super(oid, name, parentDb);
    }

}
