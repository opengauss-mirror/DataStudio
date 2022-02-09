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
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIModelContentNode.
 *
 * @since 3.0.0
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
