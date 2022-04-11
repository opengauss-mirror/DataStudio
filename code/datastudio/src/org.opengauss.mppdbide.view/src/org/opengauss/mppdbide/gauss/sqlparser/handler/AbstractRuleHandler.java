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

import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IToken;

import org.opengauss.mppdbide.gauss.sqlparser.SQLToken;
import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.bean.ScriptBlockInfoImpl;
import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.RuleBean;
import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.SQLQueryPosition;
import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenManager;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLTokenFactory;
import org.opengauss.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: AbstractRuleHandler
 *
 * @since 3.0.0
 */
public abstract class AbstractRuleHandler implements RuleHandlerIf, Cloneable {

    private SQLStmtTokenManager sqlStmtTokenManager = null;

    private BufferedRuleBasedScanner ruleScanner = null;

    /**
     * Gets the sql stmt token manager.
     *
     * @return the sql stmt token manager
     */
    public SQLStmtTokenManager getSqlStmtTokenManager() {
        return sqlStmtTokenManager;
    }

    /**
     * Sets the sql stmt token manager.
     *
     * @param sqlStmtTokenManager the new sql stmt token manager
     */
    public void setSqlStmtTokenManager(SQLStmtTokenManager sqlStmtTokenManager) {
        this.sqlStmtTokenManager = sqlStmtTokenManager;
    }

    /**
     * Gets the rule scanner.
     *
     * @return the rule scanner
     */
    public BufferedRuleBasedScanner getRuleScanner() {
        return ruleScanner;
    }

    /**
     * Sets the rule scanner.
     *
     * @param ruleScanner the new rule scanner
     */
    public void setRuleScanner(BufferedRuleBasedScanner ruleScanner) {
        this.ruleScanner = ruleScanner;
    }

    /**
     * Handle.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @param token the token
     * @param ruleManager the rule manager
     * @param ruleHandlarByToken the rule handlar by token
     * @return the rule bean
     */
    public RuleBean handle(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, IToken token,
            ISQLTokenData ruleManager, AbstractRuleHandler ruleHandlarByToken) {
        RuleBean lRuleBean = new RuleBean();
        lRuleBean.setScriptBlockInfo(curBlock);

        // for partial blocks. if block started partially then here we need to
        // handle.
        // create stmt from the first word we don't know what is the exact stmt.
        // we will parse till we find the exact stmt and then move on to the
        // isBlockEnd

        handlePartialStmt(curBlock);

        if (token instanceof SQLToken) {
            SQLToken lSQLToken = (SQLToken) token;
            if (isBlockEnd(curBlock, lSQLToken)) {
                if (curBlock.getNextOptTokenToEnd() == SQLTokenConstants.T_SQL_UNKNOWN) {
                    endScriptStrategy(curBlock, lretList, lSQLToken, ruleManager, lRuleBean);
                }
            } else if (null != ruleHandlarByToken) {
                // if this can have nested blocks.
                handleNestedBlock(lretList, ruleManager, lRuleBean, ruleHandlarByToken, ruleManager.getTokenOffset());
            } else {
                handleOtherEnd(curBlock, lSQLToken, lRuleBean, lretList, ruleManager);
            }
        }
        return lRuleBean;
    }

    /**
     * Gets the SQL token.
     *
     * @param token the token
     * @return the SQL token
     */
    public SQLToken getSQLToken(IToken token) {

        if (token instanceof SQLToken) {
            return (SQLToken) token;
        }

        return null;
    }

    /**
     * End script strategy.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @param token the token
     * @param ruleManager the rule manager
     * @param lRuleBean the l rule bean
     */
    public void endScriptStrategy(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, SQLToken token,
            ISQLTokenData ruleManager, RuleBean lRuleBean) {
        endScriptBlock(curBlock, lretList, ruleManager.getTokenOffset(), ruleManager.getTokenLength());

        // need to check the parent applicability for closure. this is
        // required for when select end with ; insert also should end.

        handleParentBlockEnd(curBlock, lretList, token, ruleManager, lRuleBean);

        // stop all the parent DML scripts in case of ; end
    }

