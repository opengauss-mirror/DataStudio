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

/**
 * Title: AbstractERAssociation
 *
 * @since 3.0.0
 */
public abstract class AbstractERAssociation {
    /** 
     * The association. 
     */
    protected AbstractERConstraint association;

    /** 
     * The association num. 
     */
    protected int associationNum;

    /** 
     * The source entity. 
     */
    protected AbstractEREntity sourceEntity;

    /** 
     * The target entity. 
     */
    protected AbstractEREntity targetEntity;

    /**
     * Instantiates a new abstract ER association.
     *
     * @param association the association
     * @param sourceEntity the source entity
     * @param targetEntity the target entity
     */
    public AbstractERAssociation(AbstractERConstraint association, AbstractEREntity sourceEntity,
            AbstractEREntity targetEntity) {
        this.association = association;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
    }
 
    /**
     * Gets the association.
     *
     * @return the association
     */
    public AbstractERConstraint getAssociation() {
        return association;
    }
 
    /**
     * Sets the association.
     *
     * @param association the new association
     */
    public void setAssociation(AbstractERConstraint association) {
        this.association = association;
    }

    /**
     * Gets the association num.
     *
     * @return the association num
     */
    public int getAssociationNum() {
        return associationNum;
    }
 
    /**
     * Sets the association num.
     *
     * @param associationNum the new association num
     */
    public void setAssociationNum(int associationNum) {
        this.associationNum = associationNum;
    }
    
    /**
     * Gets the source entity.
     *
     * @return the source entity
     */
    public AbstractEREntity getSourceEntity() {
        return sourceEntity;
    }
    
    /**
     * Sets the source entity.
     *
     * @param sourceEntity the new source entity
     */
    public void setSourceEntity(AbstractEREntity sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    /**
     * Gets the target entity.
     *
     * @return the target entity
     */
    public AbstractEREntity getTargetEntity() {
        return targetEntity;
    }
    
    /**
     * Sets the target entity.
     *
     * @param targetEntity the new target entity
     */
    public void setTargetEntity(AbstractEREntity targetEntity) {
        this.targetEntity = targetEntity;
    }

}
