/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.node;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: SqlNodeFormatProcessor
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
public class SqlNodeFormatProcessor extends AbstractProcessor<TSqlNode> {

    @Override
    public void process(TSqlNode selectAstNode, FmtOptionsIf options, OptionsProcessData pData, boolean addPreSpace) {
        if (addPreSpace) {
            ProcessorUtils.addPreEmptyText(selectAstNode, pData);
        }
        pData.addOffSet(selectAstNode.getNodeText().length());
    }

    @Override
    public TParseTreeNode getStartNode(TSqlNode selectAstNode) {
        return selectAstNode.getStartNode();
    }

}
