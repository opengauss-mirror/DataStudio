/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import java.util.List;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessorUtils;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: NewlineWithParentOffsetFormatProcessorListener
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
public class NewlineWithParentOffsetFormatProcessorListener implements IFormarProcessorListener {

    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        // this is for join type expression

        TExpression joinTypeExpr = (TExpression) nextNode;

        List<TExpressionNode> expList = joinTypeExpr.getExpList();

        for (int index = 0; index < expList.size(); index++) {
            TExpressionNode tExpressionNode = expList.get(index);

            TSqlNode sqlNode = tExpressionNode.getExpNode();
            if (index == 0) {

                ProcessorUtils.addNewLineBefore(sqlNode, pData.getParentOffSet(), options);
                ProcessorUtils.formatStartNode(sqlNode, options,
                        pData.getParentData().getOffSet() - pData.getParentOffSet());
                pData.setOffSet(pData.getParentData().getOffSet() - pData.getParentOffSet());
                continue;
            }

            AbstractProcessorUtils.processParseTreeNode(sqlNode, options, pData);

        }

    }

}
