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

package com.huawei.mppdbide.gauss.sqlparser.parser.nodelist;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserFactory;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.utils.FullNodeExpressionType;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullListNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * Title: FullStatementConverter
 *
 * @since 3.0.0
 */
public class FullStatementConverter {

    /**
     * Parses the and get full stmt.
     *
     * @param listIterator the list iterator
     * @param keywordList the keyword list
     * @return the t full stmt
     */
    public static TFullStmt parseAndGetFullStmt(ListIterator<ISQLTokenData> listIterator, Set<String> keywordList) {
        TFullStmt expression = new TFullStmt();

        TFullListNode fulllistNode = null;

        while (listIterator.hasNext()) {
            ISQLTokenData next = listIterator.next();
            String tokenStr = next.getTokenStr();
            if (isStopFullNode(fulllistNode, next)) {
                fulllistNode = null;
            }

            if (null != next.getSubTokenBean()) {
                // sub query/stmt handle
                TCustomSqlStatement customSqlStmt = ParserFactory.getCustomSqlStmt(next.getSubTokenBean());
                if (null == customSqlStmt) {
                    fulllistNode = new TFullListNode();
                    TFullStmt parseAndGetFullStmt = FullStatementConverter.parseAndGetFullStmt(
                            next.getSubTokenBean().getSqlTokenData().listIterator(), new HashSet<String>());
                    fulllistNode.addSqlTokenDataList(parseAndGetFullStmt);
                    expression.addStmtNode(fulllistNode);
                    fulllistNode = null;
                } else if (null != fulllistNode) {
                    fulllistNode.addSqlTokenDataList(customSqlStmt);
                } else {
                    ParserUtils.addCustomStmtToFullStmt(expression, customSqlStmt, listIterator);
                }
                continue;
            }

            if (SQLFoldingConstants.SQL_DELIM_SEMICOLON.equals(tokenStr) && null != fulllistNode) {
                fulllistNode.addSqlTokenDataList(next);
                fulllistNode = null;
                continue;
            }

            // if empty char then continue;

            if (keywordList.contains(tokenStr.toLowerCase())) {
                listIterator.previous();
                break;
            }

            if (null == fulllistNode) {

                fulllistNode = new TFullListNode();
                expression.addStmtNode(fulllistNode);
                if (next.getToken().getData() == ISQLSyntax.SQL_COMMENT) {
                    fulllistNode.addSqlTokenDataList(next);
                    fulllistNode = null;
                    continue;
                }
            }

            fulllistNode.addSQLTokenData(next);
        }

        return expression;
    }

    private static boolean isStopFullNode(TFullListNode fulllistNode, ISQLTokenData next) {
        boolean retVal = false;
        if (null != fulllistNode && fulllistNode.getExpressionType() == FullNodeExpressionType.COMMENTS) {
            if (null != next.getSubTokenBean()) {
                retVal = true;
            } else if (next.getToken().getData() != ISQLSyntax.SQL_MULTILINE_COMMENT
                    && StringUtils.isNotBlank(next.getTokenStr())) {
                retVal = true;
            } else {
                retVal = false;
            }
        } else if (null != fulllistNode && fulllistNode.getExpressionType() == FullNodeExpressionType.NEWLINES) {
            if (null != next.getSubTokenBean()) {
                retVal = true;
            } else if (StringUtils.isNotBlank(next.getTokenStr())) {
                retVal = true;
            } else {
                retVal = false;
            }
        } else {
            retVal = false;
        }
        return retVal;
    }
}
