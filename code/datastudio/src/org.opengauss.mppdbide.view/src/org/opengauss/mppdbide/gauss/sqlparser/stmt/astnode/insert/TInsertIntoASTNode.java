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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.insert;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TInsertIntoASTNode
 *
 * @since 3.0.0
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
