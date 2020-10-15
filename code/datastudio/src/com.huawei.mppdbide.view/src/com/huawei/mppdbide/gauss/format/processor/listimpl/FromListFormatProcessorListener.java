/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.ColumnlistCommaProcessor;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.from.TFromItemList;

/**
 * 
 * Title: FromListFormatProcessorListener
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
public class FromListFormatProcessorListener implements IFormarProcessorListener {

    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        TFromItemList fromItemList = (TFromItemList) nextNode;

        // add the expressionList

        if (fromItemList.isJoinStmt()) {
            FromJoinListProcessorNew lFromJoinListProcessor = new FromJoinListProcessorNew();
            lFromJoinListProcessor.setOptions(options);
            lFromJoinListProcessor.process(fromItemList, options, pData);
        } else {
            ColumnlistCommaProcessor lColumnlistCommaProcessor = new ColumnlistCommaProcessor();
            lColumnlistCommaProcessor.setOptions(options);
            lColumnlistCommaProcessor.process((TParseTreeNodeList) fromItemList, options, pData);
        }

    }

}
