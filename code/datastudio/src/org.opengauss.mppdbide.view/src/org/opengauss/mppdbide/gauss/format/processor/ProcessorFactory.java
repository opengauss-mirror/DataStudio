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

package org.opengauss.mppdbide.gauss.format.processor;

import java.util.HashMap;
import java.util.Map;

import org.opengauss.mppdbide.gauss.format.processor.begin.BeginFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.begin.DeclareFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.begin.ast.BeginASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.begin.ast.DeclareASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.casestmt.CaseASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.casestmt.CaseStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.common.CTEASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.common.ReturningASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.create.CreateFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.create.ast.AsASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.create.ast.CreateASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.create.ast.LanguageASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.create.ast.ReturnsASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.cursor.CursorStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.cursor.ast.CursorStmtASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.delete.DeleteStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.delete.ast.DeleteFromASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.delete.ast.DeleteUsingASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.forloop.ForLoopFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.forloop.ast.ForLoopASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.ifstmt.IfStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.ifstmt.ast.IfStmtASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.insert.InsertStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.insert.ast.InsertIntoASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.insert.ast.InsertValuesListASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.loop.LoopFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.loop.ast.LoopASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.merge.MergeStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.merge.ast.MergeASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.merge.ast.MergeWithASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.node.SqlNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.FromASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.GroupByASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.HavingASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.LimitASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.OrderByASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.SelectASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.SelectStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.WhereASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.select.WindowASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.union.UnionFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.union.ast.UnionASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.update.UpdateStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.update.ast.UpdateIntoASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.update.ast.UpdateSetASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.with.WithASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.with.WithStmtFormatProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.begin.TBeginASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.begin.TDeclareASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.casestmt.TCaseASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.common.TCTEASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.common.TFromASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.common.TOrderByASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.common.TReturningASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.common.TWhereASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.create.TAsASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.create.TCreateASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.create.TLaungageASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.create.TReturnASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.cursor.TCursorASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.delete.TDeleteFromASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.delete.TDeleteUsingASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.forloop.TForLoopASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.ifstmt.TIfElseASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertDefaultASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertIntoASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertValuesASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.limit.TLimitASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.limit.TOffsetASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.loop.TLoopASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeWhenASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select.TFetchASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select.TForASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select.TGroupByASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select.THavingASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select.TSelectASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select.TWindowASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.union.TUnionASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.update.TUpdateIntoASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.update.TUpdateSetASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.with.TWithASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.block.TBeginSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.block.TDeclareSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.common.TUnionSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.condition.TCaseSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.condition.TIfElseSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.debugobj.TCreateSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.debugobj.TCursorSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TDeleteSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TInsertSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TMergeSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TSelectSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TUpdateSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TWithSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.loop.TForLoopSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.loop.TLoopSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TCTEExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ProcessorFactory
 *
 * @since 3.0.0
 */
public class ProcessorFactory {

    private static Map<Class, Class> processorMap = new HashMap<>();

