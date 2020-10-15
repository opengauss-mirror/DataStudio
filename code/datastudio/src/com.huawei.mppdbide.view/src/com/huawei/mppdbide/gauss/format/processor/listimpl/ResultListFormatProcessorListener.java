/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.ColumnlistCommaProcessor;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TResultColumnList;

/**
 * 
 * Title: ResultListFormatProcessorListener
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
public class ResultListFormatProcessorListener implements IFormarProcessorListener {

    private boolean addPreSpace = true;
    private boolean semiColonList = false;

    /** 
     * return true is SemiColon List is there 
     * 
     * @return isSemiColonList the flag
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
     * Format process.
     *
     * @param nextNode the next node
     * @param options the options
     * @param pData the data
     */
    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        TResultColumnList expressionList = (TResultColumnList) nextNode;

        // add the expressionList

        ColumnlistCommaProcessor lColumnlistCommaProcessor = getColumnListProcessor();
        lColumnlistCommaProcessor.setOptions(options);
        lColumnlistCommaProcessor.setAddPreSpace(addPreSpace);
        lColumnlistCommaProcessor.setSemiColonList(semiColonList);
        lColumnlistCommaProcessor.process(expressionList, options, pData);

    }

    /**
     * Gets the column list processor.
     *
     * @return the column list processor
     */
    protected ColumnlistCommaProcessor getColumnListProcessor() {
        return new ColumnlistCommaProcessor();
    }

    /**
     * Checks if is adds the pre space.
     *
     * @return true, if is adds the pre space
     */
    public boolean isAddPreSpace() {
        return addPreSpace;
    }

    /**
     * Sets the adds the pre space.
     *
     * @param addPreSpace the new adds the pre space
     */
    public void setAddPreSpace(boolean addPreSpace) {
        this.addPreSpace = addPreSpace;
    }

}
