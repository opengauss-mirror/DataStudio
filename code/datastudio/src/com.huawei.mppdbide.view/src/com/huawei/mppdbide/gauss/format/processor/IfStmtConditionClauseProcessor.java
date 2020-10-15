/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;

/**
 * 
 * Title: WhereListOnClauseProcessor
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
public class IfStmtConditionClauseProcessor extends WhereListCommaProcessor {

    /**
     * return true if and/or are under where
     */
    protected boolean andOrUnderWhere(FmtOptionsIf options) {
        return false;
    }

    /**
     * return true if and/or are after expr
     */
    protected boolean andOrAfterExp(FmtOptionsIf options, OptionsProcessData pData) {
        return options.andOrAfterExp(FormatItemsType.CONTROL_STRUCTURE);
    }

    /**
     * return true if can be split at zero level
     */
    protected boolean splitAtZeroLevel(FmtOptionsIf options, OptionsProcessData pData) {
        return options.splitAtZeroLevel(FormatItemsType.CONTROL_STRUCTURE);
    }

}
