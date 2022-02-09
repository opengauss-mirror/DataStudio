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

package org.opengauss.mppdbide.gauss.format.processor.loop.ast;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractProcessorUtils;
import org.opengauss.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.loop.TLoopASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: LoopASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class LoopASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * the beforeProcess
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        TLoopASTNode selectAstNode = (TLoopASTNode) node;

        addFormatProcessListener(selectAstNode.getStartNode(), getLoopProcessorListener());

        addFormatProcessListener(selectAstNode.getEndLoop(), new IFormarProcessorListener() {

            @Override
            public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {
                int offset = pData.getParentOffSet();
                ProcessorUtils.addNewLineBefore(nextNode, offset, options);
                AbstractProcessorUtils.processParseTreeNode(nextNode, options, pData, false);
            }
        });

    }

    private IFormarProcessorListener getLoopProcessorListener() {
        return this.getOptions().isLoopOnNewLine() ? new IFormarProcessorListener() {

            @Override
            public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {
                int offset = pData.getOffSet();
                pData.setParentOffSet(offset);
                ProcessorUtils.addNewLineBefore(nextNode, offset, options);
                AbstractProcessorUtils.processParseTreeNode(nextNode, options, pData, false);
                nextNode.setCheckPreviousNewLine(Boolean.TRUE);
            }
        } : new AddEmptyPreTextFormatProcessorListener() {
            /**
             * the changeOptionsData
             */
            protected void changeOptionsData(OptionsProcessData pData) {
                pData.setParentOffSet(pData.getOffSet());
            }

        };
    }

}
