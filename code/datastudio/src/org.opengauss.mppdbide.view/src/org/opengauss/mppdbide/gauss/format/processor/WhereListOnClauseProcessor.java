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

package org.opengauss.mppdbide.gauss.format.processor;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: WhereListOnClauseProcessor
 *
 * @since 3.0.0
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
