/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.begin.ast;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.WhenListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.begin.TBeginASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: LoopASTNodeFormatProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class BeginASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {
    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode
        TBeginASTNode selectAstNode = (TBeginASTNode) node;
        addFormatProcessListener(selectAstNode.getEnd(), getNewlineFormat());

        addFormatProcessListener(selectAstNode.getException(), getNewlineFormat());

        addFormatProcessListener(selectAstNode.getExceptionWhenList(), new WhenListFormatProcessorListener());

        if (null != selectAstNode.getEndExpression() && selectAstNode.getEndExpression().getExpList().size() > 1) {
            addFormatProcessListener(selectAstNode.getEndExpression(), new AddEmptyPreTextFormatProcessorListener());
        }
    }

    private NewlineFormatProcessorListener getNewlineFormat() {
        return new NewlineFormatProcessorListener() {
            /**
             * format the process for the given node
             */
            public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {
                super.formatProcess(nextNode, options, pData);
                nextNode.setCheckPreviousNewLine(Boolean.TRUE);
            }
        };
    }
}
