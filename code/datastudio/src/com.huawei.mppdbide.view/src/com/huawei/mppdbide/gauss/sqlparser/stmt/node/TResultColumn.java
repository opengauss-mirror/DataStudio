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

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TResultColumn
 *
 * @since 3.0.0
 */
public class TResultColumn extends TListItem {

    private TExpression expression = null;

    private TSqlNode as = null;

    private TExpression endNode = null;

    private TSqlNode seperator = null;

    public TExpression getExpression() {
        return expression;
    }

    public void setExpression(TExpression expression) {
        this.expression = expression;
        setPreviousObject(this.expression);

    }

    public TSqlNode getAs() {
        return as;
    }

    public void setSeperator(TSqlNode seperator) {
        this.seperator = seperator;
        setPreviousObject(this.seperator);
    }

    public void setAs(TSqlNode as) {
        this.as = as;
        setPreviousObject(this.as);
    }

    public TExpression getEndNode() {
        return endNode;
    }

    public void setEndNode(TExpression aliasName) {
        this.endNode = aliasName;
        setPreviousObject(this.endNode);
    }

    @Override
    public TParseTreeNode getItemListNode() {
        return expression;
    }

    @Override
    public TSqlNode getSeperator() {
        return seperator;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return expression;
    }

}
