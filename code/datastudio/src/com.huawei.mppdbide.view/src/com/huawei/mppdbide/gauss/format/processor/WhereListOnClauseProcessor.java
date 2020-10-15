/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

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
public class WhereListOnClauseProcessor extends WhereListCommaProcessor {

    /**
     * return true if and/or is under where
     */
    protected boolean andOrUnderWhere(FmtOptionsIf options) {
        return true;
    }

    /**
     * return true if and/or is after expr
     */
    protected boolean andOrAfterExp(FmtOptionsIf options, OptionsProcessData pData) {
        return false;
    }

    /**
     * return true if can be splited at zero level
     */
    protected boolean splitAtZeroLevel(FmtOptionsIf options, OptionsProcessData pData) {
        return true;
    }

    /**
     * handles and/or under where clause
     */
    protected void handleAndOrUnderWhere(FmtOptionsIf options, int offset, int poffset, TSqlNode conSep) {
        int whereConOffSet = poffset + offset - conSep.getNodeText().length();
        StringBuilder lsb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        ProcessorUtils.fillSBWithIndendationChars(whereConOffSet, lsb, options);
        conSep.addPreText(lsb.toString());
    }

}
