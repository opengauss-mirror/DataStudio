/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.begin.nodelist;

import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;

/**
 * 
 * Title: DeclareItemListParser
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
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
