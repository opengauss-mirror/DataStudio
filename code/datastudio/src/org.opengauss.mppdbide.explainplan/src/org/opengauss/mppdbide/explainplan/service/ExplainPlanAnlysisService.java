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

package org.opengauss.mppdbide.explainplan.service;

import org.opengauss.mppdbide.explainplan.jsonparser.ExplainPlanJsonContentParser;
import org.opengauss.mppdbide.explainplan.nodetypes.RootPlanNode;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanAnlysisService.
 *
 * @since 3.0.0
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
