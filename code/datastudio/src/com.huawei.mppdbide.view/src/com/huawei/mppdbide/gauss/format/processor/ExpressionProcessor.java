/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import java.util.List;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: ExpressionProcessor Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class ExpressionProcessor extends AbstractProcessor<TExpression> {

    /**
     * process.
     *
     * @param selectAstNode the select ast node
     * @param options the options
     * @param pData the data
     */
    @Override
    public void process(TExpression selectAstNode, FmtOptionsIf options, OptionsProcessData pData) {
        process(selectAstNode, options, pData, true);
    }

    /**
     * process.
     *
     * @param selectAstNode the select ast node
     * @param options the options
     * @param pData the data
     * @param addPreSpace the add pre space
     */
    @Override
    public void process(TExpression selectAstNode, FmtOptionsIf options, OptionsProcessData pData,
            boolean addPreSpace) {
        List<TExpressionNode> expList = selectAstNode.getExpList();
        int itemsSize = expList.size();

        String prePreText = null;
        String pretext = null;
        String currenttext = null;

        for (int itemIndex = 0; itemIndex < itemsSize; itemIndex++) {

            TExpressionNode expNode = expList.get(itemIndex);

            if (null == expNode.getCustomStmt()) {
                if (expNode.getExpNode().getNodeText() == null) {
                    continue;
                }

                currenttext = expNode.getExpNode().getNodeText();

                if (itemIndex == 0) {
                    prePreText = pretext;
                    pretext = expNode.getExpNode().getNodeText();
                    pData.addOffSet(expNode.getExpNode().getNodeText().length());
                    continue;
                }

                handleExprNodeFormat(pData, prePreText, pretext, currenttext, expNode, options);

                prePreText = pretext;
                pretext = expNode.getExpNode().getNodeText();

            } else {

                boolean putStmtNewLine = pData.isPutStmtNewLine();
                if (selectAstNode.isDirectExpression()) {
                    pData.setPutStmtNewLine(true);
                } else {
                    pData.setPutStmtNewLine(false);
                    if (selectAstNode.getAddSpaceForCustomStmt()) {
                        pData.addOffSet(1);
                        expNode.addPreText(" ");
                    }
                }

                processExpressionCustomStmt(options, pData, expNode);

                pData.setPutStmtNewLine(putStmtNewLine);

                pretext = null;
                prePreText = null;
            }

        }

    }

    /**
     * handle each ExprNode Format
     * 
     * @param pData the data
     * @param prePreText previous previous text
     * @param pretext previous text
     * @param currenttext current text
     * @param expNode the exp node
     * @param options the options
     */
    protected void handleExprNodeFormat(OptionsProcessData pData, String prePreText, String pretext, String currenttext,
            TExpressionNode expNode, FmtOptionsIf options) {
        pData.addOffSet(expNode.getExpNode().getNodeText().length());
        if (!(ExpressionProcessorUtil.isAppendNoPreTextSpace(currenttext, pretext, prePreText))) {
            ProcessorUtils.addPreEmptyText(expNode.getExpNode(), pData);
        }
    }

    /**
     * process expression for custom statement.
     *
     * @param options the options
     * @param pData the data
     * @param expNode the exp node
     */
    protected void processExpressionCustomStmt(FmtOptionsIf options, OptionsProcessData pData,
            TExpressionNode expNode) {
        AbstractProcessorUtils.processParseTreeNode(expNode.getCustomStmt(), options, pData);
    }

    /**
     * return start node.
     *
     * @param selectAstNode the select ast node
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode(TExpression selectAstNode) {
        return null;
    }

}
