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

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractListProcessor;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.exceptionwhen.TWhenStmtExpr;

/**
 * Title: WhenListProcessor
 *
 * @since 3.0.0
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
