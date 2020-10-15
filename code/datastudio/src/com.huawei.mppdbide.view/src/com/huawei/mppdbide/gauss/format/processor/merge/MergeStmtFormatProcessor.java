/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.merge;

import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TMergeSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: WithStmtFormatProcessor Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class MergeStmtFormatProcessor extends AbstractProcessor<TMergeSqlStatement> {
    /**
     * return options of proceesed data
     * 
     * @param selectAstNode selected node to process
     * @param pData options data while process
     * @param options options for format
     * @return returns the options data
     */
    public OptionsProcessData getOptionsProcessData(TMergeSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        int maxKeywordWidth = getMaxKeywordWidth(selectAstNode, options);

        OptionsProcessData lOptionsProcessData = super.getNewOptionsDataBean(pData, maxKeywordWidth);

        lOptionsProcessData.setFormatItemsType(FormatItemsType.UNKNOWN);
        return lOptionsProcessData;
    }

    /**
     * return start node
     * 
     * @param selectAstNode node to which startnode to be returned
     * @return return start node
     */
    @Override
    public TParseTreeNode getStartNode(TMergeSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }

    /**
     * return true is data is cloned
     * 
     * @return return true is data is cloned else false
     */
    public boolean isCloneData() {
        return true;
    }
}
