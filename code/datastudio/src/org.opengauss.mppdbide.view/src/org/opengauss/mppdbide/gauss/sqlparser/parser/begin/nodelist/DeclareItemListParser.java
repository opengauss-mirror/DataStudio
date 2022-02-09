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

package org.opengauss.mppdbide.gauss.sqlparser.parser.begin.nodelist;

import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import org.opengauss.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;

/**
 * 
 * Title: DeclareItemListParser
 *
 * @since 3.0.0
 */
public class DeclareItemListParser extends SelectResultListParser {

    /**
     * Instantiates a new declare item list parser.
     *
     * @param lineBreakSet the line break set
     */
    public DeclareItemListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);

    }

    /**
     * Checks if is node end.
     *
     * @param nodeStr the node str
     * @return true, if is node end
     */
    @Override
    public boolean isNodeEnd(String nodeStr) {
        return ";".equalsIgnoreCase(nodeStr);
    }

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @return true, if is alias name
     */
    public boolean isAliasName(String nodeStr) {
        return !isInEndNode() && ":=".equalsIgnoreCase(nodeStr);
    }

    /**
     * Sets the exp contain stmt.
     */
    public void setExpContainStmt() {
        super.setExpContainStmt();
        resultColumn = null;
        expression = null;
        endNodeExpression = null;
        inEndNode = false;
        // stop the

    }

    /**
     * Checks if is list break with custom SQL.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param sqlStmtTokenListBean the sql stmt token list bean
     * @return true, if is list break with custom SQL
     */
    @Override
    public boolean isListBreakWithCustomSQL(String previousNotEmptyToken, SQLStmtTokenListBean sqlStmtTokenListBean) {

        int statementType = sqlStmtTokenListBean.getStatementType();

        if (statementType == SQLTokenConstants.T_SQL_BLOCK_DECLARE
                || statementType == SQLTokenConstants.T_SQL_BLOCK_BEGIN
                || statementType == SQLTokenConstants.T_SQL_DDL_CREATE_FUNC
                || statementType == SQLTokenConstants.T_SQL_DDL_CREATE_PROC) {
            return true;
        }

        return false;
    }

}
