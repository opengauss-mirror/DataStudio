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

package com.huawei.mppdbide.gauss.format.processor.select;

import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.format.processor.listimpl.ASTStartNodeFormatProcessorListener;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: AbstractASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class AbstractASTNodeFormatProcessor extends AbstractProcessor<TBasicASTNode> {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode
        node.getKeywordNode().setFormatListener(new ASTStartNodeFormatProcessorListener());

    }

    /**
     * Gets the start node.
     *
     * @param selectAstNode the select ast node
     * @return the start node
     */
    public TParseTreeNode getStartNode(TBasicASTNode selectAstNode) {
        return selectAstNode.getKeywordNode();
    }

}
