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
public class DropBlockRuleHandler extends AbstractRuleHandler {

    /**
     * Gets the end token type.
     *
     * @param currentBlock the current block
     * @return the end token type
     */
    @Override
    public int getEndTokenType(ScriptBlockInfo currentBlock) {

        return SQLTokenConstants.T_SQL_DELIMITER;
    }

    /**
     * Checks if is stop script block.
     *
     * @param currentBlock the current block
     * @param ruleHandlarByToken the rule handlar by token
     * @return true, if is stop script block
     */
    @Override
    public boolean isStopScriptBlock(ScriptBlockInfo currentBlock, AbstractRuleHandler ruleHandlarByToken) {
        return false;
    }

    /**
     * Checks if is nested.
     *
     * @param currentBlock the current block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo currentBlock, IToken token) {
        return false;
    }

    /**
     * Checks if is ignore by current block.
     *
     * @param ruleHandlarByToken the rule handlar by token
     * @param currentBlock the current block
     * @param token the token
     * @return true, if is ignore by current block
     */
    public boolean isIgnoreByCurrentBlock(AbstractRuleHandler ruleHandlarByToken, ScriptBlockInfo currentBlock,
            SQLToken token) {
        return SQLTokenConstants.T_SQL_DDL_CONTROL_IF == token.getType();
    }

}
