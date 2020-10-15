/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.debugobj.TCreateSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: FullStmtCTEExpressionProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Dec 5, 2019]
 * @since Dec 5, 2019
 */
public class FullStmtCTEExpressionProcessor extends ExpressionProcessor {
    /**
     * process expression with custom statement
     */
    protected void processExpressionCustomStmt(FmtOptionsIf options, OptionsProcessData pData,
            TExpressionNode expNode) {
        OptionsProcessData clone = pData.clone();
        TParseTreeNode customStmt = expNode.getCustomStmt();
        if (customStmt instanceof TCreateSqlStatement) {
            clone.addOffSet(pData.getParentOffSet() + options.getIndend());
        } else {
            clone.addOffSet(pData.getParentOffSet());
        }
        AbstractProcessorUtils.processParseTreeNode(customStmt, options, clone);
    }

    /**
     * handle each ExprNode Format
     * 
     * @param pData the data
     * @param prePreText previous previous text
     * @param pretext previous text
     * @param currenttext current text
     * @param expNode the exp node
     * @param options the options
     */
    protected void handleExprNodeFormat(OptionsProcessData pData, String prePreText, String pretext, String currenttext,
            TExpressionNode expNode, FmtOptionsIf options) {
        if ("end".equalsIgnoreCase(currenttext)) {
            ProcessorUtils.addNewLineBefore(expNode, pData.getParentOffSet(), options);
        } else {
            super.handleExprNodeFormat(pData, prePreText, pretext, currenttext, expNode, options);
        }
    }
}
