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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.delete;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TDeleteFromASTNode
 *
 * @since 3.0.0
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
