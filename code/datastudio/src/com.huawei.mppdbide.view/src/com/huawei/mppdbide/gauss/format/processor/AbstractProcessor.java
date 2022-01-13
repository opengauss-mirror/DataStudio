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

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.utils.StmtKeywordAlignUtil;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: AbstractProcessor
 *
 * @param <E> the element type
 * @since 3.0.0
 */
public abstract class AbstractProcessor<E> {

    private FmtOptionsIf options = null;

    /**
     * before process.
     *
     * @param node node to format
     */
    public void beforeProcess(E node) {
    }

    /**
     * after process.
     *
     * @param node node to be formatted
     * @param clonedOptData options data to format
     */
    public void afterProcess(E node, OptionsProcessData clonedOptData) {
    }

    /**
     * return formatter options.
     *
     * @return formatter options
     */
    public FmtOptionsIf getOptions() {
        return options;
    }

    /**
     * set formatter options.
     *
     * @param options formatter options
     */
    public void setOptions(FmtOptionsIf options) {
        this.options = options;
    }

    /**
     * process the data.
     *
     * @param selectAstNode selected node to process
     * @param options for process
     * @param pData options data while process
     */
    public void process(E selectAstNode, FmtOptionsIf options, OptionsProcessData pData) {
        process(selectAstNode, options, pData, true);
    }

    /**
     * process the data.
     *
     * @param selectAstNode selected node to process
     * @param options for process
     * @param pData options data while process
     * @param addPreSpace add prespace before process
     */
    public void process(E selectAstNode, FmtOptionsIf options, OptionsProcessData pData, boolean addPreSpace) {
        OptionsProcessData clonedOptData = getOptionsProcessData(selectAstNode, pData, options);

        TParseTreeNode nextNode = getStartNode(selectAstNode);

        iterateAndParseNodes(options, clonedOptData, nextNode, addPreSpace);
    }

    /**
     * iterate parseTree node and do parsing.
     *
     * @param options for process
     * @param clonedOptData options data while process
     * @param startNode to process
     */
    protected void iterateAndParseNodes(FmtOptionsIf options, OptionsProcessData clonedOptData,
            TParseTreeNode startNode) {
        iterateAndParseNodes(options, clonedOptData, startNode, true);
    }

    /**
     * iterate parseTree node and do parsing.
     *
     * @param options for process
     * @param clonedOptData options data while process
     * @param startNode selected node to process
     * @param addPreSpace add prespace before process
     */
    protected void iterateAndParseNodes(FmtOptionsIf options, OptionsProcessData clonedOptData,
            TParseTreeNode startNode, boolean addPreSpace) {
        TParseTreeNode nextNode = startNode;
        while (null != nextNode) {
            // process the node
            if (null != nextNode.getFormatListener()) {

                nextNode.getFormatListener().formatProcess(nextNode, options,
                        isCloneData() ? clonedOptData.clone() : clonedOptData);

            } else {
                AbstractProcessorUtils.processParseTreeNode(nextNode, options,
                        isCloneData() ? clonedOptData.clone() : clonedOptData, addPreSpace);

            }

            nextNode = nextNode.getNextNode();
        }

    }

    /**
     * return true is data is cloned.
     *
     * @return true, if is clone data
     */
    public boolean isCloneData() {
        return false;
    }

    /**
     * return options of proceesed data.
     *
     * @param selectAstNode selected node to process
     * @param pData options data while process
     * @param options options for format
     * @return returns the options data
     */
    public OptionsProcessData getOptionsProcessData(E selectAstNode, OptionsProcessData pData, FmtOptionsIf options) {
        return pData;
    }

    /**
     * return start node.
     *
     * @param selectAstNode the select ast node
     * @return the start node
     */
    public abstract TParseTreeNode getStartNode(E selectAstNode);

    /**
     * add listener to format process.
     *
     * @param parseNode node to process
     * @param processListener listers to add to prase node
     */
    protected void addFormatProcessListener(TParseTreeNode parseNode, IFormarProcessorListener processListener) {
        if (null != parseNode) {
            parseNode.setFormatListener(processListener);
        }
    }

    /**
     * return keyword with max length.
     *
     * @param selectAstNode node to process
     * @param options otpions for format
     * @return the max keyword width
     */
    protected int getMaxKeywordWidth(TCustomSqlStatement selectAstNode, FmtOptionsIf options) {
        return StmtKeywordAlignUtil.getMaxKeywordWidth(selectAstNode, options);
    }

    /**
     * process data.
     *
     * @param pData options data while process
     * @param maxKeywordWidth max width to format
     * @return the new options data bean
     */
    protected OptionsProcessData getNewOptionsDataBean(OptionsProcessData pData, int maxKeywordWidth) {
        OptionsProcessData lOptionsProcessData = new OptionsProcessData();

        lOptionsProcessData.setMaxKeywordLength(maxKeywordWidth);
        lOptionsProcessData.setOffSet(pData.getOffSet());
        lOptionsProcessData.setParentOffSet(pData.getParentOffSet());
        lOptionsProcessData.setPutStmtNewLine(pData.isPutStmtNewLine());
        return lOptionsProcessData;
    }

    /**
     * set option process data.
     *
     * @param clonedOptData options data while process
     */
    protected void setOptionsProcessData(OptionsProcessData clonedOptData) {

    }

}
