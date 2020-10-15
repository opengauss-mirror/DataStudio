/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

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
    private UIModelAnalysedPlanNode parentNode;
    private UIModelAnalysedPlanNode childNode;

    /**
     * Instantiates a new relationship.
     *
     * @param parent the parent
     * @param child the child
     */
    public Relationship(UIModelAnalysedPlanNode parent, UIModelAnalysedPlanNode child) {
        this.parentNode = parent;
        this.childNode = child;
    }

    /**
     * Gets the parent node.
     *
     * @return the parent node
     */
    public UIModelAnalysedPlanNode getParentNode() {
        return parentNode;
    }

    /**
     * Sets the parent node.
     *
     * @param parentNode the new parent node
     */
    public void setParentNode(UIModelAnalysedPlanNode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * Gets the child node.
     *
     * @return the child node
     */
    public UIModelAnalysedPlanNode getChildNode() {
        return childNode;
    }

    /**
     * Sets the child node.
     *
     * @param childNode the new child node
     */
    public void setChildNode(UIModelAnalysedPlanNode childNode) {
        this.childNode = childNode;
    }

    /**
     * Gets the record count.
     *
     * @return the record count
     */
    public long getRecordCount() {
        return getChildNode().getActualRecordCount();
    }
}
