/*
 * Copyright: Huawei Technologies Co., Ltd. Copyright 2012-2019, All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.consts.ListItemOptionsEnum;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TListItem;

/**
 * Title: WithColumnlistCommaProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 02-Dec-2019]
 * @since 02-Dec-2019
 */

public class WithColumnlistCommaProcessor extends ColumnlistCommaProcessor {
    /**
     * process end node
     */
    protected int processEndNode(FmtOptionsIf options, int runningSize, OptionsProcessData pDataClone,
            TListItem parseTreeNode, int offset) {
        if (null != parseTreeNode.getEndNode()) {
            pDataClone.setOffSet(offset + 1);
            ProcessorUtils.addNewLineBeforeWithindent(parseTreeNode.getEndNode(), pDataClone, options);
            ExpressionProcessor lExpressionProcessor = new ExpressionProcessor();
            lExpressionProcessor.process((TExpression) parseTreeNode.getEndNode(), options, pDataClone);
            runningSize = pDataClone.getOffSet();
        }
        return runningSize;
    }

    /**
     * return true if columns are aligned
     */
    protected boolean isAlignColumns(FmtOptionsIf options, OptionsProcessData pData,
            AlignColumnsParameter parameterObject, ListItemOptionsEnum itemOption) {
        return false;
    }

}
