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

package com.huawei.mppdbide.gauss.sqlparser.bean;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: ScriptBlockInfo
 *
 * @since 3.0.0
 */
public class ScriptBlockInfoImpl implements ScriptBlockInfo {

    private ScriptBlockInfo parent;

    private int tokenType = SQLTokenConstants.T_SQL_UNKNOWN;

    private int startOffSet;

    private int lenght;

    private boolean isNested = true;

    private boolean invokeParent = false;

    private int lastNonBlankTokenOffSet;

    private int lastNonBlankTokenlenght;

    private int lastKnownTokenOffSet;

    private int lastKnownTokenlenght;

    private int preNewlineTokenOffSet;

    private int preNewlineTokenlenght;

    private int lastAnyTokenOffSet;

    private int lastAnyTokenlenght;

    private String lastNonBlankToken;

    private String currentKnownToken;

    private List<String> lastKnownTokenList = new ArrayList<String>();

    private RuleHandlerIf abstractRuleHandler = null;

    private SQLBracketParamData bracketParamData = null;

    private SQLBracketParamData caseParamData = null;

    private int allowDMLWithoutBracket = 0;

    private int nextOptTokenToEnd = SQLTokenConstants.T_SQL_UNKNOWN;

    private int nextMiddleOptTokenToEnd = SQLTokenConstants.T_SQL_UNKNOWN;

    private int recentNewLineCount = 0;

    private List<ISQLTokenData> tokenDataList = new ArrayList<>();

    /**
     * Instantiates a new script block info impl.
     *
     * @param parent the parent
     * @param token the token
     * @param abstractRuleHandler the abstract rule handler
     */
    public ScriptBlockInfoImpl(ScriptBlockInfo parent, IToken token, RuleHandlerIf abstractRuleHandler) {
        this.parent = parent;
        if (token instanceof SQLToken) {
            this.tokenType = ((SQLToken) token).getType();
        }
        this.abstractRuleHandler = abstractRuleHandler;
    }

    public ScriptBlockInfoImpl() {
    }

    /**
     * Gets the start off set for block.
     *
     * @return the start off set for block
     */
    public int getStartOffSetForBlock() {
        return startOffSet;
    }

    /**
     * Sets the start off set for block.
     *
     * @param startOffSet the new start off set for block
     */
    public void setStartOffSetForBlock(int startOffSet) {
        this.startOffSet = startOffSet;
    }

    /**
     * Gets the lenght.
     *
     * @return the lenght
     */
    public int getLenght() {
        return lenght;
    }

    /**
     * Gets the token type.
     *
     * @return the token type
     */
    public int getTokenType() {
        return tokenType;
    }

    /**
     * Sets the token type.
     *
     * @param scriptType the new token type
     */
    public void setTokenType(int scriptType) {
        this.tokenType = scriptType;
    }

    /**
     * Sets the lenght.
     *
     * @param lenght the new lenght
     */
    public void setLenght(int lenght) {
        this.lenght = lenght;
    }

    /**
     * Gets the abstract rule handler.
     *
     * @return the abstract rule handler
     */
    public RuleHandlerIf getAbstractRuleHandler() {
        return abstractRuleHandler;
    }

