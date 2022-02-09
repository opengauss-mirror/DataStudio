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

package org.opengauss.mppdbide.gauss.sqlparser.handlerif;

import java.util.List;

import org.eclipse.jface.text.rules.IToken;

import org.opengauss.mppdbide.gauss.sqlparser.SQLToken;
import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.RuleBean;
import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.handler.AbstractRuleHandler;

/**
 * Title: RuleHandlerIf
 *
 * @since 3.0.0
 */
public interface RuleHandlerIf {

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
    RuleBean handle(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, IToken token, ISQLTokenData ruleManager,
            AbstractRuleHandler ruleHandlarByToken);

    /**
     * Creates the script block.
     *
     * @param parent the parent
     * @param token the token
     * @param tokenOffset the token offset
     * @return the script block info
     */
    ScriptBlockInfo createScriptBlock(ScriptBlockInfo parent, ISQLTokenData token, int tokenOffset);

    /**
     * Handle nested block.
     *
     * @param lretList the lret list
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param ruleHandlarByToken the rule handlar by token
     * @param tokenOffset the token offset
     */
    void handleNestedBlock(List<SQLScriptElement> lretList, ISQLTokenData token, RuleBean lRuleBean,
            AbstractRuleHandler ruleHandlarByToken, int tokenOffset);

    /**
     * Handle nested block script create.
     *
     * @param curBlock the cur block
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param tokenOffset the token offset
     */
    void handleNestedBlockScriptCreate(ScriptBlockInfo curBlock, ISQLTokenData token, RuleBean lRuleBean,
            int tokenOffset);

    /**
     * Handle other end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param lretList the lret list
     * @param ruleManager the rule manager
     */
    void handleOtherEnd(ScriptBlockInfo curBlock, SQLToken token, RuleBean lRuleBean, List<SQLScriptElement> lretList,
            ISQLTokenData ruleManager);

    /**
     * Handle partial stmt.
     *
     * @param curBlock the cur block
     */
    void handlePartialStmt(ScriptBlockInfo curBlock);

    /**
     * Handle block end.
     *
     * @param lretList the lret list
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param ruleHandlarByToken the rule handlar by token
     * @param tokenOffset the token offset
     */
    void handleBlockEnd(List<SQLScriptElement> lretList, ISQLTokenData token, RuleBean lRuleBean,
            AbstractRuleHandler ruleHandlarByToken, int tokenOffset);

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    boolean isNested(ScriptBlockInfo curBlock, IToken token);

    /**
     * Checks if is stop script block.
     *
     * @param curBlock the cur block
     * @param ruleHandlarByToken the rule handlar by token
     * @return true, if is stop script block
     */
    boolean isStopScriptBlock(ScriptBlockInfo curBlock, AbstractRuleHandler ruleHandlarByToken);

    /**
     * End script block.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @param tokenOffset the token offset
     * @param tokenLength the token length
     */
    void endScriptBlock(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, int tokenOffset, int tokenLength);

    /**
     * Checks if is block end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is block end
     */
    boolean isBlockEnd(ScriptBlockInfo curBlock, SQLToken token);

    /**
     * Gets the end token type.
     *
     * @param curBlock the cur block
     * @return the end token type
     */
    int getEndTokenType(ScriptBlockInfo curBlock);

    /**
     * Checks if is stop parent script block.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is stop parent script block
     */
    boolean isStopParentScriptBlock(ScriptBlockInfo curBlock, IToken token);

    /** 
     * the isRuleHandlerValid
     * 
     * @param currentRuleHandler the currentRuleHandler
     * @param curBlock the curBlock
     * @param token the token
     * @return true. if is valid rule handler
     */
    boolean isRuleHandlerValid(RuleHandlerIf currentRuleHandler, ScriptBlockInfo curBlock, SQLToken token);

    /**
     * End script strategy.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @param token the token
     * @param ruleManager the rule manager
     * @param lRuleBean the l rule bean
     */
    void endScriptStrategy(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, SQLToken token,
            ISQLTokenData ruleManager, RuleBean lRuleBean);

    /**
     * End optional script.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @param token the token
     * @param ruleManager the rule manager
     * @return the rule bean
     */
    RuleBean endOptionalScript(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, SQLToken token,
            ISQLTokenData ruleManager);

    /**
     * Checks if is block end by other block.
     *
     * @return true, if is block end by other block
     */
    boolean isBlockEndByOtherBlock();

    /**
     * Checks if is ignore by current block.
     *
     * @param ruleHandlarByToken the rule handlar by token
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is ignore by current block
     */
    boolean isIgnoreByCurrentBlock(AbstractRuleHandler ruleHandlarByToken, ScriptBlockInfo curBlock, SQLToken token);

    /**
     * Checks if is ignore by next token.
     *
     * @param ruleHandlarByToken the rule handlar by token
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is ignore by next token
     */
    boolean isIgnoreByNextToken(AbstractRuleHandler ruleHandlarByToken, ScriptBlockInfo curBlock, SQLToken token);

}