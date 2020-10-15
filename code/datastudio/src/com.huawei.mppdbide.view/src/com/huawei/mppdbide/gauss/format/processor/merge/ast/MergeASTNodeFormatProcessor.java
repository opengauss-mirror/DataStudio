/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.merge.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithOffsetSqlNodeFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NoPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.WhereListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeASTNode;

/**
 * Title: WithASTNodeFormatProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class MergeASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {
    /**
     * adds listeners to the node before process
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode
        TMergeASTNode mergeNode = getMergeAstNode(node);

        addFormatProcessListener(mergeNode.getSrcTable(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(mergeNode.getUsing(), new NewlineWithOffsetSqlNodeFormatProcessorListener());

        addFormatProcessListener(mergeNode.getDestTable(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(mergeNode.getOn(), new NewlineFormatProcessorListener());

        addFormatProcessListener(mergeNode.getOnEndBracket(), new NoPreTextFormatProcessorListener());

        addFormatProcessListener(node.getItemList(), new WhereListFormatProcessorListener());
    }

    private TMergeASTNode getMergeAstNode(TBasicASTNode node) {
        if (node instanceof TMergeASTNode) {
            return (TMergeASTNode) node;
        }
        throw new GaussDBSQLParserException("Unable to typecase the statement");
    }
}
