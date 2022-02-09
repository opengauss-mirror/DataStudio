
package org.opengauss.mppdbide.mock.presentation;

import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

public class MockERPresentationUtils {

    public static void getForeignKeysConstraintDetailsQuery(
            PreparedStatementResultSetHandler preparedStatementResultSetHandler) {
        String functions = "SELECT CONS.OWNER, CONS.TABLE_NAME, CONS.CONSTRAINT_NAME, CONS.CONSTRAINT_TYPE, CONS.R_OWNER, CONS.R_TABLE_NAME, CONS.R_CONSTRAINT_NAME, SCD.COL_LIST FROM SYS.ALL_CONSTRAINTS CONS, SYS.CONSDEF$ SCD WHERE (CONS.R_OWNER=? OR CONS.OWNER=?) AND CONS.CONSTRAINT_TYPE ='R' AND CONS.CONSTRAINT_NAME = SCD.CONS_NAME";
        MockResultSet functionsResultSet = preparedStatementResultSetHandler.createResultSet();
        functionsResultSet.addColumn("OWNER");
        functionsResultSet.addColumn("TABLE_NAME");
        functionsResultSet.addColumn("CONSTRAINT_NAME");
        functionsResultSet.addColumn("CONSTRAINT_TYPE");
        functionsResultSet.addColumn("R_OWNER");
        functionsResultSet.addColumn("R_TABLE_NAME");
        functionsResultSet.addColumn("R_CONSTRAINT_NAME");
        functionsResultSet.addColumn("COL_LIST");
        functionsResultSet
            .addRow(new Object[] {"TEST_OWNER12", "TABLE8", "FORKKK", "R", "PUBLIC", "TEST3", "PK_TEST3", "1"});
        preparedStatementResultSetHandler.prepareResultSet(functions, functionsResultSet,
            (new Object[] {"MT1", "MT1"}));
    }
    
    public static void getTableForeignKeysConstraintDetails(
            PreparedStatementResultSetHandler preparedStatementResultSetHandler) {
        String functions = "SELECT CONS.OWNER, CONS.TABLE_NAME, CONS.CONSTRAINT_NAME, CONS.CONSTRAINT_TYPE, CONS.R_OWNER, CONS.R_TABLE_NAME, CONS.R_CONSTRAINT_NAME, SCD.COL_LIST FROM SYS.ALL_CONSTRAINTS CONS, SYS.CONSDEF$ SCD WHERE CONS.R_OWNER=? AND CONS.R_TABLE_NAME=? AND CONS.CONSTRAINT_TYPE ='R' AND CONS.CONSTRAINT_NAME = SCD.CONS_NAME";
        MockResultSet functionsResultSet = preparedStatementResultSetHandler.createResultSet();
        functionsResultSet.addColumn("OWNER");
        functionsResultSet.addColumn("TABLE_NAME");
        functionsResultSet.addColumn("CONSTRAINT_NAME");
        functionsResultSet.addColumn("CONSTRAINT_TYPE");
        functionsResultSet.addColumn("R_OWNER");
        functionsResultSet.addColumn("R_TABLE_NAME");
        functionsResultSet.addColumn("R_CONSTRAINT_NAME");
        functionsResultSet.addColumn("COL_LIST");
        functionsResultSet.addRow(new Object[] {"TEST_OWNER12", "TEST8", "FKKKKKKKKK", "R", "PUBLIC", "TEST3", "PK_TEST3", "0"});
        preparedStatementResultSetHandler.prepareResultSet(functions, functionsResultSet,
            (new Object[] {"MT1", "TEST3"}));
    }

    public static void getTableKeysConstraintDetails(
            PreparedStatementResultSetHandler preparedStatementResultSetHandler) {
        String functions = "SELECT CONS.OWNER, CONS.TABLE_NAME, CONS.CONSTRAINT_NAME, CONS.CONSTRAINT_TYPE, CONS.R_OWNER, CONS.R_TABLE_NAME, CONS.R_CONSTRAINT_NAME, SCD.COL_LIST FROM SYS.ALL_CONSTRAINTS CONS, SYS.CONSDEF$ SCD WHERE CONS.OWNER=? AND CONS.TABLE_NAME=? AND CONS.CONSTRAINT_TYPE <>'C' AND CONS.CONSTRAINT_NAME = SCD.CONS_NAME";
        MockResultSet functionsResultSet = preparedStatementResultSetHandler.createResultSet();
        functionsResultSet.addColumn("OWNER");
        functionsResultSet.addColumn("TABLE_NAME");
        functionsResultSet.addColumn("CONSTRAINT_NAME");
        functionsResultSet.addColumn("CONSTRAINT_TYPE");
        functionsResultSet.addColumn("R_OWNER");
        functionsResultSet.addColumn("R_TABLE_NAME");
        functionsResultSet.addColumn("R_CONSTRAINT_NAME");
        functionsResultSet.addColumn("COL_LIST");
        functionsResultSet.addRow(new Object[] {"TEST_OWNER", "TEST3", "FK_TEST3", "R", "SYS", "TEST4", "PK_TEST3", "1"});
        preparedStatementResultSetHandler.prepareResultSet(functions, functionsResultSet,
            (new Object[] {"MT1", "TEST3"}));
    }

    public static void getTableColumnComments(PreparedStatementResultSetHandler preparedStatementResultSetHandler) {
        String functions = "SELECT TAB.COLUMN_NAME, COM.COMMENTS, TAB.CHAR_USED FROM SYS.ALL_TAB_COLUMNS TAB LEFT JOIN SYS.DBA_COL_COMMENTS COM ON TAB.TABLE_NAME = COM.TABLE_NAME AND TAB.COLUMN_NAME = COM.COLUMN_NAME WHERE TAB.OWNER=? AND TAB.TABLE_NAME=?";
        MockResultSet functionsResultSet = preparedStatementResultSetHandler.createResultSet();
        functionsResultSet.addColumn("COLUMN_NAME");
        functionsResultSet.addColumn("COMMENTS");
        functionsResultSet.addColumn("CHAR_USED");
        functionsResultSet.addRow(new Object[] {"INFO", " ", "C"});
        preparedStatementResultSetHandler.prepareResultSet(functions, functionsResultSet,
            (new Object[] {"MT1", "TEST3"}));
    }
}
