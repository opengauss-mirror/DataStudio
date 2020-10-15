/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.exceptionwhen.TWhenExprList;

/**
 * Title: WhenListFormatProcessorListener Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Dec-2019]
 * @since 19-Dec-2019
 */
public class WhenListFormatProcessorListener implements IFormarProcessorListener {
    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {
        TWhenExprList expressionList = getWhenExprList(nextNode);

        // add the expressionList

        WhenListProcessor lColumnlistCommaProcessor = new WhenListProcessor();
        lColumnlistCommaProcessor.setOptions(options);

        lColumnlistCommaProcessor.process(expressionList, options, pData);
    }

    private TWhenExprList getWhenExprList(TParseTreeNode tAbstractListItem) {
        if (tAbstractListItem instanceof TWhenExprList) {
            return (TWhenExprList) tAbstractListItem;
        }
        throw new GaussDBSQLParserException("Unable to typecase the statement");
    }
}
