/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * Title: ParserUtils Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public abstract class ParserUtils {

    /**
     * to add the comments to give ParsetreeNode
     * 
     * @param listIterator token items list
     * @param lSelect the node to which comments need to be added
     */
    public static void addCommentsR(ListIterator<ISQLTokenData> listIterator, TParseTreeNode lSelect) {

        if (listIterator.hasNext()) {
            ISQLTokenData next = listIterator.next();

            if (null != next.getSubTokenBean()) {
                listIterator.previous();
                return;
            }

            if (isTokenEmpty(next)) {
                listIterator.previous();
                readSpaces(listIterator, lSelect);
                addCommentsR(listIterator, lSelect);
            } else if (next.getToken().getData() == ISQLSyntax.SQL_MULTILINE_COMMENT
                    || next.getToken().getData() == ISQLSyntax.SQL_COMMENT) {
                lSelect.addComment(next.getTokenStr().toString());
                addCommentsR(listIterator, lSelect);
            } else {
                listIterator.previous();
            }
        }
    }

    /**
     * read the whitespace characters after the give node
     * 
     * @param listIterator token items list
     * @param lSelect the node to which comments need to be added
     */
    public static void readSpaces(ListIterator<ISQLTokenData> listIterator, TParseTreeNode lSelect) {

        if (listIterator.hasNext()) {
            ISQLTokenData next = listIterator.next();
            if (isTokenEmpty(next)) {
                // need to decide while format, need to split these or not
                // if contains line seperator then add one line seperator
                if (next.getTokenStr().contains(System.lineSeparator()) || next.getTokenStr().contains("\n")) {
                    lSelect.addComment(System.lineSeparator());
                }
                readSpaces(listIterator, lSelect);
            } else {
                listIterator.previous();
            }
        }
    }

    /**
     * to check weather the token is empty or not
     * 
     * @param next the token which need to be check for null string
     * @return true when it is empty, false if not empty. false when it is null
     */
    public static boolean isTokenEmpty(ISQLTokenData next) {
        if (null == next.getTokenStr()) {
            return false;
        }

        return StringUtils.isEmpty(next.getTokenStr().trim());
    }

    /**
     * where separation list
     * 
     * @return where separation list
     */
    public static List<String> getWhereSeperateList() {
        List<String> seperaterList = new ArrayList<String>();
        seperaterList.add("AND");
        seperaterList.add("OR");
        return seperaterList;
    }

    /**
     * read the list and return the token when it matches with the list of
     * tokens provided
     * 
     * @param listIterator token items list
     * @param tokens token list to match
     * @return the SqlNode when matches to the given tokens
     */
    public static TSqlNode handleToken(ListIterator<ISQLTokenData> listIterator, List<String> tokens) {
        return handleToken(listIterator, tokens, true);
    }

    /**
     * read the list and return the token when it matches with the list of
     * tokens provided
     * 
     * @param listIterator token items list
     * @param tokens token list to match
     * @param readComments to read the comments or not.
     * @return the SqlNode when matches to the given tokens
     */
    public static TSqlNode handleToken(ListIterator<ISQLTokenData> listIterator, List<String> tokens,
            boolean readComments) {
        ISQLTokenData sqlTokenData = getSQLTokenData(listIterator, tokens);

        return handleComments(listIterator, readComments, sqlTokenData);
    }

    /**
     * to read the commnets
     * 
     * @param listIterator token items list
     * @param readComments to read the comments or not.
     * @param sqlTokenData sqlToken Data Object
     * @return the SqlNode when matches to the given tokens
     */
    private static TSqlNode handleComments(ListIterator<ISQLTokenData> listIterator, boolean readComments,
            ISQLTokenData sqlTokenData) {
        if (null != sqlTokenData) {

            TSqlNode sqlCalc = new TSqlNode();
            sqlCalc.setNodeText(sqlTokenData.getTokenStr());
            if (readComments) {
                ParserUtils.addCommentsR(listIterator, sqlCalc);
            } else {
                ParserUtils.readSpaces(listIterator, sqlCalc);
            }
            return sqlCalc;
        }
        return null;
    }

    /**
     * this is similar to handleToken , but it not find the token will throw
     * exception
     * 
     * @param listIterator token items list
     * @param token tokenstr to match
     * @return the SqlNode when matches to the given token string
     */
    public static TSqlNode handleTokenWithException(ListIterator<ISQLTokenData> listIterator, String token) {

        TSqlNode sqlNode = handleToken(listIterator, token, true);

        if (null == sqlNode) {
            throw new IllegalStateException("Unable to position the statement");
        }

        return handleToken(listIterator, token, true);
    }

    /**
     * return the token when it matches with the token string
     * 
     * @param listIterator listIterator token items list
     * @param token tokenstr to match
     * @return the SqlNode when matches to the given token string
     */
    public static TSqlNode handleToken(ListIterator<ISQLTokenData> listIterator, String token) {
        return handleToken(listIterator, token, true);
    }

    /**
     * return the token when it matches with the token string
     * 
     * @param listIterator listIterator token items list
     * @param token tokenstr to match
     * @param readComments to read the comments or not.
     * @return the SqlNode when matches to the given token string
     */
    public static TSqlNode handleToken(ListIterator<ISQLTokenData> listIterator, String token, boolean readComments) {
        ISQLTokenData sqlTokenData = getSQLTokenData(listIterator, token);

        return handleComments(listIterator, readComments, sqlTokenData);
    }

    private static ISQLTokenData getSQLTokenData(ListIterator<ISQLTokenData> listIterator, List<String> tokens) {
        if (listIterator.hasNext()) {
            ISQLTokenData lSQLTokenData = listIterator.next();
            if (null != lSQLTokenData.getTokenStr() && tokens.contains(lSQLTokenData.getTokenStr().toLowerCase())) {
                return lSQLTokenData;
            } else {
                listIterator.previous();
            }
        }
        return null;
    }

    private static ISQLTokenData getSQLTokenData(ListIterator<ISQLTokenData> listIterator, String token) {
        if (listIterator.hasNext()) {
            ISQLTokenData lSQLTokenData = listIterator.next();
            if (token.equalsIgnoreCase(lSQLTokenData.getTokenStr())) {
                return lSQLTokenData;
            } else {
                listIterator.previous();
            }
        }
        return null;
    }

    /**
     * get the sqlnode from the give sqltoken data
     * 
     * @param listIterator listIterator token items list
     * @param next given SQL Token Data
     * @return sqlnode for the given tokenData
     */
    public static TSqlNode getSqlNode(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
        TSqlNode seperator = new TSqlNode();
        seperator.setNodeText(next.getTokenStr());
        ParserUtils.addCommentsR(listIterator, seperator);
        return seperator;
    }

    /**
     * add Custom Statement to Full Stmt
     * 
     * @param expression full stmt expression
     * @param customSqlStmt parseTreeNode to add
     * @param listIterator listIterator token items list
     */
    public static void addCustomStmtToFullStmt(TFullStmt expression, TParseTreeNode customSqlStmt,
            ListIterator<ISQLTokenData> listIterator) {
        if (null == customSqlStmt) {
            throw new GaussDBSQLParserException("Can't add the null stmt to the FullStmt. Can't parse the statement");
        }

        expression.addStmtNode(customSqlStmt);

    }

    /**
     * util to add custom stmt to expression
     * 
     * @param expression normal expression to add
     * @param customSqlStmt parseTreeNode to add
     * @param listIterator listIterator token items list
     */
    public static void addCustomStmtToExpression(TExpression expression, TParseTreeNode customSqlStmt,
            ListIterator<ISQLTokenData> listIterator) {
        TExpressionNode expNode = new TExpressionNode();
        expNode.setCustomStmt(customSqlStmt);
        expression.addExpressionNode(expNode);
        ParserUtils.addCommentsR(listIterator, customSqlStmt);
    }

    /**
     * add expression to Node.
     * 
     * @param expression expression to add.
     * @param listIterator listIterator token items list
     * @param next sql token data
     */
    public static void addExpressionNode(TExpression expression, ListIterator<ISQLTokenData> listIterator,
            ISQLTokenData next) {
        TExpressionNode expNode = new TExpressionNode();
        expNode.getExpNode().setNodeText(next.getTokenStr());
        ParserUtils.addCommentsR(listIterator, expNode.getExpNode());
        expression.addExpressionNode(expNode);
    }

    /**
     * to check the given token str is math operator
     * 
     * @param tokenStr toke string to match
     * @return true to math operator, false if not math operator
     */
    public static boolean isMathOperator(String tokenStr) {
        List<String> asList = Arrays.asList("+", "-", ".", "*", "/", "$", "|");
        return asList.contains(tokenStr);
    }

    /**
     * return Common List for Func and Procedure common words
     * 
     * @return Common List for Func and Procedure common words
     */
    public static List<String> getCommonKeywordListForFunProc() {

        List<String> asList = Arrays.asList("immutable", "stable", "volatile", "shippable", "package", "fenced",
                "leakproof", "called", "returns", "strict", "external", "security", "authid", "cost", "rows", "set",
                "not");

        return asList;
    }

    /**
     * return Common List for Func and Procedure common words
     * 
     * @return Common List for Func and Procedure common words
     */
    public static List<String> getCommonNotKeywordListForFunProc() {

        List<String> asList = Arrays.asList("SHIPPABLE", "FENCED", "LEAKPROOF");

        return asList;
    }

    /**
     * checks if it is commons keyword or not
     * 
     * @param str keyword to check
     * @return true if common keyword, false if not common
     */
    public static boolean isCommonKeyword(String str) {
        return str != null && getCommonKeywordListForFunProc().contains(str);
    }

}
