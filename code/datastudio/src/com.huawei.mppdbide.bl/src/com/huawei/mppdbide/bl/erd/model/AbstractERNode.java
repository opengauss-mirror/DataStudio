/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.erd.model;

/**
 * Title: AbstractERNode
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 17-Oct-2019]
 * @since 17-Oct-2019
 */

public abstract class AbstractERNode<OBJECT> {

    /**
     * The server object.
     */
    protected OBJECT serverObject;

    public AbstractERNode() {

    }

    public AbstractERNode(OBJECT serverObject) {
        this.serverObject = serverObject;
    }

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    public Object getServerObject() {
        return serverObject;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public abstract String getName();
}
