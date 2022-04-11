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

package org.opengauss.mppdbide.gauss.format.processor.create.ast;

import org.opengauss.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NewlineWithProcessFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NoPreTextFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.create.TReturnASTNode;

/**
 * 
 * Title: LoopASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class ReturnsASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        TReturnASTNode selectAstNode = (TReturnASTNode) node;

        addFormatProcessListener(selectAstNode.getResultExpression(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getReturnTableList(), getListFormatProcessor());

        addFormatProcessListener(selectAstNode.getReturnEndBracket(), new NoPreTextFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getCommonExpression(), new NewlineWithProcessFormatProcessorListener());

    }

    private IFormarProcessorListener getListFormatProcessor() {
        ResultListFormatProcessorListener lIFormarProcessorListener = new ResultListFormatProcessorListener();
        lIFormarProcessorListener.setAddPreSpace(false);
        return lIFormarProcessorListener;
    }

}
