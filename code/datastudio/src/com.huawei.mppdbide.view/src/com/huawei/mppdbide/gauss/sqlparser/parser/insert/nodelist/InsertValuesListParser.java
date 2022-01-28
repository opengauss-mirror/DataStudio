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

package com.huawei.mppdbide.gauss.sqlparser.parser.insert.nodelist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertValuesNodeItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertValuesNodeItemList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: InsertValuesListParser
 *
 * @since 3.0.0
 */
public class InsertValuesListParser extends AbstractNodeListParser {

    /**
     *  The result column. 
     */
    protected TInsertValuesNodeItem resultColumn = null;

    private TInsertValuesNodeItemList resultColumnList = new TInsertValuesNodeItemList();

    /**
     * Instantiates a new insert values list parser.
     *
     * @param lineBreakSet the line break set
     */
    public InsertValuesListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);
    }

    /**
     * Creates the T object.
     */
    public void createTObject() {
        // create and then it is the start of the expression
        if (null == resultColumn) {
            resultColumn = getResultColumn();
            resultColumnList.addResultColumn(resultColumn);
        }
    }

    /**
     * Gets the result column.
     *
     * @return the result column
     */
    protected TInsertValuesNodeItem getResultColumn() {
        return new TInsertValuesNodeItem();
    }

    /**
     * Handle node alias.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    public void handleNodeAlias(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {

    }

    /**
     * Handle node end.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    public void handleNodeEnd(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next) {

        TSqlNode seperator = ParserUtils.getSqlNode(listIterator, next);
        resultColumn.setSeperator(seperator);
        resultColumn = null;

    }

    /**
     * Gets the item list.
     *
     * @return the item list
     */
    @Override
    public TParseTreeNodeList<?> getItemList() {
        return resultColumnList;
    }

    /**
     * Checks if is list break.
     *
     * @param nodeStr the node str
     * @param parseCount the parse count
     * @return true, if is list break
     */
    @Override
    public boolean isListBreak(String nodeStr, int parseCount) {
        return parseCount == 0 && listBreak.contains(nodeStr.toLowerCase());
    }

    /**
     * Creates the T node.
     *
     * @param listIterator the list iterator
     * @param next the next
     * @param paramCount the param count
     */
    public void createTNode(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next, int paramCount) {

        if (null != resultColumn.getStartInsertBracket() || null != resultColumn.getValueItemList()) {
            if (SQLFoldingConstants.SQL_BRACKET_END.equals(next.getTokenStr())) {
                TSqlNode endBracket = ParserUtils.getSqlNode(listIterator, next);
                resultColumn.setInsertEndBracket(endBracket);
            } else {
                throw new IllegalStateException("Unable to position the statement");
            }
            return;
        }

        if (SQLFoldingConstants.SQL_BRACKET_START.equals(next.getTokenStr())) {
            TSqlNode startBracket = ParserUtils.getSqlNode(listIterator, next);
            resultColumn.setStartInsertBracket(startBracket);
        }

        // parse for the result list

        Set<String> asList = new HashSet<>(Arrays.asList(SQLFoldingConstants.SQL_BRACKET_END));

        SelectResultListParser lSelectResultListParser = new SelectResultListParser(asList) {
            @Override
            public boolean isListBreak(String nodeStr, int parseCount) {
                return parseCount == 0 && listBreak.contains(nodeStr.toLowerCase());
            }

            @Override
            public void handleStartEndNode(String previousNotEmptyToken, ISQLTokenData next,
                    ListIterator<ISQLTokenData> listIterator) {
                // do nothing
            }
        };

        NodeListParserConverter.handleSelectList(listIterator, lSelectResultListParser);

        TParseTreeNodeList<?> handleSelectList = lSelectResultListParser.getItemList();
        if (null != handleSelectList) {
            resultColumn.setValueItemList(handleSelectList);
        }

    }

    /**
     * Checks if is node end.
     *
     * @param nodeStr the node str
     * @return true, if is node end
     */
    public boolean isNodeEnd(String nodeStr) {
        return ",".equalsIgnoreCase(nodeStr);
    }

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @param previousNotEmptyToken the previous not empty token
     * @return true, if is alias name
     */
    public boolean isAliasName(String nodeStr, String previousNotEmptyToken) {
        return false;
    }

    /**
     * Creates the T custom stmt node.
     *
     * @param customSqlStmt the custom sql stmt
     * @param listIterator the list iterator
     * @param next the next
     */
    @Override
    public void createTCustomStmtNode(TParseTreeNode customSqlStmt, ListIterator<ISQLTokenData> listIterator,
            ISQLTokenData next) {

    }

    /**
     * Handle start end node.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param next the next
     * @param listIterator the list iterator
     */
    public void handleStartEndNode(String previousNotEmptyToken, ISQLTokenData next,
            ListIterator<ISQLTokenData> listIterator) {
    }

}
