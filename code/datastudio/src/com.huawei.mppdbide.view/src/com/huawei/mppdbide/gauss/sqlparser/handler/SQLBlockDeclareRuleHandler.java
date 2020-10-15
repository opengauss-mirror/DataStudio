/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * 
 * Title: SQLBlockDeclareRuleHandler
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
