/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TWhereListItem;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: WhereListCommaProcessor Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class WhereListCommaProcessor extends AbstractProcessor<TParseTreeNodeList<TWhereListItem>> {

    /**
     * process.
     *
     * @param columns the columns
     * @param options the options
     * @param pData the data
     */
    @Override
    public void process(TParseTreeNodeList<TWhereListItem> columns, FmtOptionsIf options, OptionsProcessData pData) {
        // in one line if possible need to find the
        int offset = pData.getOffSet();
        int poffset = pData.getParentOffSet();

        boolean spliAtZeroLevel = splitAtZeroLevel(options, pData);

        boolean andOrAfterExp = andOrAfterExp(options, pData);

        boolean andOrUnderWhere = andOrUnderWhere(options);

        boolean expContainStmt = isContainExpression(columns);

        if (expContainStmt) {
            spliAtZeroLevel = true;
        }

        // for single line

        int itemsSize = columns.getResultList().size();

        ExpressionProcessor lExpressionProcessor = new ExpressionProcessor();

        OptionsProcessData pDataClone = pData.clone();

        // for fit layout need to maintain running count
        int runningSize = offset;

        for (int itemIndex = 0; itemIndex < itemsSize; itemIndex++) {

            runningSize = processWhereList(columns, options, offset, poffset, spliAtZeroLevel, andOrAfterExp,
                    andOrUnderWhere, lExpressionProcessor, pDataClone, runningSize, itemIndex);
        }
    }

    private int processWhereList(TParseTreeNodeList<TWhereListItem> columns, FmtOptionsIf options, int offset,
            int poffset, boolean spliAtZeroLevel, boolean andOrAfterExp, boolean andOrUnderWhere,
            ExpressionProcessor lExpressionProcessor, OptionsProcessData pDataClone, int runningSizeParam,
            int itemIndex) {
        int runningSize = runningSizeParam;
        TWhereListItem parseTreeNode = columns.getResultList().get(itemIndex);

        parseTreeNode.getItemListNode().addPreText(" ");
        runningSize++;

        pDataClone.setOffSet(runningSize);

        lExpressionProcessor.process((TExpression) parseTreeNode.getItemListNode(), options, pDataClone);

        runningSize = pDataClone.getOffSet();

        TSqlNode conSep = parseTreeNode.getConSep();

        if (spliAtZeroLevel && null != conSep) {
            runningSize = splitAtZeroLevel(options, offset, poffset, andOrAfterExp, andOrUnderWhere, conSep);
        } else {
            if (null != conSep) {
                conSep.addPreText(" ");
                runningSize += offset + 1 + conSep.getNodeText().length();
            }
            if (runningSize >= getOptions().getRightMargin()) {
                ProcessorUtils.addNewLineAfter(parseTreeNode, offset, options);
                runningSize = offset + 1;
            }
        }
        return runningSize;
    }

    private int splitAtZeroLevel(FmtOptionsIf options, int offset, int poffset, boolean andOrAfterExp,
            boolean andOrUnderWhere, TSqlNode conSep) {
        int runningSize;
        if (andOrAfterExp) {
            conSep.addPreText(" ");
            ProcessorUtils.addNewLineAfter(conSep, offset, options);
            runningSize = offset;

        } else {
            conSep.addPreText(System.lineSeparator());

            if (andOrUnderWhere) {
                handleAndOrUnderWhere(options, offset, poffset, conSep);
                runningSize = offset - poffset;
            } else {
                StringBuilder lsb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                ProcessorUtils.fillSBWithIndendationChars(offset, lsb, options);
                conSep.addPreText(lsb.toString());
                conSep.addPreText(" ");
                runningSize = offset + 1 + conSep.getNodeText().length();
            }

        }
        return runningSize;
    }

    /**
     * handles and/or under where clause.
     *
     * @param options the options
     * @param offset the offset
     * @param poffset the poffset
     * @param conSep the con sep
     */
    protected void handleAndOrUnderWhere(FmtOptionsIf options, int offset, int poffset, TSqlNode conSep) {
        int whereConOffSet = offset - poffset;
        StringBuilder lsb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        ProcessorUtils.fillSBWithIndendationChars(poffset, lsb, options);
        conSep.addPreText(lsb.toString());
        ProcessorUtils.formatStartNode(conSep, options, whereConOffSet);
    }

    /**
     * return true if and/or is under where.
     *
     * @param options the options
     * @return true, if successful
     */
    protected boolean andOrUnderWhere(FmtOptionsIf options) {
        return options.andOrUnderWhere();
    }

    /**
     * return true if and/or after expr.
     *
     * @param options the options
     * @param pData the data
     * @return true, if successful
     */
    protected boolean andOrAfterExp(FmtOptionsIf options, OptionsProcessData pData) {
        return options.andOrAfterExp(pData.getFormatItemsType());
    }

    /**
     * return true if can be split at zero level.
     *
     * @param options the options
     * @param pData the data
     * @return true, if successful
     */
    protected boolean splitAtZeroLevel(FmtOptionsIf options, OptionsProcessData pData) {
        return options.splitAtZeroLevel(pData.getFormatItemsType());
    }

    private boolean isContainExpression(TParseTreeNodeList<TWhereListItem> columns) {
        boolean expContainStmt = false;

        for (TWhereListItem parseTreeNode : columns.getResultList()) {

            TParseTreeNode itemListNode = parseTreeNode.getItemListNode();
            if (itemListNode instanceof TExpression) {

                TExpression expr = (TExpression) itemListNode;
                for (TExpressionNode expNode : expr.getExpList()) {
                    if (null != expNode.getCustomStmt()) {
                        return true;
                    }
                }
            }

        }
        return expContainStmt;
    }

    /**
     * return start node
     */
    @Override
    public TParseTreeNode getStartNode(TParseTreeNodeList<TWhereListItem> selectAstNode) {
        return null;
    }

}
