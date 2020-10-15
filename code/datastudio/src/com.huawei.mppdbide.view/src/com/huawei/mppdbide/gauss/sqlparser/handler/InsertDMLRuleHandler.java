/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * 
 * Title: InsertDMLRuleHandler
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class InsertDMLRuleHandler extends AbstractDMLRuleHandler {

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {

        String lastKnownToken = curBlock.getLastKnownToken();
        if (curBlock.isNested() && getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DML_SELECT) {
            if ((curBlock.getLastKnownTokenList().contains(SQLFoldingConstants.SQL_KEYWORK_VALUES)
                    || curBlock.getLastKnownTokenList().contains(SQLFoldingConstants.SQL_KEYWORK_VALUES_UPPER))) {
                if (SQLFoldingConstants.SQL_BRACKET_START.equalsIgnoreCase(lastKnownToken)) {
                    return true;
                }
                return false;
            }
            return true;
        } else if (isCaseStmtNested(token)) {
            return true;
        }

        return false;
    }

}
