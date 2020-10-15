/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.forloop.ast;

import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.IfStmtConditionListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.forloop.TForLoopASTNode;

/**
 * Title: IfStmtASTNodeFormatProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class ForLoopASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {
    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode
        TForLoopASTNode selectAstNode = getAstNode(node);
        // add the listener for the if condition
        addFormatProcessListener(selectAstNode.getTargetName(), new AddEmptyPreTextFormatProcessorListener());
        addFormatProcessListener(selectAstNode.getItemList(), new IfStmtConditionListFormatProcessorListener());
    }

    private TForLoopASTNode getAstNode(TBasicASTNode node) {
        if (!(node instanceof TForLoopASTNode)) {
            throw new GaussDBSQLParserException("Unable to Cast the AST Node");
        }
        return (TForLoopASTNode) node;
    }

    /**
     * After process.
     *
     * @param node the node
     * @param clonedOptData the cloned opt data
     */
    public void afterProcess(TBasicASTNode node, OptionsProcessData clonedOptData) {
        clonedOptData.setOffSet(clonedOptData.getParentOffSet());
    }
}
