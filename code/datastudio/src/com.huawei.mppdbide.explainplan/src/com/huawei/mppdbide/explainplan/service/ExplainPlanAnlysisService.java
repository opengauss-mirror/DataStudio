/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.service;

import com.huawei.mppdbide.explainplan.jsonparser.ExplainPlanJsonContentParser;
import com.huawei.mppdbide.explainplan.nodetypes.RootPlanNode;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanAnlysisService.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExplainPlanAnlysisService {
    private ExplainPlanJsonContentParser parser;
    private AnalysedPlanNode apn;
    private RootPlanNode planRoot;

    /**
     * Instantiates a new explain plan anlysis service.
     *
     * @param jsonContent the json content
     */
    public ExplainPlanAnlysisService(String jsonContent) {
        this.parser = new ExplainPlanJsonContentParser(jsonContent);
    }

    /**
     * Do analysis.
     *
     * @return the analysed plan node
     * @throws DatabaseOperationException the database operation exception
     */
    public AnalysedPlanNode doAnalysis() throws DatabaseOperationException {
        planRoot = parser.parseFileContents();
        AnalysePlanService analysisService = new AnalysePlanService(planRoot);
        apn = analysisService.doAnalysis();

        return apn;
    }

    /**
     * Gets the root plan.
     *
     * @return the root plan
     */
    public RootPlanNode getRootPlan() {
        return planRoot;
    }

}
