/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.insert;

import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TInsertSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: InsertStmtFormatProcessor
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
public class InsertStmtFormatProcessor extends AbstractProcessor<TInsertSqlStatement> {

    /**
     * Gets the options process data.
     *
     * @param selectAstNode the select ast node
     * @param pData the data
     * @param options the options
     * @return the options process data
     */
    public OptionsProcessData getOptionsProcessData(TInsertSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {

        int maxKeywordWidth = getMaxKeywordWidth(selectAstNode, options);

        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, maxKeywordWidth);

        lOptionsProcessData.setFormatItemsType(FormatItemsType.INSERT);

        return lOptionsProcessData;
    }

    /**
     * Gets the start node.
     *
     * @param selectAstNode the select ast node
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode(TInsertSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }

    /**
     * Checks if is clone data.
     *
     * @return true, if is clone data
     */
    public boolean isCloneData() {
        return true;
    }

}