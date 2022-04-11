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

package org.opengauss.mppdbide.gauss.format.processor.begin.ast;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.WhenListFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.begin.TBeginASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: LoopASTNodeFormatProcessor
 *
 * @since 3.0.0
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
