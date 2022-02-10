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

import org.opengauss.mppdbide.explainplan.service.AnalysedPlanNode;
import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanNodePropertiesCore.
 *
 * @since 3.0.0
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
