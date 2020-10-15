/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
