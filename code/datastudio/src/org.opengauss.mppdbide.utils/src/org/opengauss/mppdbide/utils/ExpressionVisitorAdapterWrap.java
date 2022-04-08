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

import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExpressionVisitorAdapterWrap.
 *
 * @since 3.0.0
 */
public class ExpressionVisitorAdapterWrap extends ExpressionVisitorAdapter {
    private boolean hasNonEditableSelectItem = false;

    /**
     * Checks for non editable select item.
     *
     * @return true, if successful
     */
    public boolean hasNonEditableSelectItem() {
        return this.hasNonEditableSelectItem;
    }

    /**
     * Reset has non editable select item.
     */
    public void resetHasNonEditableSelectItem() {
        this.hasNonEditableSelectItem = false;
    }

    @Override
    public void visit(Function function) {
        this.hasNonEditableSelectItem = true;
        super.visit(function);
    }

    @Override
    public void visit(Addition expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(Division expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(Multiplication expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(Subtraction expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(AndExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(OrExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(Between expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(EqualsTo expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(GreaterThan expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(GreaterThanEquals expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(InExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(CaseExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(LikeExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(MinorThan expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(MinorThanEquals expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(NotEqualsTo expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(SubSelect subSelect) {
        this.hasNonEditableSelectItem = true;
        super.visit(subSelect);
    }

    @Override
    public void visit(AnyComparisonExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(Concat expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(Matches expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(BitwiseAnd expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(BitwiseOr expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(BitwiseXor expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(CastExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(Modulo expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(AnalyticExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(ExtractExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(IntervalExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(RegExpMatchOperator expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(NotExpression notExpr) {
        this.hasNonEditableSelectItem = true;
        super.visit(notExpr);
    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        this.hasNonEditableSelectItem = true;
        super.visit(jsonExpr);
    }

    @Override
    public void visit(JsonOperator expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(KeepExpression expr) {
        this.hasNonEditableSelectItem = true;
        super.visit(expr);
    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        this.hasNonEditableSelectItem = true;
        super.visit(rowConstructor);
    }

    @Override
    public void visit(BitwiseRightShift expr) {
        this.hasNonEditableSelectItem = true;
        visitBinaryExpression(expr);
    }

    @Override
    public void visit(BitwiseLeftShift expr) {
        this.hasNonEditableSelectItem = true;
        visitBinaryExpression(expr);
    }
}
