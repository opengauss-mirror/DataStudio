/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.consts.ListItemOptionsEnum;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.format.utils.FormatterUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * Title: ColumnlistCommaProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class ColumnlistCommaProcessor extends AbstractProcessor<TParseTreeNodeList<TListItem>> {

    private boolean addPreSpace = true;
    private boolean semiColonList = false;

    /**
     * return true if SemiColonList is there.
     *
     * @return true, if SemiColonList is there.
     */
    public boolean isSemiColonList() {
        return semiColonList;
    }

    /**
     * sets the SemiColonList
     * 
     * @param semiColonList the semicolon list
     */
    public void setSemiColonList(boolean semiColonList) {
        this.semiColonList = semiColonList;
    }

    /**
     * return true if pre added space are there.
     *
     * @return true, if is adds the pre space
     */
    public boolean isAddPreSpace() {
        return addPreSpace;
    }

    /**
     * set prePreSpace flag.
     *
     * @param addPreSpace the new adds the pre space
     */
    public void setAddPreSpace(boolean addPreSpace) {
        this.addPreSpace = addPreSpace;
    }

    /**
     * process data.
     *
     * @param columns the columns
     * @param options the options
     * @param pData the data
     */
    @Override
    public void process(TParseTreeNodeList<TListItem> columns, FmtOptionsIf options, OptionsProcessData pData) {
        // in one line if possible need to find the
        int offset = pData.getOffSet();
        boolean expContainStmt = columns.isExpContainStmt();

        ListItemOptionsEnum itemOption = getItemOption(columns, options, pData, expContainStmt);

        int maxNodeItemLength = 0;

        boolean commaAfterItem = getCommanAfterItem(itemOption, pData.getFormatItemsType());

        // for single line

        int itemsSize = columns.getResultList().size();

        offset -= (addPreSpace == false ? 1 : 0);
        // for fit layout need to maintain running count
        int runningSize = offset;

        OptionsProcessData pDataClone = pData.clone();

        for (int itemIndex = 0; itemIndex < itemsSize; itemIndex++) {

            TListItem parseTreeNode = columns.getResultList().get(itemIndex);
            boolean isLastIndex = itemIndex == itemsSize - 1;

            runningSize += getStartIndexSize(itemIndex);

            if (isAddPreEmptySpace(commaAfterItem, itemIndex)) {
                parseTreeNode.getItemListNode().addPreText(" ");
                runningSize++;
            }

            pDataClone.setOffSet(runningSize);

            TParseTreeNode parseNode = parseTreeNode.getItemListNode();

            checkAndProcessExpression(options, pDataClone, parseNode);

            int itemSize = getItemSize(runningSize, pDataClone, expContainStmt, parseNode);

            if (maxNodeItemLength < itemSize) {
                maxNodeItemLength = itemSize;
            }

            runningSize = processAsAndEndNode(options, runningSize, pDataClone, parseTreeNode, offset);

            // add new line char
            if (ListItemOptionsEnum.FIT == itemOption) {
                runningSize = handleFitOption(options, offset, runningSize, parseTreeNode, itemSize);
            } else if (isOneItemPerLine(itemOption, parseTreeNode)) {
                runningSize = handleOneItemPerLine(options,
                        new HandleOneItemPerLineParameter(offset, commaAfterItem, runningSize, isLastIndex),
                        parseTreeNode);
            } else {
                runningSize = addSeparatorLength(runningSize, parseTreeNode);
            }
        }

        handleOneItemPerLine(columns, options,
                new OneItemPerLineParameter(offset, commaAfterItem, itemsSize, runningSize), itemOption);

        if (isAlignColumns(options, pData, new AlignColumnsParameter(expContainStmt, runningSize, itemsSize),
                itemOption)) {
            for (int itemIndex = 0; itemIndex < itemsSize; itemIndex++) {
                TListItem parseTreeNode = columns.getResultList().get(itemIndex);

                if (null == parseTreeNode.getAs() && null == parseTreeNode.getEndNode()) {
                    continue;
                }
                addPostTextAlignColumn(options, maxNodeItemLength, commaAfterItem, itemIndex, parseTreeNode);
            }
        }
    }

    /**
     * Gets the item size.
     *
     * @param runningSize current size of the process
     * @param pDataClone options data while process
     * @param expContainStmt to know that expression contain stmt or nor
     * @param parseNode node to process
     * @return the item size
     * @apiNote getItemSize
     */
    protected int getItemSize(int runningSize, OptionsProcessData pDataClone, boolean expContainStmt,
            TParseTreeNode parseNode) {
        return pDataClone.getOffSet() - runningSize;
    }

    private void addPostTextAlignColumn(FmtOptionsIf options, int maxNodeItemLength, boolean commaAfterItem,
            int itemIndex, TListItem parseTreeNode) {
        StringBuilder lsb = new StringBuilder(10);
        ProcessorUtils.fillSBWithIndendationChars(
                getIndendChars(maxNodeItemLength, commaAfterItem, itemIndex, parseTreeNode, options), lsb, options);

        parseTreeNode.getItemListNode().addPostText(lsb.toString());
    }

    private int handleFitOption(FmtOptionsIf options, int offset, int runningSize, TListItem parseTreeNode,
            int itemSize) {
        if (runningSize >= getOptions().getRightMargin()) {
            // REMOVE last character which is there as part ofr
            // isAddPreEmpty space
            ProcessorUtils.addNewLineBefore(parseTreeNode.getItemListNode(), offset + 1, options);
            runningSize = offset + 1 + itemSize;
        }
        runningSize = addSeparatorLength(runningSize, parseTreeNode);
        return runningSize;
    }

    /**
     * handle one item per line.
     *
     * @param options the options
     * @param parameterObject combined parameter object to pass
     * @param parseTreeNode the parse tree node
     * @return the int
     */
    protected int handleOneItemPerLine(FmtOptionsIf options, HandleOneItemPerLineParameter parameterObject,
            TListItem parseTreeNode) {
        int runningSize = parameterObject.getRunningSize();
        if (null != parseTreeNode.getSeperator()) {
            if (!parameterObject.isCommaAfterItem()) {
                ProcessorUtils.addNewLineBefore(parseTreeNode.getSeperator(), parameterObject.getOffset(), options);
                runningSize = parameterObject.getOffset() + getNodeTextLength(parseTreeNode);
            } else {
                ProcessorUtils.addNewLineAfter(parseTreeNode.getSeperator(), parameterObject.getOffset(), options);
                runningSize = parameterObject.getOffset();
            }
        }
        return runningSize;
    }

    private int getStartIndexSize(int itemIndex) {
        return itemIndex == 0 && !addPreSpace ? 1 : 0;
    }

    private int addSeparatorLength(int runningSize, TListItem parseTreeNode) {
        if (null != parseTreeNode.getSeperator()) {
            runningSize += getNodeTextLength(parseTreeNode);
        }
        return runningSize;
    }

    /**
     * return length of Node text.
     *
     * @param parseTreeNode the parse tree node
     * @return the node text length
     */
    protected int getNodeTextLength(TListItem parseTreeNode) {
        return parseTreeNode.getSeperator().getNodeText().length();
    }

    /**
     * return true is columns are aligned.
     *
     * @param options the options
     * @param pData the data
     * @param parameterObject combined object to pass the parameters
     * @param itemOption the item option
     * @return true is columns are aligned
     */
    protected boolean isAlignColumns(FmtOptionsIf options, OptionsProcessData pData,
            AlignColumnsParameter parameterObject, ListItemOptionsEnum itemOption) {
        return !parameterObject.isExpContainStmt() && options.isAlign(pData.getFormatItemsType())
                && (ListItemOptionsEnum.ONEITEMPERLINE == itemOption
                        || (ListItemOptionsEnum.ONLINEIFPOSSIBLE == itemOption && isOneLineIfPossible(itemOption,
                                parameterObject.getRunningSize(), parameterObject.getItemsSize())));
    }

    private void handleOneItemPerLine(TParseTreeNodeList<TListItem> columns, FmtOptionsIf options,
            OneItemPerLineParameter parameterObject, ListItemOptionsEnum itemOption) {
        if (ListItemOptionsEnum.ONLINEIFPOSSIBLE == itemOption) {
            if (isOneLineIfPossible(itemOption, parameterObject.getRunningSize(), parameterObject.getItemsSize())) {
                for (int itemIndex = 0; itemIndex < parameterObject.getItemsSize(); itemIndex++) {
                    TListItem parseTreeNode = columns.getResultList().get(itemIndex);
                    if (null != parseTreeNode.getSeperator()) {
                        if (!parameterObject.isCommaAfterItem()) {
                            ProcessorUtils.addNewLineBefore(parseTreeNode.getSeperator(), parameterObject.getOffset(),
                                    options);
                        } else {
                            ProcessorUtils.addNewLineAfter(parseTreeNode.getSeperator(), parameterObject.getOffset(),
                                    options);
                        }
                    }
                }
            } else {
                if (!parameterObject.isCommaAfterItem()) {
                    for (int itemIndex = 0; itemIndex < parameterObject.getItemsSize(); itemIndex++) {
                        TListItem parseTreeNode = columns.getResultList().get(itemIndex);
                        if (0 != itemIndex) {
                            parseTreeNode.getItemListNode().addPreText(" ");
                        }
                    }
                }
            }
        }

    }

    private int getIndendChars(int maxNodeItemLength, boolean commaAfterItem, int itemIndex, TListItem parseTreeNode,
            FmtOptionsIf options) {
        return maxNodeItemLength + (isAddPreEmptySpace(commaAfterItem, itemIndex) ? 1 : 0)
                - FormatterUtils.getFormatString(parseTreeNode.getItemListNode(), options).length();
    }

    private boolean isOneLineIfPossible(ListItemOptionsEnum itemOption, int runningSize, int itemsSize) {

        return Math.max(itemsSize - 1, 0) + runningSize >= getOptions().getRightMargin();
    }

    /**
     * return true if there is only one item per line.
     *
     * @param itemOption the item option
     * @param parseTreeNode the parse tree node
     * @return true, if is one item per line
     */
    protected boolean isOneItemPerLine(ListItemOptionsEnum itemOption, TListItem parseTreeNode) {
        return ListItemOptionsEnum.ONEITEMPERLINE == itemOption && null != parseTreeNode.getSeperator();
    }

    private boolean isAddPreEmptySpace(boolean commaAfterItem, int itemIndex) {
        // for first item
        // case 1 : addPreSpace is true

        // for other items
        // case 1 : commaAfterItem is true and one item per line

        if (semiColonList) {
            return false;
        }

        if (itemIndex == 0) {
            if (this.addPreSpace) {
                return true;
            }
        } else if (commaAfterItem) {
            return true;
        }
        return false;
    }

    /**
     * process As and End node.
     *
     * @param options format configured options
     * @param runningSize for process
     * @param pDataClone data for options
     * @param parseTreeNode node to process
     * @param offset to process
     * @return returns the runningSize
     */
    protected int processAsAndEndNode(FmtOptionsIf options, int runningSize, OptionsProcessData pDataClone,
            TListItem parseTreeNode, int offset) {
        int retRunningSize = runningSize;
        retRunningSize = pDataClone.getOffSet();
        if (null != parseTreeNode.getAs()) {
            parseTreeNode.getAs().addPreText(" ");
            retRunningSize += parseTreeNode.getAs().getNodeText().length() + 1;
        }

        retRunningSize = processEndNode(options, retRunningSize, pDataClone, parseTreeNode, offset);
        return retRunningSize;
    }

    /**
     * process End node.
     *
     * @param options format configured options
     * @param runningSize for process
     * @param pDataClone data for options
     * @param parseTreeNode node to process
     * @param offset to process
     * @return the int
     */
    protected int processEndNode(FmtOptionsIf options, int runningSize, OptionsProcessData pDataClone,
            TListItem parseTreeNode, int offset) {
        if (null != parseTreeNode.getEndNode()) {
            parseTreeNode.getEndNode().addPreText(" ");
            runningSize++;
            pDataClone.setOffSet(runningSize);
            ExpressionProcessor lExpressionProcessor = new ExpressionProcessor();
            lExpressionProcessor.process((TExpression) parseTreeNode.getEndNode(), options, pDataClone);

            runningSize = pDataClone.getOffSet();
        }
        return runningSize;
    }

    private void checkAndProcessExpression(FmtOptionsIf options, OptionsProcessData pDataClone,
            TParseTreeNode parseNode) {
        ExpressionProcessor lExpressionProcessor = new ExpressionProcessor();
        if (parseNode instanceof TExpression) {
            lExpressionProcessor.process((TExpression) parseNode, options, pDataClone);
        }
    }

    /**
     * Gets the item option.
     *
     * @param columns the columns
     * @param options the options
     * @param pData the data
     * @param expContainStmt the exp contain stmt
     * @return the item option
     */
    protected ListItemOptionsEnum getItemOption(TParseTreeNodeList<TListItem> columns, FmtOptionsIf options,
            OptionsProcessData pData, boolean expContainStmt) {
        ListItemOptionsEnum itemOption = options.getItemOption(pData.getFormatItemsType());

        if (expContainStmt) {
            itemOption = ListItemOptionsEnum.ONEITEMPERLINE;
        }
        return itemOption;
    }

    private boolean getCommanAfterItem(ListItemOptionsEnum itemOption, FormatItemsType formatOptType) {
        boolean commaAfterItem = getOptions().getCommaAfteritem(formatOptType);

        if (itemOption == ListItemOptionsEnum.FIT || itemOption == ListItemOptionsEnum.ONELINE) {
            commaAfterItem = true;
        }

        return commaAfterItem;
    }

    /**
     * return start node.
     *
     * @param selectAstNode the select ast node
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode(TParseTreeNodeList<TListItem> selectAstNode) {
        return null;
    }

}
