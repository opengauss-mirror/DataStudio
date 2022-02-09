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

package org.opengauss.mppdbide.gauss.format.processor.cursor.ast;

import org.opengauss.mppdbide.gauss.format.processor.listimpl.ASTStartNodeNoNewlineFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.CreateParamsNewlineWithIndentFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NewlineWithOffsetSqlNodeFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NoPreTextFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import org.opengauss.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.cursor.TCursorASTNode;

/**
 * Title: IfStmtASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class CursorStmtASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {
    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        TCursorASTNode selectAstNode = getAstNode(node);

        addFormatProcessListener(selectAstNode.getKeywordNode(), new ASTStartNodeNoNewlineFormatProcessorListener());
        // add the listener for the if condition
        addFormatProcessListener(selectAstNode.getCursorExpression(), new AddEmptyPreTextFormatProcessorListener());

        if (this.getOptions().isListAtLeftMargin()) {
            addFormatProcessListener(selectAstNode.getParamStartBracket(), new NewlineFormatProcessorListener());
            if (ProcessorUtils.isNodeListAvailable(selectAstNode.getItemList())) {
                addFormatProcessListener(selectAstNode.getItemList(),
                        new CreateParamsNewlineWithIndentFormatProcessorListener());
            }
            addFormatProcessListener(selectAstNode.getParamEndBracket(),
                    new NewlineWithOffsetSqlNodeFormatProcessorListener());
        } else {
            addFormatProcessListener(selectAstNode.getParamStartBracket(), new NoPreTextFormatProcessorListener());
            if (ProcessorUtils.isNodeListAvailable(selectAstNode.getItemList())) {
                addFormatProcessListener(selectAstNode.getItemList(), getListItemProcessListener());
            }
            addFormatProcessListener(selectAstNode.getParamEndBracket(), new NoPreTextFormatProcessorListener());
        }

        addFormatProcessListener(selectAstNode.getCursorStmts(), new AddEmptyPreTextFormatProcessorListener());
    }

    private TCursorASTNode getAstNode(TBasicASTNode node) {
        if (!(node instanceof TCursorASTNode)) {
            throw new GaussDBSQLParserException("Unable to Cast the AST Node");
        }
        return (TCursorASTNode) node;
    }

    private ResultListFormatProcessorListener getListItemProcessListener() {
        ResultListFormatProcessorListener lResultListFormatProcessorListener = new ResultListFormatProcessorListener();
        lResultListFormatProcessorListener.setAddPreSpace(false);
        return lResultListFormatProcessorListener;
    }
}
