/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.begin.nodelist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.FullStatementConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.exceptionwhen.TWhenExprList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.exceptionwhen.TWhenStmtExpr;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * Title: CaseItemListParser Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class WhenItemListParser extends AbstractNodeListParser {
    private TWhenStmtExpr listItem = null;

    private TWhenExprList resultColumnList = new TWhenExprList();

    public WhenItemListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);
    }

    /**
     * handle the object creation for withitem
     */
    public void createTObject() {
        // create and then it is the start of the expression
    }

    private void createLocalObject() {
        // create and then it is the start of the expression
        if (null == listItem) {
            listItem = new TWhenStmtExpr();

            resultColumnList.addResultColumn(listItem);
        }
    }

    /**
     * method to call when the nodeAlias condition met
     */
    public void handleNodeAlias(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
        // close the TExpressionNode
        TSqlNode thenNode = new TSqlNode();
        thenNode.setNodeText(next.getTokenStr());
        ParserUtils.addCommentsR(listIterator, thenNode);
        listItem.setThen(thenNode);

        prepareEndNode(listIterator);
    }

    private void prepareEndNode(ListIterator<ISQLTokenData> listIterator) {
        Set<String> lineBreakSet = new HashSet<String>(
                Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END, SQLFoldingConstants.SQL_KEYWORD_WHEN));

        TFullStmt fullStmt = FullStatementConverter.parseAndGetFullStmt(listIterator, lineBreakSet);

        listItem.setFullStmt(fullStmt);
    }

    /**
     * method to call when the node end condition met
     */
    public void handleNodeEnd(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {
        listItem = null;

        createLocalObject();

        TSqlNode when = new TSqlNode();
        when.setNodeText(next.getTokenStr());
        ParserUtils.addCommentsR(listIterator, when);
        listItem.setWhen(when);

        Set<String> endTableName = new HashSet<String>(Arrays.asList("then"));

        listItem.setExceptionType(NodeExpressionConverter.parseAndGetExpression(listIterator, endTableName));
    }

    /**
     * result column list
     */
    @Override
    public TParseTreeNodeList<?> getItemList() {
        return resultColumnList;
    }

    /**
     * handle for running object node data
     */
    public void createTNode(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next, int paramCount) {
    }

    /**
     * returns true when the node end
     */
    public boolean isNodeEnd(String nodeStr) {
        return "WHEN".equalsIgnoreCase(nodeStr);
    }

    /**
     * returns true when it is alias node
     */
    public boolean isAliasName(String nodeStr, String previousNotEmptyToken) {
        return "THEN".equalsIgnoreCase(nodeStr);
    }

    /**
     * handle Custom Stmt Node Object
     */
    @Override
    public void createTCustomStmtNode(TParseTreeNode customSqlStmt, ListIterator<ISQLTokenData> listIterator,
            ISQLTokenData next) {
    }

    /**
     * handle the node end case
     */
    public void handleStartEndNode(String previousNotEmptyToken, ISQLTokenData next,
            ListIterator<ISQLTokenData> listIterator) {
    }

}
