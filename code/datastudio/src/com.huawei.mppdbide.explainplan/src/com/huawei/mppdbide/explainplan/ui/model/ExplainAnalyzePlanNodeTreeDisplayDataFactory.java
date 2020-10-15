/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.ui.model;

import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;

/**
 * Title: ExplainAnalyzePlanNodeTreeDisplayDataFactory
 * 
 * Description:A factory for creating ExplainAnalyzePlanNodeTreeDisplayData
 * objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
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
