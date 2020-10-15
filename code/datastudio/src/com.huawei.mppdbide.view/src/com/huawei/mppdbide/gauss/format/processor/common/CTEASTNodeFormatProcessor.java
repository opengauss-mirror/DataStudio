/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.common;

import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TCTEASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: CTEASTNodeFormatProcessor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class CTEASTNodeFormatProcessor extends AbstractProcessor<TCTEASTNode> {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TCTEASTNode node) {
        super.beforeProcess(node);

    }

    /**
     * Gets the start node.
     *
     * @param selectAstNode the select ast node
     * @return the start node
     */
    public TParseTreeNode getStartNode(TCTEASTNode selectAstNode) {
        return selectAstNode.getStmtExpression();
    }

}
