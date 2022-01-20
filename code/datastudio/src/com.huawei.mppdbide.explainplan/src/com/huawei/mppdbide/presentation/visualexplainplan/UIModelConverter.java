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

package com.huawei.mppdbide.presentation.visualexplainplan;

import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIModelConverter.
 *
 * @since 3.0.0
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
