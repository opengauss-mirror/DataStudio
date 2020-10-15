/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.begin;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.block.TBeginSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: LoopStmtFormatProcessor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class BeginFormatProcessor extends AbstractProcessor<TBeginSqlStatement> {

    /**
     * return option processData
     */
    public OptionsProcessData getOptionsProcessData(TBeginSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, 0);
        return lOptionsProcessData;
    }

    /**
     * return start node
     */
    @Override
    public TParseTreeNode getStartNode(TBeginSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }

    /**
     * return true if data is cloned
     */
    public boolean isCloneData() {
        return true;
    }

}
