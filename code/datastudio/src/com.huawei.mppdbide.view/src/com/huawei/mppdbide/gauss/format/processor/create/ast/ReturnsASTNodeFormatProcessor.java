/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.create.ast;

import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithProcessFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NoPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TReturnASTNode;

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
