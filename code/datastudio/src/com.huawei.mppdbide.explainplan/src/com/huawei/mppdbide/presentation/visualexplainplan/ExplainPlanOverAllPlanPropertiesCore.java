/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import com.huawei.mppdbide.presentation.IWindowDetail;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanOverAllPlanPropertiesCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
