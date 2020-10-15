/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIModelOperationalPlanNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
