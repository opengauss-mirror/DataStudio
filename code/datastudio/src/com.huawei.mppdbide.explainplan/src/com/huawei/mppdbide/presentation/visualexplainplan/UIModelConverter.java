/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIModelConverter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UIModelConverter {

    /**
     * Covert to UI model.
     *
     * @param analysedPlanNode the analysed plan node
     * @return the UI model analysed plan node
     */
    public static UIModelAnalysedPlanNode covertToUIModel(AnalysedPlanNode analysedPlanNode) {
        return new UIModelAnalysedPlanNode(analysedPlanNode);
    }
}
