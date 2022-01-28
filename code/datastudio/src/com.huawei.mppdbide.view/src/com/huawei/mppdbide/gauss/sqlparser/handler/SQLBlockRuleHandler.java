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

package com.huawei.mppdbide.gauss.sqlparser.handler;

import java.util.List;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.RuleBean;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;

/**
 * Title: SQLBlockRuleHandler
 *
 * @since 3.0.0
 */
public class SQLBlockRuleHandler extends AbstractRuleHandler {

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
     * Checks if is block end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is block end
     */
    public boolean isBlockEnd(ScriptBlockInfo curBlock, SQLToken token) {

        if (isBlockEndWithOutOptionalToken(curBlock, token)) {

            if (null != curBlock.getParent() && curBlock.getParent().isInvokeParent()) {
                return true;
            }

            curBlock.setNextOptTokenToEnd(SQLTokenConstants.T_SQL_DELIMITER);
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

        return null != curBlock && token.getType() == SQLTokenConstants.T_SQL_BLOCK_END;
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
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {
        return true;
    }

    /**
     * handles the parent block end case, when this block is getting ended
     */
    public void handleParentBlockEnd(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList, SQLToken token,
            ISQLTokenData ruleManager, RuleBean lRuleBean) {
        ScriptBlockInfo parentBlock = curBlock.getParent();
        while (null != parentBlock) {
            if (parentBlock.getAbstractRuleHandler() instanceof AbstractCreateHandler
                    && parentBlock.getAbstractRuleHandler().isBlockEnd(parentBlock, token)) {
                parentBlock.getAbstractRuleHandler().endScriptBlock(parentBlock, lretList, ruleManager.getTokenOffset(),
                        ruleManager.getTokenLength());
            } else {
                break;
            }
            parentBlock = parentBlock.getParent();
        }
        lRuleBean.setScriptBlockInfo(parentBlock);
    }
}
