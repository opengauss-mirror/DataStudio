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

import org.opengauss.mppdbide.gauss.sqlparser.SQLDDLTypeEnum;
import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.SQLToken;
import org.opengauss.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;

/**
 * 
 * Title: AbstractCreateHandler
 *
 * @since 3.0.0
 */
public class AbstractAlterHandler extends AbstractRuleHandler implements IPartialStmt {

    private AbstractAlterStmt abstractAlterStmt = new AbstractAlterStmt();

    /**
     * Instantiates a new abstract alter handler.
     */
    public AbstractAlterHandler() {

    }

    /**
     * Gets the end token type.
     *
     * @param curBlock the cur block
     * @return the end token type
     */
    @Override
    public int getEndTokenType(ScriptBlockInfo curBlock) {

        // to be done in the sub classes
        return getCreateStmt(getDMLParamScriptBlock(curBlock)).getEndTokenType();
    }

    /**
     * Gets the script block.
     *
     * @param parent the parent
     * @param token the token
     * @return the script block
     */
    protected ScriptBlockInfo getScriptBlock(ScriptBlockInfo parent, ISQLTokenData token) {
        return new DMLParamScriptBlockInfo(parent, token.getToken(), this);
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
        // to be done in the sub classes

        return false;
    }

    /**
     * Handle SQL stmt.
     *
     * @param curScriptBlock the cur script block
     */
    @Override
    public void handleSQLStmt(ScriptBlockInfo curScriptBlock) {

        DMLParamScriptBlockInfo curBlock = getDMLParamScriptBlock(curScriptBlock);
        String latestkeyword = null;
        if (curBlock != null) {
            latestkeyword = curBlock.getCurrentKnownToken();
        }

        if (curBlock != null && null == curBlock.getDdlType()) {
            if (SQLFoldingConstants.SQL_VIEW.equalsIgnoreCase(latestkeyword)) {
                curBlock.setDdlType(SQLDDLTypeEnum.VIEW);
            }
        }

    }

    /**
     * Handle partial stmt.
     *
     * @param curBlock the cur block
     */
    public void handlePartialStmt(ScriptBlockInfo curBlock) {

        handleSQLStmt(curBlock);

    }

    /**
     * Checks if is stop parent script block.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is stop parent script block
     */
    public boolean isStopParentScriptBlock(ScriptBlockInfo curBlock, IToken token) {

        return getCreateStmt(getDMLParamScriptBlock(curBlock)).isStopParentScriptBlock(curBlock, getSQLToken(token));
    }

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {
        return getCreateStmt(getDMLParamScriptBlock(curBlock)).isNested(curBlock, (SQLToken) token);
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
        return getCreateStmt(getDMLParamScriptBlock(curBlock)).isIgnoreByCurrentBlock(ruleHandlarByToken, curBlock,
                token);
    }

    /**
     * Checks if is block end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is block end
     */
    public boolean isBlockEnd(ScriptBlockInfo curBlock, SQLToken token) {
        return getCreateStmt(getDMLParamScriptBlock(curBlock)).isBlockEnd(curBlock, token);
    }

    private DMLParamScriptBlockInfo getDMLParamScriptBlock(ScriptBlockInfo curBlock) {
        if (curBlock instanceof DMLParamScriptBlockInfo) {
            return (DMLParamScriptBlockInfo) curBlock;
        }
        return null;
    }

    private AbstractAlterStmt getCreateStmt(DMLParamScriptBlockInfo curScriptBlock) {

        return RuleHandlerConfig.getInstance().getAlterStmt(curScriptBlock, abstractAlterStmt);

    }

}
