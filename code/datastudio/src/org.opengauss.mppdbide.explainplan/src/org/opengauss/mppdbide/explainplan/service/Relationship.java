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

package org.opengauss.mppdbide.explainplan.service;

/**
 * 
 * Title: class
 * 
 * Description: The Class Relationship.
 *
 * @since 3.0.0
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
