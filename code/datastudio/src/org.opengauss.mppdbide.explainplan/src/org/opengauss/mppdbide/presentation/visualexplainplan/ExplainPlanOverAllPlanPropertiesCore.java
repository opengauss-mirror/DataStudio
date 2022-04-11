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

import org.opengauss.mppdbide.presentation.IWindowDetail;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanOverAllPlanPropertiesCore.
 *
 * @since 3.0.0
 */
public class ExplainPlanOverAllPlanPropertiesCore extends AbstractExplainPlanPropertyCore {
    private ExplainPlanPresentation presentation;
    private ExplainPlanOverAllProperties nodeProperties;

    /**
     * Instantiates a new explain plan over all plan properties core.
     *
     * @param obj the obj
     */
    public ExplainPlanOverAllPlanPropertiesCore(ExplainPlanPresentation obj) {
        super(IAbstractExplainPlanPropertyCoreLabelFactory.VISUAL_EXPLAIN_OVERLLPROPERTIES_STACK);
        this.presentation = obj;
        this.nodeProperties = new ExplainPlanOverAllProperties(presentation, this);
        setPropertiesObject(this.nodeProperties);
    }

    /**
     * Gets the window details.
     *
     * @return the window details
     */
    @Override
    public IWindowDetail getWindowDetails() {
        return null;
    }
}
