/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: NewlineWithOffsetSqlNodeFormatProcessorListener Description: Copyright
 * (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 18-Dec-2019]
 * @since 18-Dec-2019
 */
public class NewlineWithOffsetSqlNodeFormatProcessorListener extends NewlineFormatProcessorListener {
    /**
     * Format process.
     *
     * @param nextNode the next node
     * @param options the options
     * @param pData the data
     */
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {
        super.formatProcess(nextNode, options, pData);
        pData.addOffSet(getTSqlNode(nextNode).getNodeText().length());
    }

    private TSqlNode getTSqlNode(TParseTreeNode nextNode) {
        if (!(nextNode instanceof TSqlNode)) {
            throw new GaussDBSQLParserException("Unable to Cast the AST Node");
        }
        return (TSqlNode) nextNode;
    }
}