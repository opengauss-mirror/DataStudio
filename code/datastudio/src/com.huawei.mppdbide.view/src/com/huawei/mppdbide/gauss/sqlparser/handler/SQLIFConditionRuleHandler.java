/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * 
 * Title: SQLIFConditionRuleHandler
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
public class SQLIFConditionRuleHandler extends AbstractRuleHandler {

    /**
     * Gets the end token type.
     *
     * @param currentBlock the current block
     * @return the end token type
     */
    @Override
    public int getEndTokenType(ScriptBlockInfo currentBlock) {

        return SQLTokenConstants.T_SQL_NONEXIST;
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
     * Checks if is block end.
     *
     * @param currentBlock the current block
     * @param token the token
     * @return true, if is block end
     */
    public boolean isBlockEnd(ScriptBlockInfo currentBlock, SQLToken token) {

        if (isBlockEndWithOutOptionalToken(currentBlock, token)) {
            currentBlock.setNextOptTokenToEnd(SQLTokenConstants.T_SQL_DELIMITER);
            return true;
        }

        return false;
    }

    /**
     * Checks if is block end with out optional token.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is block end with out optional token
     */
    public boolean isBlockEndWithOutOptionalToken(ScriptBlockInfo curBlock, SQLToken token) {
        return null != curBlock && token.getType() == SQLTokenConstants.T_SQL_DDL_CONTROL_IF
                && SQLFoldingConstants.SQL_KEYWORK_END.equalsIgnoreCase(curBlock.getLastToken());
    }

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {

        if (getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DDL_CONTROL_ELSIF
                || getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DDL_CONTROL_ELSE) {
            return false;
        }

        return true;
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