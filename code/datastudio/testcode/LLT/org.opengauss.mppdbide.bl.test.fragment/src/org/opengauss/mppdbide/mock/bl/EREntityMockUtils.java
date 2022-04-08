package org.opengauss.mppdbide.mock.bl;

import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

public class EREntityMockUtils {

    public static void getOLAPTableConstraintDetails(PreparedStatementResultSetHandler preparedstatementHandler) {
        String functions =
            "SELECT c.contype as constrainttype, c.conkey as columnlist FROM pg_constraint c where c.conrelid = 1";
        MockResultSet functionsResultSet = preparedstatementHandler.createResultSet();
        functionsResultSet.addColumn("constrainttype");
        functionsResultSet.addColumn("columnlist");
        functionsResultSet.addRow(new Object[] {"p", "{1}"});
        preparedstatementHandler.prepareResultSet(functions, functionsResultSet);
    }

    public static void getOLAPColumnComments(PreparedStatementResultSetHandler preparedStatementResultSetHandler) {
        String functions =
            "SELECT a.attrelid,a.attname ,d.objsubid, d.description FROM pg_description d left join pg_attribute a on (d.objoid = a.attrelid and a.attnum = d.objsubid) where d.objoid = 1;";
        MockResultSet functionsResultSet = preparedStatementResultSetHandler.createResultSet();
        functionsResultSet.addColumn("attrelid");
        functionsResultSet.addColumn("attname");
        functionsResultSet.addColumn("objsubid");
        functionsResultSet.addColumn("description");
        functionsResultSet.addRow(new Object[] {1, "col1", 1, "this is to test column comments"});
        preparedStatementResultSetHandler.prepareResultSet(functions, functionsResultSet);
    }

    public static void getOLAPTableComments(PreparedStatementResultSetHandler preparedStatementResultSetHandler) {
        String functions = "SELECT d.description FROM pg_description d where d.objoid = " + 1 + " and d.objsubid = 0;";
        MockResultSet functionsResultSet = preparedStatementResultSetHandler.createResultSet();
        functionsResultSet.addColumn("description");
        functionsResultSet.addRow(new Object[] {"This is test for table comments"});
        preparedStatementResultSetHandler.prepareResultSet(functions, functionsResultSet);
    }

}
