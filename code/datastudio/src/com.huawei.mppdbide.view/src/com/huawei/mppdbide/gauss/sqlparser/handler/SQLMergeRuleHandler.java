/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * Title: SQLBlockRuleHandler Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class SQLMergeRuleHandler extends AbstractDMLRuleHandler {
    /**
     * Gets the end token type.
     *
     * @param curBlock the cur block
     * @return the end token type
     */
    @Override
    public int getEndTokenType(ScriptBlockInfo curBlock) {
        return SQLTokenConstants.T_SQL_DELIMITER;
    }

    /**
     * Checks if is stop script block.
     *
     * @param curBlock the cur block
     * @param ruleHandlarByToken the rule handlar by token
     * @return true, if is stop script block
     */
    @Override
    public boolean isStopScriptBlock(ScriptBlockInfo curBlock, AbstractRuleHandler ruleHandlarByToken) {
        return false;
    }

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {
        return curBlock.isNested() && (getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DML_SELECT
                || getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DML_INSERT
                || getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DML_DELETE
                || getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DML_UPDATE);
    }
}
