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

import org.opengauss.mppdbide.gauss.sqlparser.SQLToken;
import org.opengauss.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: BaseCreatehandler
 *
 * @since 3.0.0
 */
public abstract class BaseCreatehandler extends AbstractRuleHandler implements IPartialStmt {
    /**
     * AbstractCreateStmt to maintain current Create stmt
     */
    protected AbstractCreateStmt<DMLParamScriptBlockInfo> abstractCreateStmt = new AbstractCreateStmt<DMLParamScriptBlockInfo>();

    public BaseCreatehandler() {
    }

    /**
     * create and returns specific create stmt
     * 
     * @param curScriptBlock current DML script Object
     * @return the abstract stmt
     */
    protected AbstractCreateStmt<DMLParamScriptBlockInfo> getCreateStmt(DMLParamScriptBlockInfo curScriptBlock) {
        return RuleHandlerConfig.getInstance().getCreateStmt(curScriptBlock, abstractCreateStmt);
    }

    /**
     * returns DML Script Block
     * 
     * @param curBlock the cur block
     * @return DML Script Block
     */
    protected DMLParamScriptBlockInfo getDMLParamScriptBlock(ScriptBlockInfo curBlock) {
        if (curBlock instanceof DMLParamScriptBlockInfo) {
            return (DMLParamScriptBlockInfo) curBlock;
        }
        return null;
    }

    /**
     * Checks if is rule handler valid.
     *
     * @param currentRuleHandler the current rule handler
     * @param curBlock the cur block
     * @return true, if is rule handler valid
     */
    public boolean isRuleHandlerValid(RuleHandlerIf currentRuleHandler, ScriptBlockInfo curBlock, SQLToken lToken) {
        DMLParamScriptBlockInfo dmlParamScriptBlock = getDMLParamScriptBlock(curBlock);
        return getCreateStmt(dmlParamScriptBlock).isRuleHandlerValid(currentRuleHandler, curBlock, lToken);
    }

    /**
     * Checks if is rule handler valid.
     *
     * @param currentRuleHandler the current rule handler
     * @param curBlock the cur block
     * @return true, if is rule handler valid
     */
    public boolean isPackageStmt(RuleHandlerIf currentRuleHandler, ScriptBlockInfo curBlock, SQLToken lToken) {
        DMLParamScriptBlockInfo dmlParamScriptBlock = getDMLParamScriptBlock(curBlock);
        return getCreateStmt(dmlParamScriptBlock) instanceof PackageCreateStmt;
    }

    /**
     * Gets the abstract create stmt.
     *
     * @param curBlock the cur block
     * @return the abstract create stmt
     */
    public AbstractCreateStmt<DMLParamScriptBlockInfo> getAbstractCreateStmt(ScriptBlockInfo curBlock) {
        DMLParamScriptBlockInfo dmlParamScriptBlock = getDMLParamScriptBlock(curBlock);
        return RuleHandlerConfig.getInstance().getCreateStmt(dmlParamScriptBlock, abstractCreateStmt);
    }
}