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

package org.opengauss.mppdbide.gauss.format.processor.listimpl;

import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractListProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.casenode.TCaseStmtExpr;

/**
 * 
 * Title: CaseExprListProcessor
 *
 * @since 3.0.0
 */
public class CaseExprListProcessor extends AbstractListProcessor {

    /**
     * Adds the list item listener.
     *
     * @param tAbstractListItem the t abstract list item
     */
    protected void addListItemListener(TAbstractListItem tAbstractListItem) {

        TCaseStmtExpr caseStmtExpr = (TCaseStmtExpr) tAbstractListItem;

        addFormatProcessListener(caseStmtExpr.getWhenOrElse(), new NewlineWithIndentFormatProcessorListener());

        addFormatProcessListener(caseStmtExpr.getEndNode(), new NewlineWithIndentFormatProcessorListener());

        addFormatProcessListener(caseStmtExpr.getConditionExpr(), new OnConditionListFormatProcessorListener());

    }

    /**
     * Before item process.
     *
     * @param pData the data
     * @param currentData the current data
     */
    protected void beforeItemProcess(OptionsProcessData pData, OptionsProcessData currentData) {

        currentData.setOffSet(pData.getOffSet());
        currentData.setParentOffSet(pData.getParentOffSet());
        currentData.setPreIndentOffSet(0);

    }

}
