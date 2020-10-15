/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TCustomSqlWithBracket
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
public class TCustomSqlWithBracket extends TListItem {
    private TSqlNode openCustomSqlBracket = null;
    private TExpression sql = null;
    private TSqlNode closeCustomSqlBracket = null;

    public TSqlNode getOpenCustomSqlBracket() {
        return openCustomSqlBracket;
    }

    public void setOpenCustomSqlBracket(TSqlNode openBracket) {
        this.openCustomSqlBracket = openBracket;
    }

    public TExpression getSql() {
        return sql;
    }

    public void setSql(TExpression sql) {
        this.sql = sql;
    }

    public TSqlNode getCloseCustomSqlBracket() {
        return closeCustomSqlBracket;
    }

    public void setCloseCustomSqlBracket(TSqlNode closeBracket) {
        this.closeCustomSqlBracket = closeBracket;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return openCustomSqlBracket;
    }

    @Override
    public TParseTreeNode getItemListNode() {
        return null;
    }

    @Override
    public TSqlNode getAs() {
        return null;
    }

    @Override
    public TSqlNode getEndNode() {
        return null;
    }

    @Override
    public TSqlNode getSeperator() {
        return null;
    }
}
