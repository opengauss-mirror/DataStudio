/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.casenode;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TWhereClause;

/**
 * 
 * Title: TCaseStmtExpr
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
public class TCaseStmtExpr extends TAbstractListItem {

    private TSqlNode whenOrElse = null;

    private TWhereClause conditionExpr = null;

    private TSqlNode then = null;

    private TExpression endNode = null;

    public TSqlNode getWhenOrElse() {
        return whenOrElse;
    }

    public void setWhenOrElse(TSqlNode whenOrElse) {
        this.whenOrElse = whenOrElse;
        setPreviousObject(this.whenOrElse);
    }

    public TWhereClause getConditionExpr() {
        return conditionExpr;
    }

    public void setConditionExpr(TWhereClause conditionExpr) {
        this.conditionExpr = conditionExpr;
        setPreviousObject(this.conditionExpr);
    }

    public TSqlNode getThen() {
        return then;
    }

    public void setThen(TSqlNode then) {
        this.then = then;
        setPreviousObject(this.then);
    }

    public TExpression getEndNode() {
        return endNode;
    }

    public void setEndNode(TExpression endNode) {
        this.endNode = endNode;
        setPreviousObject(this.endNode);
    }

    @Override
    public TParseTreeNode getStartNode() {
        return whenOrElse;
    }

}
