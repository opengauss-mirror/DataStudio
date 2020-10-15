/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.create.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.CreateParamsNewlineWithIndentFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithProcessFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NoPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TCreateASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;

/**
 * 
 * Title: LoopASTNodeFormatProcessor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class CreateASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        TCreateASTNode selectAstNode = (TCreateASTNode) node;

        addFormatProcessListener(selectAstNode.getIntermediateText(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getProcOrFuncName(), new AddEmptyPreTextFormatProcessorListener());

        boolean isNdeListPresent = ProcessorUtils.isNodeListAvailable(selectAstNode.getItemList());
        if (this.getOptions().isListAtLeftMargin() && isNdeListPresent) {
            addFormatProcessListener(selectAstNode.getProcStartBracket(), new NewlineFormatProcessorListener());
            addFormatProcessListener(selectAstNode.getItemList(),
                    new CreateParamsNewlineWithIndentFormatProcessorListener());
            addFormatProcessListener(selectAstNode.getProcEndBracket(), new NewlineFormatProcessorListener());
        } else {
            addFormatProcessListener(selectAstNode.getProcStartBracket(), new NoPreTextFormatProcessorListener());
            if (isNdeListPresent) {
                addFormatProcessListener(selectAstNode.getItemList(), getListItemProcessListener());
            }
            addFormatProcessListener(selectAstNode.getProcEndBracket(), new NoPreTextFormatProcessorListener());
        }

        TExpression commonExpression = selectAstNode.getCommonExpression();
        if (null != commonExpression && commonExpression.getExpList().size() == 1) {
            TExpressionNode tExpressionNode = commonExpression.getExpList().get(0);
            if (tExpressionNode.getExpNode() != null && !";".equals(tExpressionNode.getExpNode().getNodeText())) {
                addFormatProcessListener(commonExpression, new NewlineWithProcessFormatProcessorListener());
            }

        } else {
            addFormatProcessListener(commonExpression, new NewlineWithProcessFormatProcessorListener());
        }
    }

    private ResultListFormatProcessorListener getListItemProcessListener() {
        ResultListFormatProcessorListener lResultListFormatProcessorListener = new ResultListFormatProcessorListener();
        lResultListFormatProcessorListener.setAddPreSpace(false);
        return lResultListFormatProcessorListener;
    }

}
