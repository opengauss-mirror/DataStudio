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
 * @since 3.0.0
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