    /**
     * Sets the abstract rule handler.
     *
     * @param abstractRuleHandler the new abstract rule handler
     */
    public void setAbstractRuleHandler(RuleHandlerIf abstractRuleHandler) {
        this.abstractRuleHandler = abstractRuleHandler;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public ScriptBlockInfo getParent() {
        return parent;
    }

    /**
     * Checks if is nested.
     *
     * @return true, if is nested
     */
    public boolean isNested() {
        return isNested;
    }

    /**
     * Sets the nested.
     *
     * @param isNested the new nested
     */
    public void setNested(boolean isNested) {
        this.isNested = isNested;
    }

    /**
     * Gets the last token off set.
     *
     * @return the last token off set
     */
    public int getLastTokenOffSet() {
        return lastNonBlankTokenOffSet;
    }

    /**
     * Sets the last token off set.
     *
     * @param lastTokenOffSet the new last token off set
     */
    public void setLastTokenOffSet(int lastTokenOffSet) {
        this.lastNonBlankTokenOffSet = lastTokenOffSet;
    }

    /**
     * Gets the last tokenlenght.
     *
     * @return the last tokenlenght
     */
    public int getLastTokenlenght() {
        return lastNonBlankTokenlenght;
    }

    /**
     * Sets the last tokenlenght.
     *
     * @param lastTokenlenght the new last tokenlenght
     */
    public void setLastTokenlenght(int lastTokenlenght) {
        this.lastNonBlankTokenlenght = lastTokenlenght;
    }

    /**
     * Gets the last token.
     *
     * @return the last token
     */
    public String getLastToken() {
        return lastNonBlankToken;
    }

    /**
     * Sets the last token.
     *
     * @param lastToken the new last token
     */
    public void setLastToken(String lastToken) {
        this.lastNonBlankToken = lastToken;
    }

    /**
     * Addlast known token.
     *
     * @param token the token
     */
    public void addlastKnownToken(String token) {
        lastKnownTokenList.add(token);
    }

    /**
     * Gets the last known token list.
     *
     * @return the last known token list
     */
    public List<String> getLastKnownTokenList() {
        return lastKnownTokenList;
    }

    /**
     * Gets the last known token.
     *
     * @return the last known token
     */
    public String getLastKnownToken() {

        if (lastKnownTokenList.size() > 0) {
            return lastKnownTokenList.get(lastKnownTokenList.size() - 1);
        }

        return null;

    }

    /**
     * Gets the current known token.
     *
     * @return the current known token
     */
    public String getCurrentKnownToken() {
        return currentKnownToken;
    }

    /**
     * Sets the current known token.
     *
     * @param currentKnownToken the new current known token
     */
    public void setCurrentKnownToken(String currentKnownToken) {
        this.currentKnownToken = currentKnownToken;
    }

    /**
     * Gets the bracket param data.
     *
     * @return the bracket param data
     */
    public SQLBracketParamData getBracketParamData() {
        return bracketParamData;
    }

    /**
     * Sets the bracket param data.
     *
     * @param bracketParamData the new bracket param data
     */
    public void setBracketParamData(SQLBracketParamData bracketParamData) {
        this.bracketParamData = bracketParamData;
    }

    /**
     * Gets the allow DML without bracket.
     *
     * @return the allow DML without bracket
     */
    public int getAllowDMLWithoutBracket() {
        return allowDMLWithoutBracket;
    }

    /**
     * Incr allow DML without bracket.
     */
    public void incrAllowDMLWithoutBracket() {
        this.allowDMLWithoutBracket++;
    }

    /**
     * Checks if is invoke parent.
     *
     * @return true, if is invoke parent
     */
    public boolean isInvokeParent() {
        return invokeParent;
    }

    /**
     * Sets the invoke parent.
     *
     * @param invokeParent the new invoke parent
     */
    public void setInvokeParent(boolean invokeParent) {
        this.invokeParent = invokeParent;
    }

    /**
     * Gets the last known token off set.
     *
     * @return the last known token off set
     */
    public int getLastKnownTokenOffSet() {
        return lastKnownTokenOffSet;
    }

    /**
     * Sets the last known token off set.
     *
     * @param lastKnownTokenOffSet the new last known token off set
     */
    public void setLastKnownTokenOffSet(int lastKnownTokenOffSet) {
        this.lastKnownTokenOffSet = lastKnownTokenOffSet;
    }

    /**
     * Gets the last known tokenlenght.
     *
     * @return the last known tokenlenght
     */
    public int getLastKnownTokenlenght() {
        return lastKnownTokenlenght;
    }

    /**
     * Sets the last known tokenlenght.
     *
     * @param lastKnownTokenlenght the new last known tokenlenght
     */
    public void setLastKnownTokenlenght(int lastKnownTokenlenght) {
        this.lastKnownTokenlenght = lastKnownTokenlenght;
    }

    /**
     * Gets the case param data.
     *
     * @return the case param data
     */
    public SQLBracketParamData getCaseParamData() {
        return caseParamData;
    }

    /**
     * Sets the case param data.
     *
     * @param caseParamData the new case param data
     */
    public void setCaseParamData(SQLBracketParamData caseParamData) {
        this.caseParamData = caseParamData;
    }

    /**
     * Gets the next opt token to end.
     *
     * @return the next opt token to end
     */
    public int getNextOptTokenToEnd() {
        return nextOptTokenToEnd;
    }

    /**
     * Sets the next opt token to end.
     *
     * @param nextOptTokenToEnd the new next opt token to end
     */
    public void setNextOptTokenToEnd(int nextOptTokenToEnd) {
        this.nextOptTokenToEnd = nextOptTokenToEnd;
    }

    /**
     * Gets the recent new line count.
     *
     * @return the recent new line count
     */
    public int getRecentNewLineCount() {
        return recentNewLineCount;
    }

    /**
     * Reset recent new line count.
     */
    public void resetRecentNewLineCount() {
        this.recentNewLineCount = 0;
    }

    /**
     * Incr recent new line count.
     */
    public void incrRecentNewLineCount() {
        this.recentNewLineCount++;
    }

    /**
     * Gets the pre newline token off set.
     *
     * @return the pre newline token off set
     */
    public int getPreNewlineTokenOffSet() {
        return preNewlineTokenOffSet;
    }

    /**
     * Sets the pre newline token off set.
     *
     * @param preNewlineTokenOffSet the new pre newline token off set
     */
    public void setPreNewlineTokenOffSet(int preNewlineTokenOffSet) {
        this.preNewlineTokenOffSet = preNewlineTokenOffSet;
    }

    /**
     * Gets the pre newline tokenlenght.
     *
     * @return the pre newline tokenlenght
     */
    public int getPreNewlineTokenlenght() {
        return preNewlineTokenlenght;
    }

    /**
     * Sets the pre newline tokenlenght.
     *
     * @param preNewlineTokenlenght the new pre newline tokenlenght
     */
    public void setPreNewlineTokenlenght(int preNewlineTokenlenght) {
        this.preNewlineTokenlenght = preNewlineTokenlenght;
    }

    /**
     * Gets the any token off set.
     *
     * @return the any token off set
     */
    public int getAnyTokenOffSet() {
        return lastAnyTokenOffSet;
    }

    /**
     * Sets the any token off set.
     *
     * @param anyTokenOffSet the new any token off set
     */
    public void setAnyTokenOffSet(int anyTokenOffSet) {
        this.lastAnyTokenOffSet = anyTokenOffSet;
    }

    /**
     * Gets the any tokenlenght.
     *
     * @return the any tokenlenght
     */
    public int getAnyTokenlenght() {
        return lastAnyTokenlenght;
    }

    /**
     * Sets the any tokenlenght.
     *
     * @param anyTokenlenght the new any tokenlenght
     */
    public void setAnyTokenlenght(int anyTokenlenght) {
        this.lastAnyTokenlenght = anyTokenlenght;
    }

    /**
     * Gets the next middle opt token to end.
     *
     * @return the next middle opt token to end
     */
    public int getNextMiddleOptTokenToEnd() {
        return nextMiddleOptTokenToEnd;
    }

    /**
     * Sets the next middle opt token to end.
     *
     * @param nextMiddleOptTokenToEnd the new next middle opt token to end
     */
    public void setNextMiddleOptTokenToEnd(int nextMiddleOptTokenToEnd) {
        this.nextMiddleOptTokenToEnd = nextMiddleOptTokenToEnd;
    }

    @Override
    public List<ISQLTokenData> getAllTokenList() {
        return tokenDataList;
    }

    @Override
    public void addSQLTokenData(ISQLTokenData token) {
        tokenDataList.add(token);
    }

}
