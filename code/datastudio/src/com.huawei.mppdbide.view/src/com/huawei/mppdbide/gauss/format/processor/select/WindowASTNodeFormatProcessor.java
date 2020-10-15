/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.select;

import com.huawei.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;

/**
 * 
 * Title: WindowASTNodeFormatProcessor
 * 
 * Description: The Class WindowASTNodeFormatProcessor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, Dec 3, 2019]
 * @since Dec 3, 2019
 */
public class WindowASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {

        super.beforeProcess(node);

        addFormatProcessListener(node.getItemList(), new ResultListFormatProcessorListener());

    }
}
