/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessorUtils;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: NewlineWithIndentFormatProcessorListener
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
public class NewlineWithIndentFormatProcessorListener implements IFormarProcessorListener {

    /**
     * Format process.
     *
     * @param nextNode the next node
     * @param options the options
     * @param pData the data
     */
    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        changeOptionsData(pData);
        ProcessorUtils.addNewLineBeforeWithindent(nextNode, pData, options);
        AbstractProcessorUtils.processParseTreeNode(nextNode, options, pData, false);

    }

    /**
     * Change options data.
     *
     * @param pData the data
     */
    protected void changeOptionsData(OptionsProcessData pData) {
        if (pData.getPreIndentOffSet() == 0) {
            pData.setOffSet(pData.getParentOffSet());
        } else {
            pData.setOffSet(pData.getPreIndentOffSet());
        }
    }

}
