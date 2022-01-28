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

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.exceptionwhen;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * Title: TWhenStmtExpr
 *
 * @since 3.0.0
 */
public class TWhenStmtExpr extends TAbstractListItem {

    private TSqlNode when = null;

    private TExpression exceptionType = null;

    private TSqlNode sqlNodeThen = null;

    private TFullStmt tFullStmt = null;

    public TSqlNode getWhen() {
        return when;
    }

    public void setWhen(TSqlNode when) {
        this.when = when;
        setPreviousObject(this.when);
    }

    public TExpression getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(TExpression exceptionType) {
        this.exceptionType = exceptionType;
        setPreviousObject(this.exceptionType);
    }

    /**
     * get the Then node
     * 
     * @return sqlNodeThen the sqlNodeThen
     */
    public TSqlNode getThen() {
        return sqlNodeThen;
    }

    /**
     * sets the Then node
     * 
     * @param then the then node
     */
    public void setThen(TSqlNode then) {
        this.sqlNodeThen = then;
        setPreviousObject(this.sqlNodeThen);
    }

    /**
     * gets the FullStmt
     * 
     * @return tFullStmt the tFullStmt
     */
    public TFullStmt getFullStmt() {
        return tFullStmt;
    }

    /**
     * sets the FullStmt
     * 
     * @param fullStmt the fullStmt
     */
    public void setFullStmt(TFullStmt fullStmt) {
        this.tFullStmt = fullStmt;
        setPreviousObject(this.tFullStmt);
    }

    @Override
    public TParseTreeNode getStartNode() {
        return when;
    }

}
