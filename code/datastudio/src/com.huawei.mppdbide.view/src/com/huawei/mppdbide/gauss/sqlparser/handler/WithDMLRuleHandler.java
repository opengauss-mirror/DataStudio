/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLDMLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * Title: WithDMLRuleHandler Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class WithDMLRuleHandler extends AbstractDMLRuleHandler {

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {

        String lastKnownToken = curBlock.getLastKnownToken();

        if (token instanceof SQLDMLToken) {
            if (curBlock.isNested() && (SQLFoldingConstants.SQL_BRACKET_START.equalsIgnoreCase(lastKnownToken)
                    || SQLFoldingConstants.SQL_KEYWORD_UNION.equalsIgnoreCase(lastKnownToken)
                    || SQLFoldingConstants.SQL_KEYWORD_INTERSECT.equalsIgnoreCase(lastKnownToken)
                    || SQLFoldingConstants.SQL_KEYWORD_EXCEPT.equalsIgnoreCase(lastKnownToken)
                    || SQLFoldingConstants.SQL_KEYWORD_MINUS.equalsIgnoreCase(lastKnownToken))) {
                return true;
            } else if (curBlock.getAllowDMLWithoutBracket() < 1) {
                curBlock.incrAllowDMLWithoutBracket();
                return true;
            }

        }

        return false;
    }

}
