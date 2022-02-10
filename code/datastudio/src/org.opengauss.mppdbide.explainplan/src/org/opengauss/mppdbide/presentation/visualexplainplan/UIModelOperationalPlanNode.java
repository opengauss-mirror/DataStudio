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

package org.opengauss.mppdbide.presentation.visualexplainplan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIModelOperationalPlanNode.
 *
 * @since 3.0.0
 */
public class UIModelOperationalPlanNode extends BasicUIModelPlanNode {

    /**
     * The parent.
     */
    protected UIModelOperationalPlanNode parent;

    /**
     * The children.
     */
    protected ArrayList<UIModelOperationalPlanNode> children;

    /**
     * The model.
     */
    protected OperationalNode model;

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(UIModelOperationalPlanNode parent) {
        this.parent = parent;
    }

    /**
     * Instantiates a new UI model operational plan node.
     *
     * @param operationalNode the operational node
     */
    public UIModelOperationalPlanNode(OperationalNode operationalNode) {
        super();
        this.model = operationalNode;
        children = new ArrayList<UIModelOperationalPlanNode>(0);
    }

    /**
     * Adds the child node.
     *
     * @param childnode the childnode
     */
    public void addChildNode(UIModelOperationalPlanNode childnode) {
        children.add(childnode);
    }

    /**
     * To string.
     *
     * @return the string
     */
    public String toString() {
        String chilNodeInfo = null;
        if (this.children.size() == 0) {
            return super.toString();
        }

        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        int index = 1;
        for (BasicUIModelPlanNode child : this.children) {
            sb.append(index + ". ");
            sb.append(child.toString());
            sb.append(System.lineSeparator());
            index++;
        }

        chilNodeInfo = sb.toString();

        return super.toString() + System.lineSeparator() + chilNodeInfo;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public UIModelOperationalPlanNode getParent() {
        return this.parent;
    }

    /**
     * Gets the output.
     *
     * @return the output
     */
    public String getOutput() {
        return "Output : " + Arrays.toString(this.model.getOutput());
    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    public List<String> getNodeSpecific() {

        return null;
    }

    /**
     * Gets the parent relationship.
     *
     * @return the parent relationship
     */
    public String getParentRelationship() {
        return model.getParentRelationship();
    }

}
