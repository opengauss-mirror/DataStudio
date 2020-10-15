/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

/**
 * Title: IAbstractExplainPlanPropertyCoreLabelFactory
 * 
 * Description:A factory for creating IAbstractExplainPlanPropertyCoreLabel
 * objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public interface IAbstractExplainPlanPropertyCoreLabelFactory {

    /**
     * The visual explain diagram stack.
     */
    int VISUAL_EXPLAIN_DIAGRAM_STACK = 1;
    
    /**
     * The visual explain overllproperties stack.
     */
    int VISUAL_EXPLAIN_OVERLLPROPERTIES_STACK = 2;
    
    /**
     * The visual explain pernodeproperties stack.
     */
    int VISUAL_EXPLAIN_PERNODEPROPERTIES_STACK = 3;

}
