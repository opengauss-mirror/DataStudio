/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.erd.model;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: IERAttribute Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author f00512995
 * @version [DataStudio 6.5.1, 17-Oct-2019]
 * @since 26-Oct-2019
 */
public abstract class AbstractERAttribute extends AbstractERNode<ServerObject> {
    /**
     *  The in primary key. 
     */
    protected boolean inPrimaryKey;
    
    /**
     *  The in foreign key. 
     */
    protected boolean inForeignKey;

    /** 
     * The comments. 
     */
    protected String comments;

    /**
     * Instantiates a new abstract ER attribute.
     *
     * @param serverObject the server object
     * @param inPrimaryKey the in primary key
     * @param inForeignKey the in foreign key
     */
    public AbstractERAttribute(ServerObject serverObject, boolean inPrimaryKey, boolean inForeignKey) {
        super(serverObject);
        this.inPrimaryKey = inPrimaryKey;
        this.inForeignKey = inForeignKey;
    }

    /**
     * Checks if is in primary key.
     *
     * @return true, if is in primary key
     */
    public boolean isInPrimaryKey() {
        return inPrimaryKey;
    }

    /**
     * Checks if is in foreign key.
     *
     * @return true, if is in foreign key
     */
    public boolean isInForeignKey() {
        return inForeignKey;
    }

    /**
     * Gets the data types.
     *
     * @return the data types
     */
    public abstract String getDataTypes();

    /**
     * Gets the comments.
     *
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments.
     *
     * @param comments the new comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Gets the nullability.
     *
     * @return the nullability
     */
    public abstract String getNullability();

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return serverObject.getName();
    }
}
