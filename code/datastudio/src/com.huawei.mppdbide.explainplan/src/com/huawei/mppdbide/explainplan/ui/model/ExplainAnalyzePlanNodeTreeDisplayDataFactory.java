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

package com.huawei.mppdbide.explainplan.ui.model;

import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;

/**
 * Title: ExplainAnalyzePlanNodeTreeDisplayDataFactory
 * 
 * Description:A factory for creating ExplainAnalyzePlanNodeTreeDisplayData
 * objects.
 * 
 * @since 3.0.0
 */
public class ExplainAnalyzePlanNodeTreeDisplayDataFactory {
    private static volatile ExplainAnalyzePlanNodeTreeDisplayDataFactory instance = null;
    private static final Object lock = new Object();
    private int newChildId;

    /**
     * Gets the single instance of ExplainAnalyzePlanNodeTreeDisplayDataFactory.
     *
     * @return single instance of ExplainAnalyzePlanNodeTreeDisplayDataFactory
     */
    public static ExplainAnalyzePlanNodeTreeDisplayDataFactory getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (null == instance) {
                    instance = new ExplainAnalyzePlanNodeTreeDisplayDataFactory();
                }
            }
        }
        return instance;
    }

    private ExplainAnalyzePlanNodeTreeDisplayDataFactory() {
        newChildId = 0;
    }

    /**
     * Creates a new ExplainAnalyzePlanNodeTreeDisplayData object.
     *
     * @param analysedNode the analysed node
     * @param isAnalyze the is analyze
     * @return the explain analyze plan node tree display data
     */
    public ExplainAnalyzePlanNodeTreeDisplayData createData(AnalysedPlanNode analysedNode, boolean isAnalyze) {
        newChildId++;
        return new ExplainAnalyzePlanNodeTreeDisplayData(newChildId, analysedNode, isAnalyze);
    }
}
