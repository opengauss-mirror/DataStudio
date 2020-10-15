/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.insert.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.ASTStartNodeNoNewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithIndentFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NoPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertIntoASTNode;

/**
 * 
 * Title: InsertIntoASTNodeFormatProcessor
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
public class InsertIntoASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);

        TInsertIntoASTNode insertNode = (TInsertIntoASTNode) node;

        addFormatProcessListener(insertNode.getKeywordNode(), new ASTStartNodeNoNewlineFormatProcessorListener());

        addFormatProcessListener(insertNode.getTableName(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(insertNode.getStartInsertAstBracket(), new NewlineWithIndentFormatProcessorListener());

        ResultListFormatProcessorListener processListener = new ResultListFormatProcessorListener();
        processListener.setAddPreSpace(false);
        addFormatProcessListener(insertNode.getItemList(), processListener);

        addFormatProcessListener(insertNode.getEndInsertAstBracket(), new NoPreTextFormatProcessorListener());

    }

}
