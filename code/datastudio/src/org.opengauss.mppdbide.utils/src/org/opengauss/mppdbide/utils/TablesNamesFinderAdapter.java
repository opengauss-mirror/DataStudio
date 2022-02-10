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

package org.opengauss.mppdbide.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.ArrayExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.CollateExpression;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NextValExpression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.FullTextSearch;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NamedExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.expression.operators.relational.SimilarToExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Block;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.CreateFunctionalStatement;
import net.sf.jsqlparser.statement.DeclareStatement;
import net.sf.jsqlparser.statement.DescribeStatement;
import net.sf.jsqlparser.statement.ExplainStatement;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.ShowColumnsStatement;
import net.sf.jsqlparser.statement.ShowStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.UseStatement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.sequence.AlterSequence;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.schema.CreateSchema;
import net.sf.jsqlparser.statement.create.sequence.CreateSequence;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.ParenthesisFromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

/**
 * 
 * Title: class
 * 
 * Description: The Class TablesNamesFinderAdapter.
 *
 * @since 3.0.0
 */
public class TablesNamesFinderAdapter implements SelectVisitor, FromItemVisitor, ExpressionVisitor, ItemsListVisitor,
        SelectItemVisitor, StatementVisitor {

    private List<String> tablesList;
    private boolean allowColumnProcessing = false;
    private boolean isSelectFromFunction = false;
    private boolean isJoinExists = false;

    /**
     * There are some special names which are not tables but parsed as
     * tables.Those names will be collected here and excluded from tablesList
     * -names anymore
     */
    private List<String> otherItemNamesList;

    /**
     * Gets the table list.
     *
     * @param ps the ps
     * @return the table list
     */
    public List<String> getTableList(PlainSelect ps) {
        collectTables(ps);
        return tablesList;
    }

    /**
     * Gets the table list.
     *
     * @param statement the statement
     * @return the table list
     */
    public List<String> getTableList(Statement statement) {
        init(false);
        statement.accept(this);
        return tablesList;
    }

    /**
     * Collect tables.
     *
     * @param ps the ps
     */
    private void collectTables(PlainSelect ps) {
        init(false);
        if (checkForObjectNull(ps.getFromItem())) {
            ps.getFromItem().accept(this);
        }
        List<Join> joins = ps.getJoins();
        if (joins != null) {
            this.isJoinExists = true;
            for (Join j : joins) {
                j.getRightItem().accept(this);
            }
        }
    }

    /**
     * Check for object null.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private boolean checkForObjectNull(Object obj) {
        return obj != null;
    }

    /**
     * Checks if is select from function.
     *
     * @return true, if is select from function
     */
    public boolean isSelectFromFunction() {
        return this.isSelectFromFunction;
    }

    /**
     * Checks if is join exists.
     *
     * @return true, if is join exists
     */
    public boolean isJoinExists() {
        return this.isJoinExists;
    }

    @Override
    public void visit(WithItem withItm) {
        otherItemNamesList.add(withItm.getName().toLowerCase(Locale.ENGLISH));
        withItm.getSelectBody().accept(this);
    }

    @Override
    public void visit(PlainSelect plnSelect) {
        if (checkForObjectNull(plnSelect.getSelectItems())) {
            for (SelectItem selectedItem : plnSelect.getSelectItems()) {
                selectedItem.accept(this);
            }
        }

        if (checkForObjectNull(plnSelect.getFromItem())) {
            plnSelect.getFromItem().accept(this);
        }

        if (checkForObjectNull(plnSelect.getJoins())) {
            getJoinItem(plnSelect);
        }
        if (checkForObjectNull(plnSelect.getWhere())) {
            plnSelect.getWhere().accept(this);
        }

        if (checkForObjectNull(plnSelect.getHaving())) {
            plnSelect.getHaving().accept(this);
        }

        if (checkForObjectNull(plnSelect.getOracleHierarchical())) {
            plnSelect.getOracleHierarchical().accept(this);
        }
    }

    /**
     * Gets the join item.
     *
     * @param plnSelect the pln select
     * @return the join item
     */
    private void getJoinItem(PlainSelect plnSelect) {
        for (Join joinItm : plnSelect.getJoins()) {
            joinItm.getRightItem().accept(this);
        }
    }

    /**
     * Override to adapt the tableName generation (e.g. with / without schema).
     *
     * @param table the table
     * @return the string
     */
    protected String extractTableName(Table table) {
        return table.getFullyQualifiedName();
    }

    @Override
    public void visit(Table tableNme) {
        String tableCompleteName = extractTableName(tableNme);
        if (validateTable(tableCompleteName)) {
            tablesList.add(tableCompleteName);
        }
    }

    /**
     * Validate table.
     *
     * @param tableCompleteName the table complete name
     * @return true, if successful
     */
    private boolean validateTable(String tableCompleteName) {
        return !otherItemNamesList.contains(tableCompleteName.toLowerCase(Locale.ENGLISH))
                && !tablesList.contains(tableCompleteName);
    }

    @Override
    public void visit(SubSelect subSelect) {
        if (subSelect.getWithItemsList() != null) {
            getWithItem(subSelect);
        }
        subSelect.getSelectBody().accept(this);
    }

    /**
     * Gets the with item.
     *
     * @param subSelect the sub select
     * @return the with item
     */
    private void getWithItem(SubSelect subSelect) {
        for (WithItem withItm : subSelect.getWithItemsList()) {
            withItm.accept(this);
        }
    }

    @Override
    public void visit(Addition addition) {
        visitBinaryExpression(addition);
    }

    @Override
    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression);
    }

    @Override
    public void visit(Between betn) {
        betn.getLeftExpression().accept(this);
        betn.getBetweenExpressionStart().accept(this);
        betn.getBetweenExpressionEnd().accept(this);
    }

    @Override
    public void visit(Column tblColumn) {
        if (validateColumn(tblColumn)) {
            visit(tblColumn.getTable());
        }
    }

    /**
     * Validate column.
     *
     * @param tblColumn the tbl column
     * @return true, if successful
     */
    private boolean validateColumn(Column tblColumn) {
        return allowColumnProcessing && tblColumn.getTable() != null && tblColumn.getTable().getName() != null;
    }

    @Override
    public void visit(Division divn) {
        visitBinaryExpression(divn);
    }

    @Override
    public void visit(DoubleValue doubleValue) {
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo);
    }

    @Override
    public void visit(Function func) {
        ExpressionList expressionList = func.getParameters();
        if (expressionList != null) {
            visit(expressionList);
        }
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpressn) {
        visitLeftExpression(inExpressn);
        inExpressn.getRightItemsList().accept(this);
    }

    /**
     * Visit left expression.
     *
     * @param inExpressn the in expressn
     */
    private void visitLeftExpression(InExpression inExpressn) {
        if (inExpressn.getLeftExpression() != null) {
            inExpressn.getLeftExpression().accept(this);
        } else if (inExpressn.getLeftItemsList() != null) {
            inExpressn.getLeftItemsList().accept(this);
        }
    }

    @Override
    public void visit(SignedExpression signedExpression) {
        signedExpression.getExpression().accept(this);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        return;
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        return;
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression);
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        existsExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(LongValue longValue) {
        return;
    }

    @Override
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals);
    }

    @Override
    public void visit(Multiplication multiplication) {
        visitBinaryExpression(multiplication);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo);
    }

    @Override
    public void visit(NullValue nullValue) {
        return;
    }

    @Override
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }

    @Override
    public void visit(StringValue stringValue) {
        return;
    }

    @Override
    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction);
    }

    @Override
    public void visit(NotExpression notExpr) {
        notExpr.getExpression().accept(this);
    }

    /**
     * Visit binary expression.
     *
     * @param binaryExpressn the binary expressn
     */
    public void visitBinaryExpression(BinaryExpression binaryExpressn) {
        binaryExpressn.getLeftExpression().accept(this);
        binaryExpressn.getRightExpression().accept(this);
    }

    @Override
    public void visit(ExpressionList expressnList) {
        for (Expression expressn : expressnList.getExpressions()) {
            expressn.accept(this);
        }
    }

    @Override
    public void visit(DateValue dateValue) {
        return;
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        return;
    }

    @Override
    public void visit(TimeValue timeValue) {
        return;
    }

    /**
     * Visit.
     *
     * @param caseExpressn the case expressn
     */
    @Override
    public void visit(CaseExpression caseExpressn) {
        visitSwitchExpression(caseExpressn);
        visitWhenClauseExpression(caseExpressn);
        visitCaseExpression(caseExpressn);
    }

    /**
     * Visit case expression.
     *
     * @param caseExpressn the case expressn
     */
    private void visitCaseExpression(CaseExpression caseExpressn) {
        if (caseExpressn.getElseExpression() != null) {
            caseExpressn.getElseExpression().accept(this);
        }
    }

    /**
     * Visit when clause expression.
     *
     * @param caseExpressn the case expressn
     */
    private void visitWhenClauseExpression(CaseExpression caseExpressn) {
        if (caseExpressn.getWhenClauses() != null) {
            for (Expression when : caseExpressn.getWhenClauses()) {
                when.accept(this);
            }
        }
    }

    /**
     * Visit switch expression.
     *
     * @param caseExpressn the case expressn
     */
    private void visitSwitchExpression(CaseExpression caseExpressn) {
        if (caseExpressn.getSwitchExpression() != null) {
            caseExpressn.getSwitchExpression().accept(this);
        }
    }

    /**
     * Visit.
     *
     * @param whenClause the when clause
     * net.sf.jsqlparser.expression.ExpressionVisitor#visit(net.sf.jsqlparser.
     * expression.WhenClause)
     */
    @Override
    public void visit(WhenClause whenClause) {
        if (whenClause.getWhenExpression() != null) {
            whenClause.getWhenExpression().accept(this);
        }
        if (whenClause.getThenExpression() != null) {
            whenClause.getThenExpression().accept(this);
        }
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        allComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        anyComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }

    @Override
    public void visit(SubJoin subjoin) {
        subjoin.getLeft().accept(this);
        this.isJoinExists = true;
    }

    @Override
    public void visit(Concat concat) {
        visitBinaryExpression(concat);
    }

    @Override
    public void visit(Matches matches) {
        visitBinaryExpression(matches);
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        visitBinaryExpression(bitwiseAnd);
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        visitBinaryExpression(bitwiseOr);
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        visitBinaryExpression(bitwiseXor);
    }

    @Override
    public void visit(CastExpression cast) {
        cast.getLeftExpression().accept(this);
    }

    @Override
    public void visit(Modulo modulo) {
        visitBinaryExpression(modulo);
    }

    @Override
    public void visit(AnalyticExpression analytic) {
        return;
    }

    @Override
    public void visit(SetOperationList opernlist) {
        for (SelectBody plnSelect : opernlist.getSelects()) {
            plnSelect.accept(this);
        }
    }

    @Override
    public void visit(ExtractExpression eexpr) {
        return;
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        lateralSubSelect.getSubSelect().getSelectBody().accept(this);
    }

    @Override
    public void visit(MultiExpressionList multiExprnList) {
        for (ExpressionList exprnList : multiExprnList.getExprList()) {
            exprnList.accept(this);
        }
    }

    @Override
    public void visit(ValuesList valuesList) {
        return;
    }

    /**
     * Initializes table names collector. Important is the usage of Column
     * instances to find table names. This is only allowed for expression
     * parsing, where a better place for tablenames could not be there. For
     * complete statements only from items are used to avoid some alias as
     * tablenames.
     *
     * @param allowColProcessing the allow col processing
     */
    protected void init(boolean allowColProcessing) {
        otherItemNamesList = new ArrayList<String>();
        tablesList = new ArrayList<String>();
        this.allowColumnProcessing = allowColProcessing;
    }

    @Override
    public void visit(IntervalExpression iexpr) {
        return;
    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        return;
    }

    @Override
    public void visit(OracleHierarchicalExpression oracleexpr) {
        if (oracleexpr.getStartExpression() != null) {
            oracleexpr.getStartExpression().accept(this);
        }

        if (oracleexpr.getConnectExpression() != null) {
            oracleexpr.getConnectExpression().accept(this);
        }
    }

    @Override
    public void visit(RegExpMatchOperator regexpr) {
        visitBinaryExpression(regexpr);
    }

    @Override
    public void visit(RegExpMySQLOperator regexpr) {
        visitBinaryExpression(regexpr);
    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        return;
    }

    @Override
    public void visit(JsonOperator jsonExpr) {
        return;
    }

    @Override
    public void visit(AllColumns allColumns) {
        return;
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
    }

    @Override
    public void visit(SelectExpressionItem expressionItem) {
        expressionItem.getExpression().accept(this);
    }

    @Override
    public void visit(UserVariable var) {
        return;
    }

    @Override
    public void visit(NumericBind bind) {
        return;
    }

    @Override
    public void visit(KeepExpression aexpr) {
        return;
    }

    @Override
    public void visit(MySQLGroupConcat groupConcat) {
    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        for (Expression expression : rowConstructor.getExprList().getExpressions()) {
            expression.accept(this);
        }
    }

    @Override
    public void visit(HexValue hexValue) {
        return;
    }

    @Override
    public void visit(OracleHint hint) {
    }

    @Override
    public void visit(TableFunction valuesList) {
        this.isSelectFromFunction = true;
    }

    @Override
    public void visit(TimeKeyExpression arg0) {
        return;
    }

    @Override
    public void visit(DateTimeLiteralExpression arg0) {
        return;
    }

    @Override
    public void visit(BitwiseRightShift arg0) {
        return;
    }

    @Override
    public void visit(BitwiseLeftShift arg0) {
        return;
    }

    @Override
    public void visit(ValueListExpression arg0) {
        return;
    }

    @Override
    public void visit(ParenthesisFromItem arg0) {
        return;
    }

    @Override
    public void visit(Commit arg0) {
        return;
    }

    @Override
    public void visit(Delete delete) {
        tablesList.add(delete.getTable().getName());
        if (delete.getWhere() != null) {
            delete.getWhere().accept(this);
        }
    }

    @Override
    public void visit(Update update) {

        tablesList.add(update.getTable().getName());
        if (update.getExpressions() != null) {
            for (Expression expression : update.getExpressions()) {
                expression.accept(this);
            }
        }

        if (update.getFromItem() != null) {
            update.getFromItem().accept(this);
        }

        if (update.getJoins() != null) {
            for (Join join : update.getJoins()) {
                join.getRightItem().accept(this);
            }
        }

        if (update.getWhere() != null) {
            update.getWhere().accept(this);
        }

    }

    @Override
    public void visit(Insert insert) {

        tablesList.add(insert.getTable().getName());
        if (insert.getItemsList() != null) {
            insert.getItemsList().accept(this);
        }
        if (insert.getSelect() != null) {
            visit(insert.getSelect());
        }

    }

    @Override
    public void visit(Replace replace) {
        tablesList.add(replace.getTable().getName());
        if (replace.getExpressions() != null) {
            for (Expression expression : replace.getExpressions()) {
                expression.accept(this);
            }
        }
        if (replace.getItemsList() != null) {
            replace.getItemsList().accept(this);
        }

    }

    @Override
    public void visit(Drop arg0) {
        return;
    }

    @Override
    public void visit(Truncate arg0) {
        return;
    }

    @Override
    public void visit(CreateIndex arg0) {
        return;
    }

    @Override
    public void visit(CreateTable create) {
        tablesList.add(create.getTable().getFullyQualifiedName());
        if (create.getSelect() != null) {
            create.getSelect().accept(this);
        }

    }

    @Override
    public void visit(CreateView arg0) {
        return;
    }

    @Override
    public void visit(AlterView arg0) {
        return;
    }

    @Override
    public void visit(Alter arg0) {
        return;
    }

    @Override
    public void visit(Statements arg0) {
        return;
    }

    @Override
    public void visit(Execute arg0) {
        return;
    }

    @Override
    public void visit(SetStatement arg0) {
        return;
    }

    @Override
    public void visit(Merge arg0) {
        return;
    }

    @Override
    public void visit(Select arg0) {
        return;
    }

    @Override
    public void visit(Upsert arg0) {
        return;
    }

    @Override
    public void visit(UseStatement arg0) {
        return;
    }

    /**
     * <Detailed description of function>.
     *
     * @param ps the ps
     * @return the table list
     */
    public List<String> getTableList(Table ps) {
        init(false);
        ps.accept(this);
        return tablesList;
    }

    @Override
    public void visit(Comment comment) {
    }

    @Override
    public void visit(CreateSchema aThis) {
    }

    @Override
    public void visit(ShowColumnsStatement set) {
    }

    @Override
    public void visit(Block block) {
    }

    @Override
    public void visit(DescribeStatement describe) {
    }

    @Override
    public void visit(ExplainStatement aThis) {
    }

    @Override
    public void visit(ShowStatement aThis) {
    }

    @Override
    public void visit(DeclareStatement aThis) {
    }

    @Override
    public void visit(Grant grant) {
    }

    @Override
    public void visit(CreateSequence createSequence) {
    }

    @Override
    public void visit(AlterSequence alterSequence) {
    }

    @Override
    public void visit(CreateFunctionalStatement createFunctionalStatement) {
    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {
    }

    @Override
    public void visit(IntegerDivision division) {
    }

    @Override
    public void visit(FullTextSearch fullTextSearch) {
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
    }

    @Override
    public void visit(NextValExpression aThis) {
    }

    @Override
    public void visit(CollateExpression aThis) {
    }

    @Override
    public void visit(SimilarToExpression aThis) {
    }

    @Override
    public void visit(ArrayExpression aThis) {
    }

    @Override
    public void visit(ValuesStatement aThis) {
    }
}
