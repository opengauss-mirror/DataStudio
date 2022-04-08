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
 * Title: SQLBlockDeclareRuleHandler
 *
 * @since 3.0.0
 */
public class SQLBlockDeclareRuleHandler extends AbstractRuleHandler {

    /**
     * Gets the end token type.
     *
     * @param curBlock the cur block
     * @return the end token type
     */
    @Override
    public int getEndTokenType(ScriptBlockInfo curBlock) {

        return SQLTokenConstants.T_SQL_NONEXIST;
    }

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {

        if (getSQLToken(token) != null && getSQLToken(token).getType() == SQLTokenConstants.T_SQL_BLOCK_BEGIN) {

            return false;

        }

        return true;
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
     * Checks if is block end by other block.
     *
     * @return true, if is block end by other block
     */
    public boolean isBlockEndByOtherBlock() {

        return true;
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

        return token.getType() == SQLTokenConstants.T_SQL_DML_WITH;
    }

    /**
     * Gets the last tokenlenght.
     *
     * @param curBlock the cur block
     * @return the last tokenlenght
     */
    protected int getLastTokenlenght(ScriptBlockInfo curBlock) {
        return (curBlock.getPreNewlineTokenOffSet() == 0 && curBlock.getPreNewlineTokenlenght() == 0)
                ? curBlock.getAnyTokenlenght()
                : curBlock.getPreNewlineTokenlenght();
    }

    /**
     * Gets the last token off set.
     *
     * @param curBlock the cur block
     * @return the last token off set
     */
    protected int getLastTokenOffSet(ScriptBlockInfo curBlock) {
        return (curBlock.getPreNewlineTokenOffSet() == 0 && curBlock.getPreNewlineTokenlenght() == 0)
                ? curBlock.getAnyTokenOffSet()
                : curBlock.getPreNewlineTokenOffSet();
    }

}
