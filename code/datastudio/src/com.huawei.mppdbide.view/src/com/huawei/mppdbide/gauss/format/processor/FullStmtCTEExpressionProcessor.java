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

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.debugobj.TCreateSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: FullStmtCTEExpressionProcessor
 *
 * @since 3.0.0
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