    /**
     * End optional script.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @param token the token
     * @param ruleManager the rule manager
     * @return the rule bean
     */
    public RuleBean endOptionalScript(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, SQLToken token,
            ISQLTokenData ruleManager) {
        RuleBean lRuleBean = new RuleBean();
        lRuleBean.setScriptBlockInfo(curBlock);

        endScriptStrategy(curBlock, lretList, token, ruleManager, lRuleBean);
        return lRuleBean;

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
        lRuleBean.setScriptBlockInfo(curBlock.getParent());
    }

    /**
     * Creates the script block.
     *
     * @param parent the parent
     * @param token the token
     * @param tokenOffset the token offset
     * @return the script block info
     */
    public ScriptBlockInfo createScriptBlock(ScriptBlockInfo parent, ISQLTokenData token, int tokenOffset) {
        ScriptBlockInfo scriptBlock = getScriptBlock(parent, token);
        scriptBlock.setStartOffSetForBlock(tokenOffset);
        return scriptBlock;
    }

    /**
     * Creates the script block.
     *
     * @param parent the parent
     * @param token the token
     * @param tokenOffset the token offset
     * @param removeTokenFromParent the remove token from parent
     * @return the script block info
     */
    public ScriptBlockInfo createScriptBlock(ScriptBlockInfo parent, ISQLTokenData token, int tokenOffset,
            boolean removeTokenFromParent) {
        ScriptBlockInfo scriptBlock = createScriptBlock(parent, token, tokenOffset);
        if (removeTokenFromParent) {
            removeLastTokenAndAddToChild(parent, token, scriptBlock);
        }
        return scriptBlock;
    }

    /**
     * remove the last token and add to child.
     *
     * @param parent parent script block
     * @param token current token data
     * @param scriptBlock current script block
     */
    public void removeLastTokenAndAddToChild(ScriptBlockInfo parent, ISQLTokenData token, ScriptBlockInfo scriptBlock) {
        scriptBlock.addSQLTokenData(token);
        if (!parent.getAllTokenList().isEmpty()) {
            parent.getAllTokenList().remove(parent.getAllTokenList().size() - 1);
        }
    }

    /**
     * remove the last token and add to parent.
     *
     * @param parent parent script block
     * @param token current token data
     * @param scriptBlock current script block
     */
    public void removeLastTokenAndAddToParent(ScriptBlockInfo parent, ISQLTokenData token,
            ScriptBlockInfo scriptBlock) {
        parent.addSQLTokenData(token);
        if (!parent.getAllTokenList().isEmpty()) {
            scriptBlock.getAllTokenList().remove(scriptBlock.getAllTokenList().size() - 1);
        }
    }

    /**
     * Gets the script block.
     *
     * @param parent the parent
     * @param token the token
     * @return the script block
     */
    protected ScriptBlockInfo getScriptBlock(ScriptBlockInfo parent, ISQLTokenData token) {
        return new ScriptBlockInfoImpl(parent, token.getToken(), this);
    }

    /**
     * Handle nested block.
     *
     * @param lretList the lret list
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param ruleHandlarByToken the rule handlar by token
     * @param tokenOffset the token offset
     */
    public void handleNestedBlock(List<SQLScriptElement> lretList, ISQLTokenData token, RuleBean lRuleBean,
            AbstractRuleHandler ruleHandlarByToken, int tokenOffset) {
        ScriptBlockInfo curBlock = lRuleBean.getScriptBlockInfo();
        if (isIgnore(ruleHandlarByToken, curBlock, (SQLToken) token.getToken())) {
            // if nested then find end or child
            return;
        } else if (isNested(curBlock, token.getToken())) {
            // if nested then find end or child
            ruleHandlarByToken.handleNestedBlockScriptCreate(curBlock, token, lRuleBean, tokenOffset);
            handlePartialStmt(lRuleBean.getScriptBlockInfo());
        } else {
            handleBlockEnd(lretList, token, lRuleBean, ruleHandlarByToken, tokenOffset);
        }
    }

