/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.delete.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.ASTStartNodeNoNewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.delete.TDeleteFromASTNode;

/**
 * 
 * Title: DeleteFromASTNodeFormatProcessor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 01-Dec-2019]
 * @since 01-Dec-2019
 */
public class DeleteFromASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);

        TDeleteFromASTNode insertNode = (TDeleteFromASTNode) node;

        addFormatProcessListener(insertNode.getKeywordNode(), new ASTStartNodeNoNewlineFormatProcessorListener());

        addFormatProcessListener(insertNode.getTableName(), new AddEmptyPreTextFormatProcessorListener());

    }

}
