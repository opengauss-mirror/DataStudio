/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.update.TUpdateSetValuesNodeItemList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: UpdateValuesListFormatProcessorListener
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
public class UpdateValuesListFormatProcessorListener implements IFormarProcessorListener {

    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        TUpdateSetValuesNodeItemList expressionList = (TUpdateSetValuesNodeItemList) nextNode;

        // add the expressionList

        UpdateSetValuesItemListProcessor lColumnlistCommaProcessor = new UpdateSetValuesItemListProcessor();
        lColumnlistCommaProcessor.setOptions(options);

        lColumnlistCommaProcessor.process(expressionList, options, pData);
    }

}