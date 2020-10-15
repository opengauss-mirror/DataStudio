/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import java.util.List;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.SQLBracketParamData;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.RuleBean;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: AbstractDMLRuleHandler Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class AbstractDMLRuleHandler extends AbstractRuleHandler {

    /**
     * Checks if is stop script block.
     *
     * @param curBlock the cur block
     * @param ruleHandlarByToken the rule handlar by token
     * @return true, if is stop script block
     */
    @Override
    public boolean isStopScriptBlock(ScriptBlockInfo curBlock, AbstractRuleHandler ruleHandlarByToken) {

        if (ruleHandlarByToken instanceof AbstractDMLRuleHandler) {
            return false;
        }

        return true;

    }

    /**
     * Gets the end token type.
     *
     * @param curBlockMain the cur block main
     * @return the end token type
     */
    @Override
    public int getEndTokenType(ScriptBlockInfo curBlockMain) {
        return SQLTokenConstants.T_SQL_DELIMITER;
    }

    /**
     * Handle block end.
     *
     * @param curBlockMain the cur block main
     * @param lretList the lret list
     * @param token the token
     * @param lRuleBean the l rule bean
     */
    public void handleBlockEnd(ScriptBlockInfo curBlockMain, List<SQLScriptElement> lretList, SQLToken token,
            RuleBean lRuleBean) {

        ScriptBlockInfo curBlock = curBlockMain;
        while (null != curBlock) {

            // if not nested then end and start new block
            curBlock.getAbstractRuleHandler().endScriptBlock(curBlock, lretList, curBlock.getLastTokenOffSet(),
                    curBlock.getLastTokenlenght());

            // check how many parents blocks need to end the script

            if (!curBlock.getAbstractRuleHandler().isStopParentScriptBlock(curBlock, token)) {
                break;
            }

            curBlock = curBlock.getParent();

        }

        lRuleBean.setScriptBlockInfo(null != curBlock ? curBlock.getParent() : null);

    }

    /**
     * Checks if is stop parent script block.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is stop parent script block
     */
    public boolean isStopParentScriptBlock(ScriptBlockInfo curBlock, IToken token) {

        if (null != curBlock.getParent() && curBlock.getAbstractRuleHandler() instanceof AbstractDMLRuleHandler
                && (curBlock.getParent().getAbstractRuleHandler() instanceof AbstractDMLRuleHandler)) {
            return true;
        }

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

        if (curBlock.isNested() && null != getSQLToken(token)
                && (getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DML_SELECT)) {
            return true;
        }

        return false;
    }

    /**
     * Handle nested block script create.
     *
     * @param curBlock the cur block
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param tokenOffset the token offset
     */
    public void handleNestedBlockScriptCreate(ScriptBlockInfo curBlock, ISQLTokenData token, RuleBean lRuleBean,
            int tokenOffset) {
        String lastKnownToken = curBlock.getLastKnownToken();

        if (SQLFoldingConstants.SQL_BRACKET_START.equalsIgnoreCase(lastKnownToken)
                || SQLFoldingConstants.SQL_KEYWORD_UNION.equalsIgnoreCase(lastKnownToken)
                || SQLFoldingConstants.SQL_KEYWORD_INTERSECT.equalsIgnoreCase(lastKnownToken)
                || SQLFoldingConstants.SQL_KEYWORD_EXCEPT.equalsIgnoreCase(lastKnownToken)
                || SQLFoldingConstants.SQL_KEYWORD_MINUS.equalsIgnoreCase(lastKnownToken)) {
            ScriptBlockInfo scriptBlock = createScriptBlock(curBlock, token, tokenOffset, true);
            SQLBracketParamData lSQLBracketParamData = new SQLBracketParamData();

            scriptBlock.setBracketParamData(lSQLBracketParamData);

            if (null != curBlock.getBracketParamData()) {
                curBlock.getBracketParamData().decrStartEndTokenCounter();
            }
            scriptBlock.setStartOffSetForBlock(tokenOffset);

            lRuleBean.setScriptBlockInfo(scriptBlock);

        } else {
            super.handleNestedBlockScriptCreate(curBlock, token, lRuleBean, tokenOffset);
        }
    }

    /**
     * Handle parent block end.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @param token the token
     * @param ruleManager the rule manager
     * @param lRuleBean the l rule bean
     */
    public void handleParentBlockEnd(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, SQLToken token,
            ISQLTokenData ruleManager, RuleBean lRuleBean) {
        ScriptBlockInfo parentBlock = curBlock.getParent();

        while (null != parentBlock) {

            if ((parentBlock.getAbstractRuleHandler() instanceof AbstractDMLRuleHandler
                    || (parentBlock.getAbstractRuleHandler() instanceof SQLCursorRuleHandler))
                    && isBlockEnd(parentBlock, token)) {
                parentBlock.getAbstractRuleHandler().endScriptBlock(parentBlock, lretList, ruleManager.getTokenOffset(),
                        ruleManager.getTokenLength());
            } else {
                break;
            }

            parentBlock = parentBlock.getParent();

        }

        lRuleBean.setScriptBlockInfo(parentBlock);
    }

    /**
     * Handle other end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param lretList the lret list
     * @param ruleManager the rule manager
     */
    public void handleOtherEnd(ScriptBlockInfo curBlock, SQLToken token, RuleBean lRuleBean,
            List<SQLScriptElement> lretList, ISQLTokenData ruleManager) {
        manageCaseStmt(curBlock, token);
        // get from the parent that the child is sub query with '(' then handle
        // other '(' start and ')' end
        SQLBracketParamData bracketParamData = curBlock.getBracketParamData();
        if (null != bracketParamData) {

            if (token.getType() == SQLTokenConstants.T_SQL_BRACKET_BEGIN) {
                bracketParamData.incrStartEndTokenCounter();
            } else if (token.getType() == SQLTokenConstants.T_SQL_BRACKET_END) {
                bracketParamData.decrStartEndTokenCounter();
            }

            if (bracketParamData.getStartEndTokenCounter() < 0) {
                // stop the script

                endScriptBlock(curBlock, lretList, curBlock.getLastTokenOffSet(), curBlock.getLastTokenlenght());
                removeLastTokenAndAddToParent(curBlock.getParent(), ruleManager, curBlock);
                lRuleBean.setScriptBlockInfo(curBlock.getParent());

                return;
            }

        } else if (null != curBlock.getParent() && curBlock.getParent() instanceof DMLParamScriptBlockInfo
                && token.getType() == SQLTokenConstants.T_SQL_KEYWORK_LANGUAGE
                && SQLFoldingConstants.SQL_DOUBLE_DOLLER.equalsIgnoreCase(curBlock.getLastKnownToken())) {

            setParentBlockInfo(curBlock);

            endScriptBlock(curBlock, lretList, curBlock.getLastKnownTokenOffSet(), curBlock.getLastKnownTokenlenght());
            lRuleBean.setScriptBlockInfo(curBlock.getParent());
            return;

        } else if (null != curBlock.getParent()
                && (isSQLBlockEnd(curBlock, token) || isSQLIfEnd(curBlock, token) || isSQLElseIfEnd(curBlock, token))) {

            curBlock.getParent().setInvokeParent(true);

            endScriptBlock(curBlock, lretList, curBlock.getLastTokenOffSet(), curBlock.getLastTokenlenght());
            lRuleBean.setScriptBlockInfo(curBlock.getParent());
            return;
        }

        handleStmtCustomEnd(curBlock, token, lRuleBean, lretList, ruleManager);

    }

    /**
     * Checks if is case stmt open.
     *
     * @param curBlock the cur block
     * @return true, if is case stmt open
     */
    private boolean isCaseStmtOpen(ScriptBlockInfo curBlock) {

        SQLBracketParamData caseParamData = curBlock.getCaseParamData();

        if (null == caseParamData) {
            return false;
        }

        return caseParamData.getStartEndTokenCounter() > 0;
    }

    private void manageCaseStmt(ScriptBlockInfo curBlock, SQLToken token) {

        SQLBracketParamData caseParamData = curBlock.getCaseParamData();

        if (token.getType() == SQLTokenConstants.T_SQL_KEYWORK_CASE) {
            if (null == caseParamData) {

                caseParamData = new SQLBracketParamData();
                curBlock.setCaseParamData(caseParamData);
            }

            caseParamData.incrStartEndTokenCounter();

        } else if (null != caseParamData && token.getType() == SQLTokenConstants.T_SQL_BLOCK_END) {
            caseParamData.decrStartEndTokenCounter();

        }
    }

    private boolean isSQLBlockEnd(ScriptBlockInfo curBlock, SQLToken token) {
        return curBlock.getParent().getAbstractRuleHandler() instanceof SQLBlockRuleHandler
                && token.getType() == SQLTokenConstants.T_SQL_BLOCK_END;
    }

    private boolean isSQLIfEnd(ScriptBlockInfo curBlock, SQLToken token) {
        return curBlock.getParent().getAbstractRuleHandler() instanceof SQLIFConditionRuleHandler
                && isElseIfBlock(token);
    }

    /**
     * Checks if is else if block.
     *
     * @param token the token
     * @return true, if is else if block
     */
    public boolean isElseIfBlock(SQLToken token) {
        return token.getType() == SQLTokenConstants.T_SQL_BLOCK_END
                || token.getType() == SQLTokenConstants.T_SQL_DDL_CONTROL_ELSE
                || token.getType() == SQLTokenConstants.T_SQL_DDL_CONTROL_ELSIF;
    }

    private boolean isSQLElseIfEnd(ScriptBlockInfo curBlock, SQLToken token) {
        return curBlock.getParent().getAbstractRuleHandler() instanceof SQLELSIFConditionRuleHandler
                && isElseIfBlock(token);
    }

    /**
     * Sets the parent block info.
     *
     * @param curBlock the new parent block info
     */
    public void setParentBlockInfo(ScriptBlockInfo curBlock) {

        ScriptBlockInfo parentBlock = curBlock.getParent();
        parentBlock.setInvokeParent(true);

        if (parentBlock instanceof DMLParamScriptBlockInfo) {
            ((DMLParamScriptBlockInfo) parentBlock).setEndStmtFound(true);
        }
    }

    /**
     * Handle stmt custom end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param lretList the lret list
     * @param ruleManager the rule manager
     */
    protected void handleStmtCustomEnd(ScriptBlockInfo curBlock, SQLToken token, RuleBean lRuleBean,
            List<SQLScriptElement> lretList, ISQLTokenData ruleManager) {
        if (isInMergeStmt(curBlock, token)) {
            unreadAndEndScriptBlock(curBlock, lRuleBean, lretList, ruleManager);
        }
    }

    /**
     * unread and End the ScriptBlock.
     *
     * @param curBlock the cur block
     * @param lRuleBean the l rule bean
     * @param lretList the lret list
     * @param ruleManager the rule manager
     */
    protected void unreadAndEndScriptBlock(ScriptBlockInfo curBlock, RuleBean lRuleBean,
            List<SQLScriptElement> lretList, ISQLTokenData ruleManager) {
        // 1.unread for the token size
        if (null != this.getRuleScanner()) {
            int tokenLength = ruleManager.getTokenLength();
            while (tokenLength > 0) {
                this.getRuleScanner().unread();
                tokenLength--;
            }
        }

        // 2.remove last token
        if (!curBlock.getAllTokenList().isEmpty()) {
            curBlock.getAllTokenList().remove(curBlock.getAllTokenList().size() - 1);
        }

        endScriptBlock(curBlock, lretList, curBlock.getLastTokenOffSet(), curBlock.getLastTokenlenght());
        lRuleBean.setScriptBlockInfo(curBlock.getParent());
        lRuleBean.setPreviousScriptBlock(curBlock);
    }

    /**
     * returns the select,insert or update is in Merge stmt or not.
     * 
     * @param curBlock the cur block
     * @param token the token
     * @return weather it is in merge stmt or not in merge stmt
     */
    protected boolean isInMergeStmt(ScriptBlockInfo curBlock, SQLToken token) {
        return null != curBlock.getParent() && curBlock.getParent().getTokenType() == SQLTokenConstants.T_SQL_MERGE
                && SQLTokenConstants.T_SQL_WHEN == token.getType();
    }

    /**
     * Checks if is rule handler valid.
     *
     * @param currentRuleHandler the current rule handler
     * @param curBlock the cur block
     * @return true, if is rule handler valid
     */
    public boolean isRuleHandlerValid(RuleHandlerIf currentRuleHandler, ScriptBlockInfo curBlock, SQLToken token) {

        if (currentRuleHandler instanceof SQLELSEConditionRuleHandler && isCaseStmtOpen(curBlock)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if is case stmt nested.
     *
     * @param token the token
     * @return true, if is case stmt nested
     */
    protected boolean isCaseStmtNested(IToken token) {
        return null != getSQLToken(token) && getSQLToken(token).getType() == SQLTokenConstants.T_SQL_KEYWORK_CASE;
    }

}
