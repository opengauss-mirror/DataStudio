/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.format.processor.begin.BeginFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.begin.DeclareFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.begin.ast.BeginASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.begin.ast.DeclareASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.casestmt.CaseASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.casestmt.CaseStmtFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.common.CTEASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.common.ReturningASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.create.CreateFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.create.ast.AsASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.create.ast.CreateASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.create.ast.LanguageASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.create.ast.ReturnsASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.cursor.CursorStmtFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.cursor.ast.CursorStmtASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.delete.DeleteStmtFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.delete.ast.DeleteFromASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.delete.ast.DeleteUsingASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.forloop.ForLoopFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.forloop.ast.ForLoopASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.ifstmt.IfStmtFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.ifstmt.ast.IfStmtASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.insert.InsertStmtFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.insert.ast.InsertIntoASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.insert.ast.InsertValuesListASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.loop.LoopFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.loop.ast.LoopASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.merge.MergeStmtFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.merge.ast.MergeASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.merge.ast.MergeWithASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.node.SqlNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.FromASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.GroupByASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.HavingASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.LimitASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.OrderByASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.SelectASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.SelectStmtFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.WhereASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.select.WindowASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.union.UnionFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.union.ast.UnionASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.update.UpdateStmtFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.update.ast.UpdateIntoASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.update.ast.UpdateSetASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.with.WithASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.format.processor.with.WithStmtFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.begin.TBeginASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.begin.TDeclareASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.casestmt.TCaseASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TCTEASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TFromASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TOrderByASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TReturningASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TWhereASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TAsASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TCreateASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TLaungageASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TReturnASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.cursor.TCursorASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.delete.TDeleteFromASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.delete.TDeleteUsingASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.forloop.TForLoopASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.ifstmt.TIfElseASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertDefaultASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertIntoASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertValuesASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.limit.TLimitASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.limit.TOffsetASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.loop.TLoopASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeWhenASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select.TFetchASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select.TForASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select.TGroupByASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select.THavingASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select.TSelectASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select.TWindowASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.union.TUnionASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.update.TUpdateIntoASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.update.TUpdateSetASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.with.TWithASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.block.TBeginSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.block.TDeclareSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.common.TUnionSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.condition.TCaseSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.condition.TIfElseSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.debugobj.TCreateSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.debugobj.TCursorSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TDeleteSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TInsertSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TMergeSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TSelectSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TUpdateSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.dml.TWithSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.loop.TForLoopSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.loop.TLoopSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TCTEExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ProcessorFactory Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
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
