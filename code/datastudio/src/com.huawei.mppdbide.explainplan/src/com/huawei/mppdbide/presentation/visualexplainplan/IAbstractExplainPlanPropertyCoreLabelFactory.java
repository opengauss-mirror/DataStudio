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

/**
 * Title: IAbstractExplainPlanPropertyCoreLabelFactory
 * 
 * Description:A factory for creating IAbstractExplainPlanPropertyCoreLabel
 * objects.
 * 
 * @since 3.0.0
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
