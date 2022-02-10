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

package org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif;

import java.util.List;

import org.opengauss.mppdbide.gauss.sqlparser.bean.SQLBracketParamData;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: ScriptBlockInfo
 *
 * @since 3.0.0
 */
public interface ScriptBlockInfo {

    /**
     * Gets the start off set for block.
     *
     * @return the start off set for block
     */
    int getStartOffSetForBlock();

    /**
     * Sets the start off set for block.
     *
     * @param startOffSet the new start off set for block
     */
    void setStartOffSetForBlock(int startOffSet);

    /**
     * Gets the lenght.
     *
     * @return the lenght
     */
    int getLenght();

    /**
     * Gets the token type.
     *
     * @return the token type
     */
    int getTokenType();

    /**
     * Sets the token type.
     *
     * @param scriptType the new token type
     */
    void setTokenType(int scriptType);

    /**
     * Sets the lenght.
     *
     * @param lenght the new lenght
     */
    void setLenght(int lenght);

    /**
     * Gets the abstract rule handler.
     *
     * @return the abstract rule handler
     */
    RuleHandlerIf getAbstractRuleHandler();

    /**
     * Sets the abstract rule handler.
     *
     * @param abstractRuleHandler the new abstract rule handler
     */
    void setAbstractRuleHandler(RuleHandlerIf abstractRuleHandler);

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    ScriptBlockInfo getParent();

    /**
     * Checks if is nested.
     *
     * @return true, if is nested
     */
    boolean isNested();

    /**
     * Sets the nested.
     *
     * @param isNested the new nested
     */
    void setNested(boolean isNested);

    /**
     * Gets the last token off set.
     *
     * @return the last token off set
     */
    int getLastTokenOffSet();

    /**
     * Sets the last token off set.
     *
     * @param lastTokenOffSet the new last token off set
     */
    void setLastTokenOffSet(int lastTokenOffSet);

    /**
     * Gets the last tokenlenght.
     *
     * @return the last tokenlenght
     */
    int getLastTokenlenght();

    /**
     * Sets the last tokenlenght.
     *
     * @param lastTokenlenght the new last tokenlenght
     */
    void setLastTokenlenght(int lastTokenlenght);

    /**
     * Gets the last token.
     *
     * @return the last token
     */
    String getLastToken();

    /**
     * Sets the last token.
     *
     * @param lastToken the new last token
     */
    void setLastToken(String lastToken);

    /**
     * Addlast known token.
     *
     * @param token the token
     */
    void addlastKnownToken(String token);

    /**
     * Gets the last known token list.
     *
     * @return the last known token list
     */
    List<String> getLastKnownTokenList();

    /**
     * Adds the SQL token data.
     *
     * @param token the token
     */
    void addSQLTokenData(ISQLTokenData token);

    /**
     * Gets the all token list.
     *
     * @return the all token list
     */
    List<ISQLTokenData> getAllTokenList();

    /**
     * Gets the last known token.
     *
     * @return the last known token
     */
    String getLastKnownToken();

    /**
     * Gets the current known token.
     *
     * @return the current known token
     */
    String getCurrentKnownToken();

    /**
     * Sets the current known token.
     *
     * @param currentKnownToken the new current known token
     */
    void setCurrentKnownToken(String currentKnownToken);

    /**
     * Gets the bracket param data.
     *
     * @return the bracket param data
     */
    SQLBracketParamData getBracketParamData();

    /**
     * Sets the bracket param data.
     *
     * @param bracketParamData the new bracket param data
     */
    void setBracketParamData(SQLBracketParamData bracketParamData);

    /**
     * Gets the allow DML without bracket.
     *
     * @return the allow DML without bracket
     */
    int getAllowDMLWithoutBracket();

    /**
     * Incr allow DML without bracket.
     */
    void incrAllowDMLWithoutBracket();

    /**
     * Checks if is invoke parent.
     *
     * @return true, if is invoke parent
     */
    boolean isInvokeParent();

    /**
     * Sets the invoke parent.
     *
     * @param invokeParent the new invoke parent
     */
    void setInvokeParent(boolean invokeParent);

    /**
     * Gets the last known token off set.
     *
     * @return the last known token off set
     */
    int getLastKnownTokenOffSet();

    /**
     * Sets the last known token off set.
     *
     * @param lastKnownTokenOffSet the new last known token off set
     */
    void setLastKnownTokenOffSet(int lastKnownTokenOffSet);

    /**
     * Gets the last known tokenlenght.
     *
     * @return the last known tokenlenght
     */
    int getLastKnownTokenlenght();

    /**
     * Sets the last known tokenlenght.
     *
     * @param lastKnownTokenlenght the new last known tokenlenght
     */
    void setLastKnownTokenlenght(int lastKnownTokenlenght);

    /**
     * Gets the case param data.
     *
     * @return the case param data
     */
    SQLBracketParamData getCaseParamData();

    /**
     * Sets the case param data.
     *
     * @param caseParamData the new case param data
     */
    void setCaseParamData(SQLBracketParamData caseParamData);

    /**
     * Gets the next opt token to end.
     *
     * @return the next opt token to end
     */
    int getNextOptTokenToEnd();

    /**
     * Sets the next opt token to end.
     *
     * @param nextOptTokenToEnd the new next opt token to end
     */
    void setNextOptTokenToEnd(int nextOptTokenToEnd);

    /**
     * Gets the recent new line count.
     *
     * @return the recent new line count
     */
    int getRecentNewLineCount();

    /**
     * Reset recent new line count.
     */
    void resetRecentNewLineCount();

    /**
     * Incr recent new line count.
     */
    void incrRecentNewLineCount();

    /**
     * Gets the pre newline token off set.
     *
     * @return the pre newline token off set
     */
    int getPreNewlineTokenOffSet();

    /**
     * Sets the pre newline token off set.
     *
     * @param preNewlineTokenOffSet the new pre newline token off set
     */
    void setPreNewlineTokenOffSet(int preNewlineTokenOffSet);

    /**
     * Gets the pre newline tokenlenght.
     *
     * @return the pre newline tokenlenght
     */
    int getPreNewlineTokenlenght();

    /**
     * Sets the pre newline tokenlenght.
     *
     * @param preNewlineTokenlenght the new pre newline tokenlenght
     */
    void setPreNewlineTokenlenght(int preNewlineTokenlenght);

    /**
     * Gets the any token off set.
     *
     * @return the any token off set
     */
    int getAnyTokenOffSet();

    /**
     * Sets the any token off set.
     *
     * @param anyTokenOffSet the new any token off set
     */
    void setAnyTokenOffSet(int anyTokenOffSet);

    /**
     * Gets the any tokenlenght.
     *
     * @return the any tokenlenght
     */
    int getAnyTokenlenght();

    /**
     * Sets the any tokenlenght.
     *
     * @param anyTokenlenght the new any tokenlenght
     */
    void setAnyTokenlenght(int anyTokenlenght);

    /**
     * Gets the next middle opt token to end.
     *
     * @return the next middle opt token to end
     */
    int getNextMiddleOptTokenToEnd();

    /**
     * Sets the next middle opt token to end.
     *
     * @param nextMiddleOptTokenToEnd the new next middle opt token to end
     */
    void setNextMiddleOptTokenToEnd(int nextMiddleOptTokenToEnd);
}
