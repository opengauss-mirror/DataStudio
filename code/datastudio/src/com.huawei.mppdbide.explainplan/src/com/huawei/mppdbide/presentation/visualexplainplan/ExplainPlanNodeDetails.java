/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanNodeDetails.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExplainPlanNodeDetails implements IExplainPlanNodeDetails {
    private AnalysedPlanNode coreNode;

    /**
     * Instantiates a new explain plan node details.
     *
     * @param node the node
     */
    public ExplainPlanNodeDetails(AnalysedPlanNode node) {
        this.coreNode = node;
    }

    /**
     * Gets the node sequence num.
     *
     * @return the node sequence num
     */
    @Override
    public int getNodeSequenceNum() {
        return this.coreNode.getNodeSequenceNum();
    }

    /**
     * Gets the node title.
     *
     * @return the node title
     */
    @Override
    public String getNodeTitle() {
        return this.coreNode.getNodeUniqueName();
    }

}
