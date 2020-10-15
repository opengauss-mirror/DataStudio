/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: BaseCreatehandler Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 23-Mar-2020]
 * @since 23-Mar-2020
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