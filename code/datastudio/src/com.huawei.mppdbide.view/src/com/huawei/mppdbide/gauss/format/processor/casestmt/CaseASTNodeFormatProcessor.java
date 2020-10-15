/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.casestmt;

import com.huawei.mppdbide.gauss.format.processor.listimpl.ASTStartNodeNoNewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.CaseEndNodeFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.CaseListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.casestmt.TCaseASTNode;

/**
 * 
 * Title: CaseASTNodeFormatProcessor
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
public class CaseASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        TCaseASTNode selectAstNode = (TCaseASTNode) node;

        addFormatProcessListener(selectAstNode.getStartNode(), new ASTStartNodeNoNewlineFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getColExpression(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getItemList(), new CaseListFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getEndNode(), new CaseEndNodeFormatProcessorListener());

    }

}
