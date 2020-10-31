
package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.huawei.mppdbide.utils.TablesNamesFinderAdapter;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserTokenManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.UseStatement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.upsert.Upsert;

public class TablesNamesFinderAdapterTest {

    @Test
    public void test_getTableList() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Table table = new Table();
        List<String> result = tnfa.getTableList(table);
        assertTrue(result != null);
    }

    @Test
    public void test_getVisit() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        PlainSelect ps = new PlainSelect();
        List<Join> joins = new ArrayList<>();
        ps.setJoins(joins);
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        ps.setSelectItems(selectItems);
        OracleHierarchicalExpression oheExpression = new OracleHierarchicalExpression();
        ps.setOracleHierarchical(oheExpression);
        tnfa.visit(ps);
        assertTrue(ps != null);
    }

    @Test
    public void test_getVisit_whenInputIsReplace() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Table table = new Table("test");
        tnfa.getTableList(table);
        Replace replace = new Replace();
        replace.setTable(table);
        List<Expression> expressions = new ArrayList<>();
        replace.setExpressions(expressions);
        tnfa.visit(replace);
        assertTrue(replace != null);
    }

    @Test
    public void test_getVisit_whenInputIsDrop() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Drop drop = new Drop();
        tnfa.visit(drop);
        assertTrue(drop != null);
    }

    @Test
    public void test_getVisit_whenInputIsCreateTable() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Table table = new Table("test");
        tnfa.getTableList(table);
        CreateTable createTable = new CreateTable();
        createTable.setTable(table);
        createTable.setSelect(new Select(), false);
        tnfa.visit(createTable);
        assertTrue(createTable != null);
    }

    @Test
    public void test_getVisit_whenInputIsCreateView() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        CreateView createView = new CreateView();
        tnfa.visit(createView);
        assertTrue(createView != null);
    }
    
    @Test
    public void test_getVisit_whenInputIsUpsert() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Upsert upsert = new Upsert();
        tnfa.visit(upsert);
        assertTrue(upsert != null);
    }
    
    @Test
    public void test_getVisit_whenInputIsUseStatement() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        UseStatement useStatement = new UseStatement("test");
        tnfa.visit(useStatement);
        assertTrue(useStatement != null);
    }
    
    @Test
    public void test_getVisit_whenInputIsTruncate() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Truncate truncate = new Truncate();
        tnfa.visit(truncate);
        assertTrue(truncate != null);
    }
    
    @Test
    public void test_getVisit_whenInputIsCreateIndex() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        CreateIndex createIndex = new CreateIndex();
        tnfa.visit(createIndex);
        assertTrue(createIndex != null);
    }

    @Test
    public void test_getVisit_whenInputIsAlterView() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        AlterView alterView = new AlterView();
        tnfa.visit(alterView);
        assertTrue(alterView != null);
    }
    
    @Test
    public void test_getVisit_whenInputIsStatements() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Statements statements = new Statements();
        tnfa.visit(statements);
        assertTrue(statements != null);
    }

    @Test
    public void test_getVisit_whenInputIsAlter() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Alter alter = new Alter();
        tnfa.visit(alter);
        assertTrue(alter != null);
    }

    @Test
    public void test_getVisit_whenInputIsExecute() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        Execute execute = new Execute();
        tnfa.visit(execute);
        assertTrue(execute != null);
    }
    @Test
    public void test_getVisit_whenInputIsMultiExpression() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        MultiExpressionList execute = new MultiExpressionList();
        tnfa.visit(execute);
        assertTrue(execute != null);
    }
    
    @Test
    public void test_getVisit_whenInputIsHexValue() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        HexValue hexValue = new HexValue("64");
        tnfa.visit(hexValue);
        assertTrue(hexValue != null);
    }
   
    @Test
    public void test_getVisit_whenInputIsDoubleValue() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        DoubleValue doubleValue = new DoubleValue("64");
        tnfa.visit(doubleValue);
        assertTrue(doubleValue != null);
    }
    
    @Test
    public void test_getVisit_whenInputIsCaseExpression() {
        TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
        CaseExpression caseExpression = new CaseExpression();
        tnfa.visit(caseExpression);
        assertTrue(caseExpression != null);
    }
    @Test
    public void test_getVisit_whenInputIsBetween() throws JSQLParserException {
        try {
            TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
            Between bt = new Between();
            bt.setLeftExpression(new Column("a"));
            bt.setBetweenExpressionStart(new LongValue(1));
            bt.setBetweenExpressionEnd(new LongValue(10));
            tnfa.visit(bt);
            assertTrue(bt != null);
        } catch (Exception e) {
            fail("can\'t run here");
        }
    } 
    
    @Test
    public void test_getVisit_whenInputIsOrExpression() {
        try {
            EqualsTo left = new EqualsTo();
            left.setLeftExpression(new Column("a"));
            left.setRightExpression(new LongValue(1));
            
            EqualsTo right = new EqualsTo();
            right.setLeftExpression(new Column(""));
            right.setRightExpression(new LongValue(2));
            
            OrExpression or = new OrExpression(left, right);
            TablesNamesFinderAdapter tnfa = new TablesNamesFinderAdapter();
            tnfa.visit(or);
            assertNotNull(or);
        } catch (Exception e) {
            fail("can\'t run here");
        }
    }
    
    @Test
    public void test_getVisit_whenInputIsDelete() {
        try {
            Delete del = getDeleteSt();
            TablesNamesFinderAdapter ta = new TablesNamesFinderAdapter();
            Table table = new Table("t2");
            ta.getTableList(table);
            ta.visit(del);
            assertNotNull(del);
        } catch (Exception e) {
            fail("can\'t run here");
        }
    }
    
    @Test
    public void test_getVisit_whenInputIsWhenClause() {
        TablesNamesFinderAdapter ta = new TablesNamesFinderAdapter();
        WhenClause when = new WhenClause();
        when.setThenExpression(new StringValue("mon"));
        EqualsTo whenExpress = new EqualsTo();
        whenExpress.setLeftExpression(new Column("a"));
        whenExpress.setRightExpression(new LongValue(1));
        when.setWhenExpression(whenExpress);
        ta.visit(when);
        assertNotNull(when);
    }

    public static Delete getDeleteSt() {
        String sql = "delete from t1 where a = 100";
        return (Delete) getBaseStatement(sql).get();
    }

    public static Optional<Statement> getBaseStatement(String sql) {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        try {
            Statement st = pm.parse(new StringReader(sql));
            return Optional.of(st);
        } catch (JSQLParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Optional.empty();
        
    }
}
