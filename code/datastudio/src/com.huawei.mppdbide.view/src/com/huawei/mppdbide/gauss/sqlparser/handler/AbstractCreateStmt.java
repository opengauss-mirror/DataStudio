/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: AbstractCreateStmt Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class AbstractCreateStmt<T> {

    /**
     * Gets the end token type.
     *
     * @return the end token type
     */
    public int getEndTokenType() {
        return SQLTokenConstants.T_SQL_DELIMITER;
    }

    /**
     * Checks if is stop parent script block.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is stop parent script block
     */
    public boolean isStopParentScriptBlock(T curBlock, SQLToken token) {

        return false;
    }

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(T curBlock, SQLToken token) {
        return false;
    }

    /**
     * Checks if is block end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is block end
     */
    public boolean isBlockEnd(T curBlock, SQLToken token) {
        return null != curBlock && token.getType() == getEndTokenType();
    }

    /**
     * Checks if is ignore by current block.
     *
     * @param ruleHandlarByToken the rule handlar by token
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is ignore by current block
     */
    public boolean isIgnoreByCurrentBlock(AbstractRuleHandler ruleHandlarByToken, ScriptBlockInfo curBlock,
            SQLToken token) {
        return SQLTokenConstants.T_SQL_DDL_CONTROL_IF == token.getType()
                || SQLTokenConstants.T_SQL_DML_WITH == token.getType();
    }

    /**
     * Checks if is ignore by current block.
     * 
     * @param currentRuleHandler the rule handlar by token
     * @param curBlock the cur block
     * @param lToken the token
     * @return true, if is ignore by current block
     */
    public boolean isRuleHandlerValid(RuleHandlerIf currentRuleHandler, ScriptBlockInfo curBlock, SQLToken lToken) {
        return true;
    }

}
