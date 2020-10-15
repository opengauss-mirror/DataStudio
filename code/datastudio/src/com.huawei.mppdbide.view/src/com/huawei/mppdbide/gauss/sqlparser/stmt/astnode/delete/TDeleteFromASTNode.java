/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.delete;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TDeleteFromASTNode
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
public class TDeleteFromASTNode extends TBasicASTNode {

    private TSqlNode from = null;

    private TSqlNode only = null;

    private TExpression tableName = null;

    public TSqlNode getFrom() {
        return from;
    }

    public void setFrom(TSqlNode from) {
        this.from = from;
        setPreviousObject(this.from);
    }

    public TSqlNode getOnly() {
        return only;
    }

    public void setOnly(TSqlNode only) {
        this.only = only;
        setPreviousObject(this.only);
    }

    public TExpression getTableName() {
        return tableName;
    }

    public void setTableName(TExpression tableName) {
        this.tableName = tableName;
        setPreviousObject(this.tableName);
    }

}
