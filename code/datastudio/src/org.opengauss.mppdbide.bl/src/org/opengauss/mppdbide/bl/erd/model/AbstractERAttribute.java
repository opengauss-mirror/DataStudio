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

package org.opengauss.mppdbide.bl.erd.model;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: IERAttribute 
 *
 * @since 3.0.0
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
