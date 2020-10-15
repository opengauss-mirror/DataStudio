/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIModelContentNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UIModelContentNode extends BasicUIModelPlanNode {

    private String outputText;

    /**
     * Instantiates a new UI model content node.
     *
     * @param str the str
     */
    public UIModelContentNode(String str) {
        this.outputText = str;
    }

    /**
     * Gets the label text.
     *
     * @return the label text
     */
    public String getLabelText() {
        return outputText;
    }

    /**
     * Gets the node contents.
     *
     * @param model the model
     * @return the node contents
     */
    public static List<UIModelContentNode> getNodeContents(UIModelOperationalPlanNode model) {
        ArrayList<UIModelContentNode> nodeList = new ArrayList<UIModelContentNode>(1);

        String str = model.getOutput();
        String parentModel = model.getParentRelationship();

        nodeList.add(new UIModelContentNode(str));
        nodeList.add(new UIModelContentNode(parentModel));

        return nodeList;
    }

}