    /**
     * Checks if is ignore.
     *
     * @param ruleHandlarByToken the rule handlar by token
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is ignore
     */
    public boolean isIgnore(AbstractRuleHandler ruleHandlarByToken, ScriptBlockInfo curBlock, SQLToken token) {
        return isIgnoreByCurrentBlock(ruleHandlarByToken, curBlock, token)
                || ruleHandlarByToken.isIgnoreByNextToken(this, curBlock, token);
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
        return false;
    }

    /**
     * Checks if is ignore by next token.
     *
     * @param ruleHandlarByToken the rule handlar by token
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is ignore by next token
     */
    public boolean isIgnoreByNextToken(AbstractRuleHandler ruleHandlarByToken, ScriptBlockInfo curBlock,
            SQLToken token) {
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
        ScriptBlockInfo createScriptBlock = createScriptBlock(curBlock, token, tokenOffset, true);
        lRuleBean.setScriptBlockInfo(createScriptBlock);
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
    }

    /**
     * Handle partial stmt.
     *
     * @param curBlock the cur block
     */
    public void handlePartialStmt(ScriptBlockInfo curBlock) {

    }

    /**
     * Checks if is block end by other block.
     *
     * @return true, if is block end by other block
     */
    public boolean isBlockEndByOtherBlock() {

        return false;
    }

    /**
     * Handle block end.
     *
     * @param lretList the lret list
     * @param sqlTokenData the sql token data
     * @param lRuleBean the l rule bean
     * @param ruleHandlarByToken the rule handlar by token
     * @param tokenOffset the token offset
     */
    public void handleBlockEnd(List<SQLScriptElement> lretList, ISQLTokenData sqlTokenData, RuleBean lRuleBean,
            AbstractRuleHandler ruleHandlarByToken, int tokenOffset) {
        ScriptBlockInfo curBlock = lRuleBean.getScriptBlockInfo();

        // check if the new block is in the new line, if new line then can end
        // the block

        if (!isBlockEndByOtherBlock() && !isEndBlockByThisToken(sqlTokenData.getToken(), curBlock)) {
            if (curBlock.getRecentNewLineCount() < 3) {
                return;
            }
        }

        if (!curBlock.getAllTokenList().isEmpty()) {
            curBlock.getAllTokenList().remove(curBlock.getAllTokenList().size() - 1);
        }
        int lastTokenOffSet = getLastTokenOffSet(curBlock);
        int lastTokenLength = getLastTokenlenght(curBlock);

        while (null != curBlock) {
            // if not nested then end and start new block

            curBlock.getAbstractRuleHandler().endScriptBlock(curBlock, lretList, lastTokenOffSet, lastTokenLength);

            // check how many parents blocks need to end the script

            if (!curBlock.getAbstractRuleHandler().isStopParentScriptBlock(curBlock, sqlTokenData.getToken())) {
                curBlock = curBlock.getParent();
                break;
            }
            curBlock = curBlock.getParent();
        }

        // new script token need to identify that the old script
        // block is the end of it is the child of current script
        // block.

        lRuleBean.setScriptBlockInfo(curBlock);

        // create a script block here and
        ScriptBlockInfo createScriptBlock = ruleHandlarByToken.createScriptBlock(curBlock, sqlTokenData, tokenOffset);
        createScriptBlock.addSQLTokenData(sqlTokenData);
        // remove the sql token data from curBlock
        lRuleBean.setScriptBlockInfo(createScriptBlock);

    }

    /**
     * Adds the child stmt data parent.
     *
     * @param curBlock the cur block
     * @return the script block info
     */
    public ScriptBlockInfo addChildStmtDataParent(ScriptBlockInfo curBlock) {

        addSubTokenBeanData(curBlock);

        return curBlock;
    }

