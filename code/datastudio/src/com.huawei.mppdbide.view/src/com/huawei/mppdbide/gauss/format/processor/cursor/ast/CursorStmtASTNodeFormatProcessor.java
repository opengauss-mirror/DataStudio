/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.cursor.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.ASTStartNodeNoNewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.CreateParamsNewlineWithIndentFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithOffsetSqlNodeFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NoPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.cursor.TCursorASTNode;

/**
 * Title: IfStmtASTNodeFormatProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
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
