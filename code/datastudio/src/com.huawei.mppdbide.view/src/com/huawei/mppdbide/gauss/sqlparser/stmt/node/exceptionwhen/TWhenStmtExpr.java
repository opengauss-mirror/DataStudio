/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.exceptionwhen;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * Title: TWhenStmtExpr Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 18-Dec-2019]
 * @since 18-Dec-2019
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
