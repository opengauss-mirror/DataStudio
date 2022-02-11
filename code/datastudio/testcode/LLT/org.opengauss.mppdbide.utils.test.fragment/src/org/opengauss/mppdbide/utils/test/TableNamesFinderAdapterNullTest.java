package org.opengauss.mppdbide.utils.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.opengauss.mppdbide.utils.TablesNamesFinderAdapter;

import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.UseStatement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.ParenthesisFromItem;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.upsert.Upsert;

public class TableNamesFinderAdapterNullTest {
    @Test
    public void test_getVist_doubleValue() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        DoubleValue doub = new DoubleValue("2.00");
        tnfa.visit(doub);
        assertTrue(doub != null);
    }
    
    @Test
    public void test_getVist_equalsTo() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        HexValue hex = new HexValue("1A");
        tnfa.visit(hex);
        assertTrue(hex != null);
    }
    
    @Test
    public void test_getVist_extractExpression() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        ExtractExpression exp = new ExtractExpression();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_intervalExpression() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        IntervalExpression exp = new IntervalExpression();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_jdbcNamedParam() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        JdbcNamedParameter exp = new JdbcNamedParameter();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_valuesList() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        ValuesList exp = new ValuesList();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_analyticExpression() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        AnalyticExpression exp = new AnalyticExpression();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_jsonExp() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        JsonExpression exp = new JsonExpression();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_jsonOp() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        JsonOperator exp = new JsonOperator(":");
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_allColmns() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        AllColumns exp = new AllColumns();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_allTabColmns() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        AllTableColumns exp = new AllTableColumns();
        tnfa.visit(exp);
        assertTrue(exp != null);
    } 
    
    @Test
    public void test_getVist_userVar() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        UserVariable exp = new UserVariable();
        tnfa.visit(exp);
        assertTrue(exp != null);
    } 
    
    @Test
    public void test_getVist_numericBind() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        NumericBind exp = new NumericBind();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_keepExp() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        KeepExpression exp = new KeepExpression();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_mySQLGroupConcat() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        MySQLGroupConcat exp = new MySQLGroupConcat();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_timeKeyExpression() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        TimeKeyExpression exp = new TimeKeyExpression("12");
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_dateTimeLiteralExpression() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        DateTimeLiteralExpression exp = new DateTimeLiteralExpression();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_valueListExpression() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        ValueListExpression exp = new ValueListExpression();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_parenthesisFromItem() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        ParenthesisFromItem exp = new ParenthesisFromItem();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_commit() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Commit exp = new Commit();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_drop() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Drop exp = new Drop();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_truncat() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Truncate exp = new Truncate();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_createView() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        CreateView exp = new CreateView();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_AlterView() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        AlterView exp = new AlterView();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_Alter() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Alter exp = new Alter();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_createIndex() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        CreateIndex exp = new CreateIndex();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_statement() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Statements exp = new Statements();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_Setstatement() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        SetStatement exp = new SetStatement("str", null);
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_merge() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Merge exp = new Merge();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    
    @Test
    public void test_getVist_Select() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Select exp = new Select();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_Upsert() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Upsert exp = new Upsert();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_useStatement() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        UseStatement exp = new UseStatement("struse");
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_jdbcParam() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        JdbcParameter exp = new JdbcParameter();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    
    @Test
    public void test_getVist_oraclehint() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        OracleHint exp = new OracleHint();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_rightshift() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        BitwiseRightShift exp = new  BitwiseRightShift();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    @Test
    public void test_getVist_leftshift() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        BitwiseLeftShift exp = new  BitwiseLeftShift();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    
    @Test
    public void test_getVist_nullVal() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        NullValue exp = new NullValue();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
    
    @Test
    public void test_getVist_isNUllexp() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        IsNullExpression exp = new IsNullExpression();
        tnfa.visit(exp);
        assertTrue(exp != null);
    }
    
}