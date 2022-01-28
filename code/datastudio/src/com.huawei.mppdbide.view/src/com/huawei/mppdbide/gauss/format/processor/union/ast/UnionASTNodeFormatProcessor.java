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

package com.huawei.mppdbide.gauss.format.processor.union.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.union.TUnionASTNode;

/**
 * Title: WithASTNodeFormatProcessor
 *
 * @since 3.0.0
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