    /**
     * Gets the last tokenlenght.
     *
     * @param curBlock the cur block
     * @return the last tokenlenght
     */
    protected int getLastTokenlenght(ScriptBlockInfo curBlock) {
        return curBlock.getLastTokenlenght();
    }

    /**
     * Gets the last token off set.
     *
     * @param curBlock the cur block
     * @return the last token off set
     */
    protected int getLastTokenOffSet(ScriptBlockInfo curBlock) {
        return curBlock.getLastTokenOffSet();
    }

    /**
     * Checks if is end block by this token.
     *
     * @param token the token
     * @param curBlock the cur block
     * @return true, if is end block by this token
     */
    protected boolean isEndBlockByThisToken(IToken token, ScriptBlockInfo curBlock) {
        return false;
    }

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public abstract boolean isNested(ScriptBlockInfo curBlock, IToken token);

    /**
     * Checks if is stop script block.
     *
     * @param curBlock the cur block
     * @param ruleHandlarByToken the rule handlar by token
     * @return true, if is stop script block
     */
    public abstract boolean isStopScriptBlock(ScriptBlockInfo curBlock, AbstractRuleHandler ruleHandlarByToken);

    /**
     * End script block.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @param tokenOffset the token offset
     * @param tokenLength the token length
     */
    public void endScriptBlock(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, int tokenOffset,
            int tokenLength) {
        if (curBlock.isNested()) {
            curBlock.setLenght((tokenOffset + tokenLength) - curBlock.getStartOffSetForBlock());

            lretList.add(new SQLQueryPosition(curBlock.getStartOffSetForBlock(), curBlock.getLenght()));

            if (null != curBlock.getParent()) {
                addSubTokenBeanData(curBlock);
            } else {
                addSQLStmtTokenList(curBlock);
            }
        }
    }

    private void addSubTokenBeanData(ScriptBlockInfo curBlock) {
        SQLStmtTokenListBean stmtTokenListBean = new SQLStmtTokenListBean();
        stmtTokenListBean.setStatementType(curBlock.getTokenType());

        if (curBlock instanceof DMLParamScriptBlockInfo) {
            stmtTokenListBean.setDdlType(((DMLParamScriptBlockInfo) curBlock).getDdlType());
        }

        List<ISQLTokenData> allTokenList = curBlock.getAllTokenList();
        stmtTokenListBean.setSqlTokenData(allTokenList);

        ISQLTokenData fullTokenData = SQLTokenFactory.getTokenData();
        fullTokenData.setSubTokenBean(stmtTokenListBean);
        curBlock = curBlock.getParent();
        curBlock.addSQLTokenData(fullTokenData);
    }

    private void addSQLStmtTokenList(ScriptBlockInfo curBlock) {
        if (null != getSqlStmtTokenManager()) {
            getSqlStmtTokenManager().addSQLStmtTokenListBean(curBlock);
        }
    }

    /**
     * Checks if is block end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is block end
     */
    public boolean isBlockEnd(ScriptBlockInfo curBlock, SQLToken token) {
        return null != curBlock && token.getType() == getEndTokenType(curBlock);
    }

    /**
     * Gets the end token type.
     *
     * @param curBlock the cur block
     * @return the end token type
     */
    public abstract int getEndTokenType(ScriptBlockInfo curBlock);

    /**
     * Checks if is stop parent script block.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is stop parent script block
     */
    public boolean isStopParentScriptBlock(ScriptBlockInfo curBlock, IToken token) {

        return false;
    }

    /**
     * Checks if is rule handler valid.
     *
     * @param currentRuleHandler the current rule handler
     * @param curBlock the cur block
     * @return true, if is rule handler valid
     */
    public boolean isRuleHandlerValid(RuleHandlerIf currentRuleHandler, ScriptBlockInfo curBlock, SQLToken token) {
        return true;
    }

    /**
     * Clone.
     *
     * @return the object
     */
    @Override
    public Object clone() {
        try {
            return this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {

            return null;
        }
    }

}
