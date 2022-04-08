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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TCustomSqlWithBracket
 *
 * @since 3.0.0
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