    static {

        processorMap.put(TSelectSqlStatement.class, SelectStmtFormatProcessor.class);
        processorMap.put(TSelectASTNode.class, SelectASTNodeFormatProcessor.class);
        processorMap.put(TLimitASTNode.class, LimitASTNodeFormatProcessor.class);
        processorMap.put(TOrderByASTNode.class, OrderByASTNodeFormatProcessor.class);
        processorMap.put(TGroupByASTNode.class, GroupByASTNodeFormatProcessor.class);
        processorMap.put(THavingASTNode.class, HavingASTNodeFormatProcessor.class);
        processorMap.put(TForASTNode.class, LimitASTNodeFormatProcessor.class);
        processorMap.put(TOffsetASTNode.class, LimitASTNodeFormatProcessor.class);
        processorMap.put(TFetchASTNode.class, LimitASTNodeFormatProcessor.class);
        processorMap.put(TWindowASTNode.class, WindowASTNodeFormatProcessor.class);

        // insert process rules
        processorMap.put(TInsertSqlStatement.class, InsertStmtFormatProcessor.class);
        processorMap.put(TInsertIntoASTNode.class, InsertIntoASTNodeFormatProcessor.class);
        processorMap.put(TInsertDefaultASTNode.class, AbstractASTNodeFormatProcessor.class);
        processorMap.put(TInsertValuesASTNode.class, InsertValuesListASTNodeFormatProcessor.class);

        // delete process rules
        processorMap.put(TDeleteSqlStatement.class, DeleteStmtFormatProcessor.class);
        processorMap.put(TDeleteFromASTNode.class, DeleteFromASTNodeFormatProcessor.class);
        processorMap.put(TDeleteUsingASTNode.class, DeleteUsingASTNodeFormatProcessor.class);

        // update process rules
        processorMap.put(TUpdateSqlStatement.class, UpdateStmtFormatProcessor.class);
        processorMap.put(TUpdateIntoASTNode.class, UpdateIntoASTNodeFormatProcessor.class);
        processorMap.put(TUpdateSetASTNode.class, UpdateSetASTNodeFormatProcessor.class);

        // with process rules
        processorMap.put(TWithSqlStatement.class, WithStmtFormatProcessor.class);
        processorMap.put(TWithASTNode.class, WithASTNodeFormatProcessor.class);

        // case process rules
        processorMap.put(TCaseSqlStatement.class, CaseStmtFormatProcessor.class);
        processorMap.put(TCaseASTNode.class, CaseASTNodeFormatProcessor.class);

        // if stmt rules
        processorMap.put(TIfElseSqlStatement.class, IfStmtFormatProcessor.class);
        processorMap.put(TIfElseASTNode.class, IfStmtASTNodeFormatProcessor.class);

        // Loop rules
        processorMap.put(TLoopSqlStatement.class, LoopFormatProcessor.class);
        processorMap.put(TLoopASTNode.class, LoopASTNodeFormatProcessor.class);

        // Begin rules
        processorMap.put(TBeginSqlStatement.class, BeginFormatProcessor.class);
        processorMap.put(TBeginASTNode.class, BeginASTNodeFormatProcessor.class);

        processorMap.put(TDeclareSqlStatement.class, DeclareFormatProcessor.class);
        processorMap.put(TDeclareASTNode.class, DeclareASTNodeFormatProcessor.class);

        // create stmt
        processorMap.put(TCreateSqlStatement.class, CreateFormatProcessor.class);
        processorMap.put(TCreateASTNode.class, CreateASTNodeFormatProcessor.class);
        processorMap.put(TAsASTNode.class, AsASTNodeFormatProcessor.class);
        processorMap.put(TLaungageASTNode.class, LanguageASTNodeFormatProcessor.class);
        processorMap.put(TReturnASTNode.class, ReturnsASTNodeFormatProcessor.class);

        // loop stmt
        processorMap.put(TForLoopSqlStatement.class, ForLoopFormatProcessor.class);
        processorMap.put(TForLoopASTNode.class, ForLoopASTNodeFormatProcessor.class);

        // cursor stmt
        processorMap.put(TCursorSqlStatement.class, CursorStmtFormatProcessor.class);
        processorMap.put(TCursorASTNode.class, CursorStmtASTNodeFormatProcessor.class);

        // union stmt
        processorMap.put(TUnionSqlStatement.class, UnionFormatProcessor.class);
        processorMap.put(TUnionASTNode.class, UnionASTNodeFormatProcessor.class);

        // merge stmt
        processorMap.put(TMergeSqlStatement.class, MergeStmtFormatProcessor.class);
        processorMap.put(TMergeASTNode.class, MergeASTNodeFormatProcessor.class);
        processorMap.put(TMergeWhenASTNode.class, MergeWithASTNodeFormatProcessor.class);

        // common sql rules
        processorMap.put(TSqlNode.class, SqlNodeFormatProcessor.class);
        processorMap.put(TExpression.class, ExpressionProcessor.class);
        processorMap.put(TCTEExpression.class, FullStmtCTEExpressionProcessor.class);
        processorMap.put(TCTEASTNode.class, CTEASTNodeFormatProcessor.class);
        processorMap.put(TFromASTNode.class, FromASTNodeFormatProcessor.class);
        processorMap.put(TWhereASTNode.class, WhereASTNodeFormatProcessor.class);
        processorMap.put(TReturningASTNode.class, ReturningASTNodeFormatProcessor.class);
        processorMap.put(TFullStmt.class, FullStatementProcessor.class);

    }

    /**
     * abstract processor.
     *
     * @param startNode the start node
     * @return the processor
     */
    public static AbstractProcessor<TParseTreeNode> getProcessor(TParseTreeNode startNode) {
        Class parseNodeClass = processorMap.get(startNode.getClass());

        if (null == parseNodeClass) {
            return null;
        }

        AbstractProcessor<TParseTreeNode> processor = null;
        try {
            processor = (AbstractProcessor<TParseTreeNode>) parseNodeClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            MPPDBIDELoggerUtility.error("Exception occured while getting instance of parseNode class" + startNode);

        }
        return processor;
    }

}
