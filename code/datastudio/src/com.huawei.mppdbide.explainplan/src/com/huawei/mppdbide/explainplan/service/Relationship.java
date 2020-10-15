/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.service;

/**
 * 
 * Title: class
 * 
 * Description: The Class Relationship.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class Relationship {
    private AnalysedPlanNode parentNode;
    private AnalysedPlanNode childNode;

    /**
     * Instantiates a new relationship.
     *
     * @param parent the parent
     * @param child the child
     */
    public Relationship(AnalysedPlanNode parent, AnalysedPlanNode child) {
        this.parentNode = parent;
        this.childNode = child;
    }

    /**
     * Gets the parent node.
     *
     * @return the parent node
     */
    public AnalysedPlanNode getParentNode() {
        return parentNode;
    }

    /**
     * Sets the parent node.
     *
     * @param parentNode the new parent node
     */
    public void setParentNode(AnalysedPlanNode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * Gets the child node.
     *
     * @return the child node
     */
    public AnalysedPlanNode getChildNode() {
        return childNode;
    }

    /**
     * Sets the child node.
     *
     * @param childNode the new child node
     */
    public void setChildNode(AnalysedPlanNode childNode) {
        this.childNode = childNode;
    }

    /**
     * Gets the record count.
     *
     * @return the record count
     */
    public long getRecordCount() {
        return getChildNode().getRecordCount();
    }
}
