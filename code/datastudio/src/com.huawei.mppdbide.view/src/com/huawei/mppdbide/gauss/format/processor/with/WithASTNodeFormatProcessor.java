/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.with;

import com.huawei.mppdbide.gauss.format.processor.listimpl.ASTStartNodeNoNewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.WithResultListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;

/**
 * 
 * Title: WithASTNodeFormatProcessor
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
public class WithASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        addFormatProcessListener(node.getKeywordNode(), new ASTStartNodeNoNewlineFormatProcessorListener());

        addFormatProcessListener(node.getItemList(), new WithResultListFormatProcessorListener());

    }

}