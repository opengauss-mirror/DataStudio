/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanNodePropertiesCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExplainPlanNodePropertiesCore extends AbstractExplainPlanPropertyCore {
    private AnalysedPlanNode planNode;
    private AnalysedPlanNodeProperties nodeProperties;

    /**
     * Instantiates a new explain plan node properties core.
     *
     * @param obj the obj
     */
    public ExplainPlanNodePropertiesCore(AnalysedPlanNode obj) {
        super(IAbstractExplainPlanPropertyCoreLabelFactory.VISUAL_EXPLAIN_PERNODEPROPERTIES_STACK);
        this.planNode = obj;
        this.nodeProperties = new AnalysedPlanNodeProperties(planNode, this);
        setPropertiesObject(this.nodeProperties);
    }

    /**
     * Gets the window details.
     *
     * @return the window details
     */
    @Override
    public IWindowDetail getWindowDetails() {
        if (null == details) {
            this.details = new ExplainPlanNodeWindowDetails(nodeProperties.getUniqueID(), nodeProperties.getHeader());
        }
        return details;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ExplainPlanNodeWindowDetails.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class ExplainPlanNodeWindowDetails implements IWindowDetail {
        private String nodeSequenceNum;
        private String nodeTitle;

        private ExplainPlanNodeWindowDetails(String nodeSequenceNum, String nodeTitle) {
            super();
            this.nodeSequenceNum = nodeSequenceNum;
            this.nodeTitle = nodeTitle;
        }

        @Override
        public String getTitle() {
            return this.nodeTitle;
        }

        @Override
        public String getUniqueID() {
            return this.nodeSequenceNum;
        }

        @Override
        public String getIcon() {
            return null;
        }

        @Override
        public String getShortTitle() {
            return nodeTitle + " - "
                    + MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PER_NODE_DETAILS_PART_LBL);
        }

        @Override
        public boolean isCloseable() {
            return true;
        }
    }
}
