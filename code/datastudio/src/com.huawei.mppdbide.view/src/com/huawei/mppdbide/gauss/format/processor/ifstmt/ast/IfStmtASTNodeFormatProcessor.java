/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.ifstmt.ast;

import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.IfStmtConditionListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithProcessFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.ifstmt.TIfElseASTNode;

/**
 * 
 * Title: IfStmtASTNodeFormatProcessor
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
public class IfStmtASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        TIfElseASTNode selectAstNode = (TIfElseASTNode) node;

        // add the listener for the if condition

        addFormatProcessListener(selectAstNode.getThen(), getThenProcessorListener());

        addFormatProcessListener(selectAstNode.getItemList(), new IfStmtConditionListFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getEnd(), new NewlineWithProcessFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getEndNode(), new AddEmptyPreTextFormatProcessorListener());

    }

    private IFormarProcessorListener getThenProcessorListener() {
        return this.getOptions().isThenOnNewLine() ? new NewlineWithProcessFormatProcessorListener()
                : new AddEmptyPreTextFormatProcessorListener();
    }

}
