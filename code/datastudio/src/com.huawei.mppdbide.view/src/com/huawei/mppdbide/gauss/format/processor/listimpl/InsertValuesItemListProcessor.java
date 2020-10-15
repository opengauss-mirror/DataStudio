/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractListProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertValuesNodeItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;

/**
 * 
 * Title: InsertValuesItemListProcessor
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
public class InsertValuesItemListProcessor extends AbstractListProcessor {

    /**
     * Adds the list item listener.
     *
     * @param tAbstractListItem the t abstract list item
     */
    protected void addListItemListener(TAbstractListItem tAbstractListItem) {

        TInsertValuesNodeItem fromItem = (TInsertValuesNodeItem) tAbstractListItem;

        addFormatProcessListener(fromItem.getStartInsertBracket(), new NewlineWithIndentFormatProcessorListener());

        ResultListFormatProcessorListener processListener = new ResultListFormatProcessorListener();
        processListener.setAddPreSpace(false);
        addFormatProcessListener(fromItem.getValueItemList(), processListener);

        addFormatProcessListener(fromItem.getInsertEndBracket(), new NoPreTextFormatProcessorListener());

        addFormatProcessListener(fromItem.getSeperator(), new NoPreTextFormatProcessorListener());

    }

    /**
     * Before item process.
     *
     * @param pData the data
     * @param currentData the current data
     */
    protected void beforeItemProcess(OptionsProcessData pData, OptionsProcessData currentData) {

        currentData.setOffSet(pData.getOffSet());
        currentData.setParentOffSet(pData.getParentOffSet());
        currentData.setPreIndentOffSet(0);

    }

}
