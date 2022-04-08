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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node.casenode;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TWhereClause;

/**
 * 
 * Title: TCaseStmtExpr
 *
 * @since 3.0.0
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
