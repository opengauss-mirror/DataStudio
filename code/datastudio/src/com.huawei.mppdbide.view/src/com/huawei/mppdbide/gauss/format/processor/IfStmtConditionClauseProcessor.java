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

import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;

/**
 * 
 * Title: WhereListOnClauseProcessor
 *
 * @since 3.0.0
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
