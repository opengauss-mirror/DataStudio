/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TInsertIntoASTNode
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
public class TInsertIntoASTNode extends TBasicASTNode {

    private TSqlNode into = null;

    private TExpression tableName = null;

    private TSqlNode startInsertAstBracket = null;

    private TSqlNode endInsertAstBracket = null;

    public TSqlNode getInto() {
        return into;
    }

    public void setInto(TSqlNode into) {
        this.into = into;
        setPreviousObject(this.into);
    }

    public TExpression getTableName() {
        return tableName;
    }

    public void setTableName(TExpression tableName) {
        this.tableName = tableName;
        setPreviousObject(this.tableName);
    }

    public TSqlNode getStartInsertAstBracket() {
        return startInsertAstBracket;
    }

    public void setStartInsertAstBracket(TSqlNode startBracket) {
        this.startInsertAstBracket = startBracket;
        setPreviousObject(this.startInsertAstBracket);
    }

    public TSqlNode getEndInsertAstBracket() {
        return endInsertAstBracket;
    }

    public void setEndInsertAstBracket(TSqlNode endBracket) {
        this.endInsertAstBracket = endBracket;
        setPreviousObject(this.endInsertAstBracket);
    }

}
