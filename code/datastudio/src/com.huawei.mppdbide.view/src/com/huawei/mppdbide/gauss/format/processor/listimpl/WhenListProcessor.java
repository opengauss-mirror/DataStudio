/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractListProcessor;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.exceptionwhen.TWhenStmtExpr;

/**
 * Title: WhenListProcessor Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class WhenListProcessor extends AbstractListProcessor {
    /**
     * add listener
     * 
     * @param tAbstractListItem list item to which listeners to be added
     */
    protected void addListItemListener(TAbstractListItem tAbstractListItem) {
        TWhenStmtExpr caseStmtExpr = getWhenStmtExpr(tAbstractListItem);

        addFormatProcessListener(caseStmtExpr.getWhen(), new NewlineWithIndentFormatProcessorListener());

        addFormatProcessListener(caseStmtExpr.getExceptionType(), new AddEmptyPreTextFormatProcessorListener());
    }

    private TWhenStmtExpr getWhenStmtExpr(TAbstractListItem tAbstractListItem) {
        if (tAbstractListItem instanceof TWhenStmtExpr) {
            return (TWhenStmtExpr) tAbstractListItem;
        }
        throw new GaussDBSQLParserException("Unable to typecase the statement");
    }

    /**
     * process before item
     * 
     * @param pData options data to process
     * @param currentData current options data to process
     */
    protected void beforeItemProcess(OptionsProcessData pData, OptionsProcessData currentData) {
        currentData.setOffSet(pData.getOffSet());
        currentData.setParentOffSet(pData.getParentOffSet());
        currentData.setPreIndentOffSet(0);
    }

}
