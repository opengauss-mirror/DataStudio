/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.union.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.union.TUnionASTNode;

/**
 * Title: WithASTNodeFormatProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class UnionASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {
    /**
     * before process
     * 
     * @param node node to format
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode
        TUnionASTNode unionAst = getAstNode(node);

        addFormatProcessListener(unionAst.getRemainingStmt(), new AddEmptyPreTextFormatProcessorListener());
    }

    private TUnionASTNode getAstNode(TBasicASTNode node) {
        if (!(node instanceof TUnionASTNode)) {
            throw new GaussDBSQLParserException("Unable to Cast the AST Node");
        }
        return (TUnionASTNode) node;
    }
}
