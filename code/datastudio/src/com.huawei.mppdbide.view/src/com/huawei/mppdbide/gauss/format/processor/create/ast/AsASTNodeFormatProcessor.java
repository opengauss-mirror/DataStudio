/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.create.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.DeclareFieldsNewlineWithIndentFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TAsASTNode;

/**
 * Title: LoopASTNodeFormatProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class AsASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        TAsASTNode lTAsASTNode = getAstNode(node);
        // add listener to the startnode

        if (ProcessorUtils.isNodeListAvailable(node.getItemList())) {
            addFormatProcessListener(node.getItemList(), new DeclareFieldsNewlineWithIndentFormatProcessorListener());
        }

        addFormatProcessListener(lTAsASTNode.getEndAs(), new NewlineFormatProcessorListener());

    }

    private TAsASTNode getAstNode(TBasicASTNode node) {
        if (!(node instanceof TAsASTNode)) {
            throw new GaussDBSQLParserException("Unable to Cast the AST Node");
        }
        return (TAsASTNode) node;
    }

}
