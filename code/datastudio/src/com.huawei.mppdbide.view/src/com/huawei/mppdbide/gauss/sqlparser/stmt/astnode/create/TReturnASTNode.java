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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TReturnASTNode
 *
 * @since 3.0.0
 */
public class TReturnASTNode extends TBasicASTNode {

    private TSqlNode deterministic = null;
    private TSqlNode table = null;
    private TExpression resultExpression = null;

    private TParseTreeNodeList<?> returnTableList = null;

    private TExpression commonExpression = null;

    private TSqlNode returnStartBracket = null;

    private TSqlNode returnEndBracket = null;

    public TExpression getCommonExpression() {
        return commonExpression;
    }

    public void setCommonExpression(TExpression commonExpression) {
        this.commonExpression = commonExpression;
        setPreviousObject(this.commonExpression);
    }

    public TExpression getResultExpression() {
        return resultExpression;
    }

    public void setResultExpression(TExpression resultExpression) {
        this.resultExpression = resultExpression;
        setPreviousObject(this.resultExpression);
    }

    public TSqlNode getTable() {
        return table;
    }

    public void setTable(TSqlNode table) {
        this.table = table;
        setPreviousObject(this.table);
    }

    public TSqlNode getDeterministic() {
        return deterministic;
    }

    public void setDeterministic(TSqlNode deterministic) {
        this.deterministic = deterministic;
        setPreviousObject(this.deterministic);
    }

    public TParseTreeNodeList<?> getReturnTableList() {
        return returnTableList;
    }

    public void setReturnTableList(TParseTreeNodeList<?> returnTableList) {
        this.returnTableList = returnTableList;
        setPreviousObject(this.returnTableList);
    }

    public TSqlNode getReturnStartBracket() {
        return returnStartBracket;
    }

    public void setReturnStartBracket(TSqlNode returnStartBracket) {
        this.returnStartBracket = returnStartBracket;
        setPreviousObject(this.returnStartBracket);
    }

    public TSqlNode getReturnEndBracket() {
        return returnEndBracket;
    }

    public void setReturnEndBracket(TSqlNode returnEndBracket) {
        this.returnEndBracket = returnEndBracket;
        setPreviousObject(this.returnEndBracket);
    }

}
