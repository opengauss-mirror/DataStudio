/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.processor.ColumnlistColonProcessor;
import com.huawei.mppdbide.gauss.format.processor.ColumnlistCommaProcessor;

/**
 * Title: ColonResultListFormatProcessorListener
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
 */

public class ColonResultListFormatProcessorListener extends ResultListFormatProcessorListener {

    /**
     * Gets the column list processor.
     *
     * @return the column list processor
     */
    protected ColumnlistCommaProcessor getColumnListProcessor() {
        return new ColumnlistColonProcessor();
    }
}
