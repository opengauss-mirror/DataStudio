/*
 * Copyright: Huawei Technologies Co., Ltd. Copyright 2012-2019, All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: DeclareFieldsNewlineWithIndentFormatProcessorListener
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 04-Dec-2019]
 * @since 04-Dec-2019
 */

public class DeclareFieldsNewlineWithIndentFormatProcessorListener implements IFormarProcessorListener {

    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        pData.setOffSet(pData.getParentOffSet());
        ProcessorUtils.addNewLineBeforeWithindent(nextNode, pData, options);

        pData.setFormatItemsType(FormatItemsType.FIXED_ONEITEMPERLINE);

        ResultListFormatProcessorListener processListener = new ColonResultListFormatProcessorListener();
        processListener.setAddPreSpace(false);
        processListener.setSemiColonList(true);
        processListener.formatProcess(nextNode, options, pData);

    }

}
