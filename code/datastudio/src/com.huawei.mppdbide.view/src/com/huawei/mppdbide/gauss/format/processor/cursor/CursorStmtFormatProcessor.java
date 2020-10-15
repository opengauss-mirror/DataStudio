/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.cursor;

import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.debugobj.TCursorSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: CursorStmtFormatProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class CursorStmtFormatProcessor extends AbstractProcessor<TCursorSqlStatement> {
    /**
     * Gets the options process data.
     *
     * @param selectAstNode the select ast node
     * @param pData the data
     * @param options the options
     * @return the options process data
     */
    public OptionsProcessData getOptionsProcessData(TCursorSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, 0);
        lOptionsProcessData.setFormatItemsType(FormatItemsType.PARAMETER);
        return lOptionsProcessData;
    }

    @Override
    public TParseTreeNode getStartNode(TCursorSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }
}
