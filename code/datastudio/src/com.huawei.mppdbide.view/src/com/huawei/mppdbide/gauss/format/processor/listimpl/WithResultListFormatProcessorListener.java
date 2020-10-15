/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.WithColumnlistCommaProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TResultColumnList;

/**
 * 
 * Title: WithResultListFormatProcessorListener
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
public class WithResultListFormatProcessorListener extends ResultListFormatProcessorListener {

    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        TResultColumnList expressionList = (TResultColumnList) nextNode;

        // add the expressionList

        WithColumnlistCommaProcessor lColumnlistCommaProcessor = new WithColumnlistCommaProcessor();
        lColumnlistCommaProcessor.setOptions(options);
        lColumnlistCommaProcessor.setAddPreSpace(isAddPreSpace());
        lColumnlistCommaProcessor.process(expressionList, options, pData);

    }

}
