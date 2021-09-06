/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

/**
 * 
 * Title: class
 * 
 * Description: The Class JSQLParserUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class JSQLParserUtils {
    /*
     * Rules For Disabling Editing of Query Results
     * 
     */

    /**
     * Checks if is query result edit supported.
     *
     * @param query the query
     * @return true, if is query result edit supported
     * @throws DatabaseCriticalException the database critical exception
     */
    public static boolean isQueryResultEditSupported(String query) throws DatabaseCriticalException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select selectStatement = null;
        SelectVisitorWrap selVisitor = new SelectVisitorWrap();
        try {
            Statement stmt = parserManager.parse(new StringReader(query));
            if (!(stmt instanceof Select)) {
                return false;
            }
            selectStatement = (Select) stmt;

            PlainSelect ps = null;
            if (selectStatement.getSelectBody() instanceof PlainSelect) {
                ps = (PlainSelect) selectStatement.getSelectBody();
            }
            selectStatement.getSelectBody().accept(selVisitor);

            if (!selVisitor.hasSetOperations() && !isMoreThanOneTableinFrom(ps) && !isGroupByExists(ps)) {
                if (!isWithItemExists(selectStatement) && isAllowedSelectItems(ps) && !isHavingExists(ps)
                        && !isIntoExists(ps)) {
                    return true;
                }
            }
        } catch (OutOfMemoryError error) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED),
                    error);
            throw new DatabaseCriticalException(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED, error);

        } catch (JSQLParserException e1) {
            return false;
        } catch (Exception e2) {
            // Jsql Parser can throw Null pointer exception for unsupported
            // syntax such as OVER in select statement,We are handling such
            // exception here.
            return false;
        } catch (Error e3) {
            return false;
        }

        return false;
    }

    /**
     * Checks if is copy query.
     *
     * @param query the query
     * @return true if fixed
     * @description Is Copy Query
     */
    public static boolean isCopyQuery(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select selectStatement = null;
        try {
            Statement stmt = parserManager.parse(new StringReader(query));
            if (!(stmt instanceof Select)) {
                return false;
            }
            selectStatement = (Select) stmt;

            PlainSelect ps = null;
            if (selectStatement.getSelectBody() instanceof PlainSelect) {
                ps = (PlainSelect) selectStatement.getSelectBody();
            }
            if (isIntoExists(ps)) {
                return true;
            }
        } catch (JSQLParserException e1) {
            return false;
        } catch (Exception e2) {
            return false;
        }
        return false;
    }

    /**
     * Checks if is with item exists.
     *
     * @param selectStatement the select statement
     * @return true, if is with item exists
     */
    private static boolean isWithItemExists(Select selectStatement) {
        if (selectStatement == null) {
            return false;
        }
        List<WithItem> withItemList = selectStatement.getWithItemsList();
        if (withItemList != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if is more than one tablein from.
     *
     * @param ps the ps
     * @return true, if is more than one tablein from
     */
    private static boolean isMoreThanOneTableinFrom(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        TablesNamesFinderAdapter tablesNamesFinder = new TablesNamesFinderAdapter();
        int tableCount = tablesNamesFinder.getTableList(ps).size();
        if ((tableCount != 1) || tablesNamesFinder.isSelectFromFunction() || tablesNamesFinder.isJoinExists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the qualified table name.Get NamespaceName.TableName from query
     *
     * @param query the query
     * @return the qualified table name
     */
    public static String getQualifiedTableName(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select selectStatement = null;
        PlainSelect ps = null;
        try {
            Statement stmt = parserManager.parse(new StringReader(query));
            if (stmt instanceof Select) {
                selectStatement = (Select) stmt;
            }

            if (selectStatement == null) {
                return "";
            }

            if (selectStatement.getSelectBody() instanceof PlainSelect) {
                ps = (PlainSelect) selectStatement.getSelectBody();
            }

            if (ps == null) {
                return "";
            }
        } catch (JSQLParserException e1) {
            return "";
        } catch (Exception e2) {
            return "";
        } catch (Error e3) {
            return "";
        }

        TablesNamesFinderAdapter tablesNamesFinder = new TablesNamesFinderAdapter();
        List<String> tableList = tablesNamesFinder.getTableList(ps);
        if (tableList.size() == 1) {
            return tableList.get(0);
        }
        return "";
    }

    /**
     * Gets the select query main table name.
     *
     * @param query the query
     * @return the select query main table name
     */
    public static String getSelectQueryMainTableName(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select selectStatement = null;
        PlainSelect ps = null;
        SetOperationList operationList = null;
        try {
            Statement stmt = parserManager.parse(new StringReader(query));
            if (stmt instanceof Select) {
                selectStatement = (Select) stmt;
            }

            if (selectStatement == null) {
                return "";
            }

            if (selectStatement.getSelectBody() instanceof PlainSelect) {
                ps = (PlainSelect) selectStatement.getSelectBody();
            } else if (selectStatement.getSelectBody() instanceof SetOperationList) {
                operationList = (SetOperationList) selectStatement.getSelectBody();
                if (null != operationList.getSelects() && operationList.getSelects().size() > 0) {
                    List<SelectBody> plainSelectsList = operationList.getSelects();
                    if (!plainSelectsList.isEmpty()) {
                        ps = (PlainSelect) plainSelectsList.get(0);
                    }
                }
            }

            if (ps == null) {
                return "";
            }
        } catch (JSQLParserException e11) {
            return "";
        } catch (Exception e22) {
            return "";
        } catch (Error e33) {
            return "";
        }

        TablesNamesFinderAdapter tablesNameFinder = new TablesNamesFinderAdapter();
        List<String> tableList = tablesNameFinder.getTableList(ps);
        if (tableList.size() >= 1) {
            return tableList.get(0);
        }
        return "";
    }

    /**
     * Gets the split qualified name.
     *
     * @param name the name
     * @param isStripQuote the is strip quote
     * @return the split qualified name
     */
    public static String[] getSplitQualifiedName(String name, boolean isStripQuote) {
        if (name == null) {
            return new String[0];
        }
        List<String> strList = new ArrayList<String>(1);
        int size;
        if (name.contains("\"")) {
            if (!name.contains(".")) {
                strList.add(name);
            } else {
                String[] splitStr = name.split("\\.");
                int[] dQuoteCntr = populateDQuoteCntr(splitStr);
                int index = getCorrectSplitPoint(dQuoteCntr);

                if (index != -1) {
                    StringBuilder str = new StringBuilder(splitStr[0]);
                    for (int jindex = 1; jindex < index; jindex++) {
                        str.append('.').append(splitStr[jindex]);
                    }

                    strList.add(str.toString());
                    str = new StringBuilder(splitStr[index]);
                    for (int cnt = index + 1; cnt < splitStr.length; cnt++) {
                        str.append('.').append(splitStr[cnt]);
                    }

                    strList.add(str.toString());
                } else {
                    strList.add(name);
                }
            }

            if (isStripQuote) {
                size = strList.size();
                for (int indx = 0; indx < size; indx++) {
                    String str1 = strList.get(indx);
                    if (str1.startsWith("\"") && str1.endsWith("\"")) {
                        str1 = str1.substring(1, str1.length() - 1);
                        strList.remove(indx);
                        strList.add(indx, str1);
                    }
                }
            }

            return strList.toArray(new String[strList.size()]);
        } else if (name.contains(".")) {
            return name.split("\\.");
        } else {
            return new String[] {name};
        }
    }

    /**
     * Populate D quote cntr.
     *
     * @param splitStr the split str
     * @return the int[]
     */
    private static int[] populateDQuoteCntr(String[] splitStr) {
        int[] cntr = new int[splitStr.length];
        for (int cnt = 0; cnt < splitStr.length; cnt++) {
            cntr[cnt] = 0;
            int len = splitStr[cnt].length();
            for (int jindex = 0; jindex < len; jindex++) {
                if (splitStr[cnt].charAt(jindex) == '"') {
                    cntr[cnt]++;
                }
            }
        }
        return cntr;
    }

    /**
     * Gets the correct split point.
     *
     * @param dQuoteCntr the d quote cntr
     * @return the correct split point
     */
    private static int getCorrectSplitPoint(int[] dQuoteCntr) {
        int leftDQuoteCnt = dQuoteCntr[0];
        int rightDQuoteCnt = 0;
        for (int cnt = 1; cnt < dQuoteCntr.length; cnt++) {
            rightDQuoteCnt += dQuoteCntr[cnt];
        }

        if (leftDQuoteCnt % 2 == 0 && rightDQuoteCnt % 2 == 0) {
            return 1;
        }

        for (int index = 1; index < dQuoteCntr.length - 1; index++) {
            leftDQuoteCnt += dQuoteCntr[index];
            rightDQuoteCnt -= dQuoteCntr[index];
            if (leftDQuoteCnt % 2 == 0 && rightDQuoteCnt % 2 == 0) {
                return index + 1;
            }
        }

        return -1;
    }

    /**
     * Checks if is group by exists.
     *
     * @param ps the ps
     * @return true, if is group by exists
     */
    private static boolean isGroupByExists(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        GroupByElement groupBy = ps.getGroupBy();
        if (groupBy == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if is allowed select items.
     *
     * @param ps the ps
     * @return true, if is allowed select items
     */
    private static boolean isAllowedSelectItems(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        ExpressionVisitorAdapterWrap expVisitor = new ExpressionVisitorAdapterWrap();
        List<SelectItem> sel = ps.getSelectItems();
        if (sel != null) {
            for (int index = 0; index < sel.size(); index++) {
                if (sel.get(index) instanceof AllColumns || sel.get(index) instanceof AllTableColumns) {
                    continue;
                }

                SelectExpressionItem selExpItem = (SelectExpressionItem) sel.get(index);
                /* For Aliases */
                if (selExpItem.getAlias() != null) {
                    return false;
                } else {
                    selExpItem.getExpression().accept(expVisitor);
                    if (expVisitor.hasNonEditableSelectItem()) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Checks if is having exists.
     *
     * @param ps the ps
     * @return true, if is having exists
     */
    private static boolean isHavingExists(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        Expression having = ps.getHaving();
        if (having == null) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean isIntoExists(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        List<Table> into = ps.getIntoTables();
        if (into == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the qualified tbl R view name.
     *
     * @param query the query
     * @return the qualified tbl R view name
     */
    public static String getQualifiedTblRViewName(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Alter alterStatement = null;
        CreateTable createTable = null;
        CreateView createView = null;
        Drop drop = null;
        Table tableType = null;
        StringReader reader = new StringReader(query);
        try {
            Statement stmt = parserManager.parse(reader);
            if (stmt instanceof Alter) {
                alterStatement = (Alter) stmt;
                tableType = (Table) alterStatement.getTable();
            } else if (stmt instanceof CreateView) {
                createView = (CreateView) stmt;
                tableType = (Table) createView.getView();
            } else if (stmt instanceof Drop) {
                drop = (Drop) stmt;
                tableType = (Table) drop.getName();
            }
            if (tableType == null) {
                return "";
            }
        } catch (JSQLParserException e11) {
            return "";
        } catch (Exception e22) {
            return "";
        } catch (Error e33) {
            return "";
        } finally {
            reader.close();
        }
        TablesNamesFinderAdapter tablesNameFinder = new TablesNamesFinderAdapter();
        List<String> tableList = tablesNameFinder.getTableList(tableType);
        if (tableList.size() >= 1) {
            return tableList.get(0);
        }
        return "";
    }

    /**
     * Sets the object type.
     *
     * @param query the query
     * @return the string
     */
    public static String setObjectType(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        String objType = null;
        StringReader reader = new StringReader(query);
        try {
            Statement stmt = parserManager.parse(reader);

            if (stmt instanceof Alter) {
                objType = MPPDBIDEConstants.ALTER_TABLE;
            } else if (stmt instanceof CreateView) {
                objType = MPPDBIDEConstants.CREATE_VIEW;
            } else if (stmt instanceof Drop) {
                if ("view".equalsIgnoreCase(((Drop) stmt).getType())) {
                    objType = MPPDBIDEConstants.DROP_VIEW;
                } else if ("table".equalsIgnoreCase(((Drop) stmt).getType())) {
                    objType = MPPDBIDEConstants.DROP_TABLE;
                }
            }

        } catch (JSQLParserException e11) {
            return "";
        } catch (Exception e22) {
            return "";
        } catch (Error e33) {
            return "";
        } finally {
            reader.close();
        }
        return objType;
    }
}