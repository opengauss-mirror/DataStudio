/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractListProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.casenode.TCaseStmtExpr;

/**
 * 
 * Title: CaseExprListProcessor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
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
