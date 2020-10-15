/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.union;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.common.TUnionSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: LoopStmtFormatProcessor Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class UnionFormatProcessor extends AbstractProcessor<TUnionSqlStatement> {
    /**
     * return options of proceesed data
     * 
     * @param selectAstNode selected node to process
     * @param pData options data while process
     * @param options options for format
     * @return returns the options data
     */
    public OptionsProcessData getOptionsProcessData(TUnionSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        pData.setLastFormatType(SQLTokenConstants.T_SQL_KEYWORK_UNION);
        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, 0);
        return lOptionsProcessData;
    }

    /**
     * return start node
     * 
     * @param selectAstNode custom select statement
     * @return returns the start node
     */
    @Override
    public TParseTreeNode getStartNode(TUnionSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }
}
