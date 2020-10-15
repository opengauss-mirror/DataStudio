/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.nodelist;

import java.util.ListIterator;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserFactory;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;

/**
 * 
 * Title: NodeListParserConverter
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class NodeListParserConverter {

    /**
     * Handle select list.
     *
     * @param listIterator the list iterator
     * @param listParser the list parser
     */
    public static void handleSelectList(ListIterator<ISQLTokenData> listIterator, NodeListParser listParser) {
        // select list contains elements which is separated by , and ends when
        // the from clause occurs
        int paramCount = 0;
        String previousNotEmptyToken = null;
        while (listIterator.hasNext()) {
            ISQLTokenData next = listIterator.next();
            String tokenStr = next.getTokenStr();
            if (null != next.getSubTokenBean()) {
                if (listParser.isListBreakWithCustomSQL(previousNotEmptyToken, next.getSubTokenBean())) {
                    listIterator.previous();
                    break;
                }
                // sub query/stmt handle
                TCustomSqlStatement customSqlStmt = handleSubQuery(listIterator, listParser, next);
                continue;
            }
            // if empty char then continue;
            if (ParserUtils.isTokenEmpty(next)) {
                continue;
            }
            if (null == tokenStr) {
                continue;
            }
            if (listParser.isListBreak(tokenStr, paramCount)) {
                listIterator.previous();
                break;
            }
            paramCount = handleParameterCount(listParser, paramCount, tokenStr);
            if (paramCount == 0) {
                if (listParser.isNodeEnd(tokenStr)) {
                    previousNotEmptyToken = handleNodeEnd(listIterator, listParser, next, tokenStr);
                    continue;
                }
                if (listParser.isAliasName(tokenStr, previousNotEmptyToken)) {
                    previousNotEmptyToken = handleNodeAlias(listIterator, listParser, next, tokenStr);
                    continue;
                }
                listParser.handleStartEndNode(previousNotEmptyToken, next, listIterator);
            }
            previousNotEmptyToken = createNode(listIterator, listParser, paramCount, next, tokenStr);
        }
    }

    private static String handleNodeAlias(ListIterator<ISQLTokenData> listIterator, NodeListParser listParser,
            ISQLTokenData next, String tokenStr) {
        String previousNotEmptyToken;
        listParser.handleNodeAlias(listIterator, next);
        previousNotEmptyToken = tokenStr;
        return previousNotEmptyToken;
    }

    private static String handleNodeEnd(ListIterator<ISQLTokenData> listIterator, NodeListParser listParser,
            ISQLTokenData next, String tokenStr) {
        String previousNotEmptyToken;
        listParser.handleNodeEnd(listIterator, next);
        previousNotEmptyToken = tokenStr;
        return previousNotEmptyToken;
    }

    private static String createNode(ListIterator<ISQLTokenData> listIterator, NodeListParser listParser,
            int paramCount, ISQLTokenData next, String tokenStr) {
        String previousNotEmptyToken;
        listParser.createTNode(listIterator, next, paramCount);
        previousNotEmptyToken = tokenStr;
        return previousNotEmptyToken;
    }

    private static int handleParameterCount(NodeListParser listParser, int paramCountParam, String tokenStr) {
        int paramCount = paramCountParam;
        listParser.createTObject();

        if ("(".equalsIgnoreCase(tokenStr)) {
            paramCount++;
        }
        if (")".equalsIgnoreCase(tokenStr)) {
            paramCount--;
        }
        return paramCount;
    }

    private static TCustomSqlStatement handleSubQuery(ListIterator<ISQLTokenData> listIterator,
            NodeListParser listParser, ISQLTokenData next) {
        TCustomSqlStatement customSqlStmt = ParserFactory.getCustomSqlStmt(next.getSubTokenBean());
        if (null == customSqlStmt) {
            throw new GaussDBSQLParserException("Unable to position the statement");
        }
        listParser.createTObject();
        listParser.createTCustomStmtNode(customSqlStmt, listIterator, next);
        listParser.setExpContainStmt();
        return customSqlStmt;
    }

    /**
     * Handle declare list.
     *
     * @param listIterator the list iterator
     * @param listParser the list parser
     */
    public static void handleDeclareList(ListIterator<ISQLTokenData> listIterator, NodeListParser listParser) {
        while (listIterator.hasNext()) {
            ISQLTokenData next = listIterator.next();
            String tokenStr = next.getTokenStr();

            // if empty char then continue;
            if (ParserUtils.isTokenEmpty(next)) {
                continue;
            }

            if (listParser.isListBreak(tokenStr, 0)) {
                listIterator.previous();
                break;
            }

            listParser.createTObject();

            if (listParser.isNodeEnd(tokenStr)) {
                listParser.handleNodeEnd(listIterator, next);
                continue;
            }

            listParser.createTNode(listIterator, next, 0);
        }
    }

}