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

package org.opengauss.mppdbide.gauss.sqlparser.handler;

import org.eclipse.jface.text.rules.IToken;

import org.opengauss.mppdbide.gauss.sqlparser.SQLToken;
import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * 
 * Title: SQLBlockRuleHandler
 *
 * @since 3.0.0
 */
public class PermissionBlockRuleHandler extends AbstractRuleHandler {

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
        return false;
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
                || SQLTokenConstants.T_SQL_DDL_CREATE == token.getType()
                || SQLTokenConstants.T_SQL_DML_SELECT == token.getType()
                || SQLTokenConstants.T_SQL_DML_INSERT == token.getType()
                || SQLTokenConstants.T_SQL_DML_UPDATE == token.getType()
                || SQLTokenConstants.T_SQL_DML_DELETE == token.getType()
                || SQLTokenConstants.T_SQL_DML_WITH == token.getType()
                || SQLTokenConstants.T_SQL_DML_TRUNCATE == token.getType()
                || SQLTokenConstants.T_SQL_DDL_ALTER == token.getType();
    }

}
