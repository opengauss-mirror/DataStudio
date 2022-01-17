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

import com.huawei.mppdbide.gauss.sqlparser.SQLDDLTypeEnum;
import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * 
 * Title: FuncAndProcCreateStmt
 *
 * @since 3.0.0
 */
public class FuncAndProcCreateStmt extends AbstractCreateStmt<DMLParamScriptBlockInfo> {

    /**
     * Gets the end token type.
     *
     * @return the end token type
     */
    public int getEndTokenType() {
        return SQLTokenConstants.T_SQL_NONEXIST;
    }

    /**
     * Checks if is stop parent script block.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is stop parent script block
     */
    @Override
    public boolean isStopParentScriptBlock(DMLParamScriptBlockInfo curBlock, SQLToken token) {

        return false;
    }

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    @Override
    public boolean isNested(DMLParamScriptBlockInfo curBlock, SQLToken token) {

        if (curBlock.isEndStmtFound() || isLangStmtEnd(curBlock)) {
            return false;
        }

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
        return false;
    }

    /**
     * Checks if is lang stmt end.
     *
     * @param curBlock the cur block
     * @return true, if is lang stmt end
     */
    public boolean isLangStmtEnd(DMLParamScriptBlockInfo curBlock) {
        return curBlock.isLangKeywordFound() && !curBlock.isLangKeywordFoundBeforeStart();
    }

    /**
     * Checks if is block end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is block end
     */
    @Override
    public boolean isBlockEnd(DMLParamScriptBlockInfo curBlock, SQLToken token) {

        String lastToken = curBlock.getLastKnownToken();

        ScriptBlockInfo parent = curBlock.getParent();

        if (null != parent && parent instanceof DMLParamScriptBlockInfo) {
            DMLParamScriptBlockInfo dmlScript = (DMLParamScriptBlockInfo) parent;
            SQLDDLTypeEnum ddlType = dmlScript.getDdlType();
            if (isPackageType(parent, ddlType)) {
                // stop if found ;
                if (token.getType() == SQLTokenConstants.T_SQL_DELIMITER) {
                    return true;
                }
            }
        }

        if (token.getType() == SQLTokenConstants.T_SQL_KEYWORK_AS
                || token.getType() == SQLTokenConstants.T_SQL_KEYWORK_IS) {
            curBlock.setPlBodyStart(true);
        }

        if (null == curBlock.getStartKeywork()) {
            if (isLastTokenIsorAs(lastToken) && token.getType() == SQLTokenConstants.T_SQL_FUNCTION_END) {
                curBlock.setStartKeywork(curBlock.getCurrentKnownToken());
                return false;
            } else if (isProcOrFuncStart(token)) {
                curBlock.setInvokeParent(true);
                curBlock.setStartKeywork(curBlock.getCurrentKnownToken());
                return false;
            }
        }

        handleFuncEndStatement(curBlock, token, lastToken);

        if (isLangStmtEnd(curBlock) && token.getType() == SQLTokenConstants.T_SQL_DELIMITER) {
            return true;
        } else if (curBlock.isEndStmtFound() && (token.getType() == SQLTokenConstants.T_SQL_DELIMITER
                || token.getType() == SQLTokenConstants.T_SQL_DELIMITER_FSLASH)) {
            return true;
        }

        return false;
    }

    private void handleFuncEndStatement(DMLParamScriptBlockInfo curBlock, SQLToken token, String lastToken) {
        if (isFuncEndKeyword(token, lastToken)) {
            curBlock.setEndStmtFound(true);
        } else if (token.getType() == SQLTokenConstants.T_SQL_BLOCK_END) {
            curBlock.setEndStmtFound(true);
        }

        if (token.getType() == SQLTokenConstants.T_SQL_KEYWORK_LANGUAGE) {
            curBlock.setLangKeywordFound(true);

            curBlock.setLangKeywordFoundBeforeStart(!curBlock.isPlBodyStart());
        }
    }

    private boolean isFuncEndKeyword(SQLToken token, String lastToken) {
        return token.getType() == SQLTokenConstants.T_SQL_FUNCTION_END
                && !SQLFoldingConstants.SQL_KEYWORK_AS.equalsIgnoreCase(lastToken)
                && !SQLFoldingConstants.SQL_KEYWORK_IS.equalsIgnoreCase(lastToken);
    }

    private boolean isPackageType(ScriptBlockInfo parent, SQLDDLTypeEnum ddlType) {
        return ddlType == SQLDDLTypeEnum.PACKAGE && !(parent.getLastKnownTokenList().contains("body")
                || parent.getLastKnownTokenList().contains("BODY"));
    }

    /**
     * Checks if is proc or func start.
     *
     * @param token the token
     * @return true, if is proc or func start
     */
    public boolean isProcOrFuncStart(SQLToken token) {
        return token.getType() == SQLTokenConstants.T_SQL_BLOCK_BEGIN
                || token.getType() == SQLTokenConstants.T_SQL_BLOCK_DECLARE;
    }

    /**
     * Checks if is last token isor as.
     *
     * @param lastToken the last token
     * @return true, if is last token isor as
     */
    public boolean isLastTokenIsorAs(String lastToken) {
        return SQLFoldingConstants.SQL_KEYWORK_AS.equalsIgnoreCase(lastToken)
                || SQLFoldingConstants.SQL_KEYWORK_IS.equalsIgnoreCase(lastToken);
    }

}
