/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.loop.ast;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessorUtils;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.loop.TLoopASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: LoopASTNodeFormatProcessor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
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
