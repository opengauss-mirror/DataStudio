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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node.fullstmt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import org.opengauss.mppdbide.gauss.sqlparser.parser.utils.FullNodeExpressionType;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmtbeanif.StatementBeanIf;

/**
 * Title: TFullListNode
 *
 * @since 3.0.0
 */
public class TFullListNode extends TParseTreeNode {

    private FullNodeExpressionType expressionType = FullNodeExpressionType.UNKNOWN;

    private List<StatementBeanIf> sqlTokenDataList = new ArrayList<StatementBeanIf>(10);

    private TParseTreeNode nullNode = null;

    private String lastNonEmptyToken = null;

    private int nonEmptyCharcount = 0;

    public List<StatementBeanIf> getSqlTokenDataList() {
        return sqlTokenDataList;
    }

    public void setSqlTokenDataList(List<StatementBeanIf> sqlTokenDataList) {
        this.sqlTokenDataList = sqlTokenDataList;
    }

    /**
     * add sqltokendata to list
     * 
     * @param sqlTokenData which is added to sql token list
     */
    public void addSqlTokenDataList(StatementBeanIf sqlTokenData) {
        if (expressionType != FullNodeExpressionType.ASSIGNMENTS) {
            expressionType = FullNodeExpressionType.NORMAL;
        }
        this.sqlTokenDataList.add(sqlTokenData);
    }

    /**
     * add sql tokendata with type
     * 
     * @param sqlTokenData which is added to sql token list
     */
    public void addSQLTokenData(ISQLTokenData sqlTokenData) {
        if (FullNodeExpressionType.UNKNOWN == expressionType) {
            if (sqlTokenData.getToken().getData() == ISQLSyntax.SQL_MULTILINE_COMMENT) {
                expressionType = FullNodeExpressionType.COMMENTS;
            } else if (StringUtils.isBlank(sqlTokenData.getTokenStr())) {
                expressionType = FullNodeExpressionType.NEWLINES;
            } else {
                expressionType = FullNodeExpressionType.NORMAL;
            }
        }

        assignAssignmentStmt(sqlTokenData);

        this.sqlTokenDataList.add(sqlTokenData);
    }

    private void assignAssignmentStmt(ISQLTokenData sqlTokenData) {
        if (StringUtils.isNotBlank(sqlTokenData.getTokenStr()) && nonEmptyCharcount < 8) {
            nonEmptyCharcount++;
            if (expressionType != FullNodeExpressionType.ASSIGNMENTS) {

                if ("=".equals(sqlTokenData.getTokenStr()) && ":".equals(lastNonEmptyToken)) {
                    expressionType = FullNodeExpressionType.ASSIGNMENTS;
                }
                lastNonEmptyToken = sqlTokenData.getTokenStr();
            }
        }
    }

    public FullNodeExpressionType getExpressionType() {
        return expressionType;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return nullNode;
    }

}
