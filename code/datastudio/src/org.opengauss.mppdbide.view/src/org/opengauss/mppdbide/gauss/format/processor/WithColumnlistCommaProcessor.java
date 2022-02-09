/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.gauss.format.processor;

import org.opengauss.mppdbide.gauss.format.consts.ListItemOptionsEnum;
import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TListItem;

/**
 * Title: WithColumnlistCommaProcessor
 *
 * @since 3.0.0
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
