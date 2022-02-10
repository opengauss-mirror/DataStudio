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

import java.util.HashMap;
import java.util.Map;

import org.opengauss.mppdbide.gauss.sqlparser.SQLDDLTypeEnum;
import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;

/**
 * Title: RuleHandlerConfig
 *
 * @since 3.0.0
 */
public class RuleHandlerConfig {
    private Map<Integer, AbstractRuleHandler> ruleLibrary = new HashMap<Integer, AbstractRuleHandler>(10);

    private Map<SQLDDLTypeEnum, AbstractCreateStmt<DMLParamScriptBlockInfo>> createStmtLibrary = new HashMap<SQLDDLTypeEnum, AbstractCreateStmt<DMLParamScriptBlockInfo>>();

    private Map<SQLDDLTypeEnum, AbstractAlterStmt> alterStmtLibrary = new HashMap<SQLDDLTypeEnum, AbstractAlterStmt>();

    private static RuleHandlerConfig ruleHandlerConfig = new RuleHandlerConfig();

    /**
     * Gets the single instance of RuleHandlerConfig.
     *
     * @return single instance of RuleHandlerConfig
     */
    public static RuleHandlerConfig getInstance() {

        return ruleHandlerConfig;
    }

    private RuleHandlerConfig() {
        ruleLibrary.put(SQLTokenConstants.T_SQL_BLOCK_BEGIN, new SQLBlockRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_DML_SELECT, new SelectDMLRuleHandler());
        ruleLibrary.put(SQLTokenConstants.T_SQL_DML_INSERT, new InsertDMLRuleHandler());
        ruleLibrary.put(SQLTokenConstants.T_SQL_DML_UPDATE, new UpdateDMLRuleHandler());
        ruleLibrary.put(SQLTokenConstants.T_SQL_DML_DELETE, new DeleteDMLRuleHandler());
        ruleLibrary.put(SQLTokenConstants.T_SQL_DML_TRUNCATE, new TruncateDMLRuleHandler());
        ruleLibrary.put(SQLTokenConstants.T_SQL_DML_WITH, new WithDMLRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_DDL_CONTROL_IF, new SQLIFConditionRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_DDL_CONTROL_ELSE, new SQLELSEConditionRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_DDL_CONTROL_ELSIF, new SQLELSIFConditionRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_DDL_CREATE, new AbstractCreateHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_BLOCK_DECLARE, new SQLBlockDeclareRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_DDL_ALTER, new AbstractAlterHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_LOOP, new SQLLoopRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_KEYWORK_CASE, new SQLCaseStmtRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_DROP, new DropBlockRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_GRANT, new GrantPermissionBlockRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_REVOKE, new RevokePermissionBlockRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_KEYWORD_FOR, new SQLForLoopRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_KEYWORD_CURSOR, new SQLCursorRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_KEYWORK_UNION, new SQLUnionRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_MERGE, new SQLMergeRuleHandler());

        ruleLibrary.put(SQLTokenConstants.T_SQL_DDL_CREATE_PROC, new AbstractCreateHandler());
        ruleLibrary.put(SQLTokenConstants.T_SQL_DDL_CREATE_FUNC, new AbstractCreateHandler());

        createStmtLibrary.put(SQLDDLTypeEnum.PROCEDURE, new ProcedureCreateStmt());

        createStmtLibrary.put(SQLDDLTypeEnum.FUNCTION, new FunctionCreateStmt());

        createStmtLibrary.put(SQLDDLTypeEnum.PACKAGE, new PackageCreateStmt());

        createStmtLibrary.put(SQLDDLTypeEnum.TABLE, new TableCreateStmt());

        createStmtLibrary.put(SQLDDLTypeEnum.VIEW, new ViewCreateStmt());

        createStmtLibrary.put(SQLDDLTypeEnum.TRIGGER, new TriggerCreateStmt());

        alterStmtLibrary.put(SQLDDLTypeEnum.VIEW, new ViewAlterStmt());

    }

    /**
     * Gets the creates the stmt.
     *
     * @param curScriptBlock the cur script block
     * @param abstractCreateStmt the abstract create stmt
     * @return the creates the stmt
     */
    public AbstractCreateStmt<DMLParamScriptBlockInfo> getCreateStmt(DMLParamScriptBlockInfo curScriptBlock,
            AbstractCreateStmt<DMLParamScriptBlockInfo> abstractCreateStmt) {

        return createStmtLibrary.getOrDefault(curScriptBlock.getDdlType(), abstractCreateStmt);

    }

    /**
     * Gets the alter stmt.
     *
     * @param curScriptBlock the cur script block
     * @param abstractCreateStmt the abstract create stmt
     * @return the alter stmt
     */
    public AbstractAlterStmt getAlterStmt(DMLParamScriptBlockInfo curScriptBlock,
            AbstractAlterStmt abstractCreateStmt) {

        return alterStmtLibrary.getOrDefault(curScriptBlock.getDdlType(), abstractCreateStmt);

    }

    /**
     * Gets the rule handle.
     *
     * @param type the type
     * @return the rule handle
     */
    public AbstractRuleHandler getRuleHandle(int type) {

        return ruleLibrary.get(type);

    }

    /**
     * Gets the rule handle.
     *
     * @param type the type
     * @return the rule handle
     */
    public AbstractRuleHandler getNewRuleHandle(int type) {

        AbstractRuleHandler ruleHandlarByToken = ruleLibrary.get(type);
        if (null != ruleHandlarByToken) {
            return (AbstractRuleHandler) ruleHandlarByToken.clone();
        }
        return null;

    }

}
