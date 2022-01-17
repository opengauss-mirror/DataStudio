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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: TMergeASTNode
 *
 * @since 3.0.0
 */
public class TMergeASTNode extends TBasicASTNode {

    private TSqlNode hintInfo = null;

    private TSqlNode into = null;

    private TExpression srcTable = null;

    private TSqlNode using = null;

    private TExpression destTable = null;

    private TSqlNode on = null;

    private TSqlNode onStartBracket = null;

    private TSqlNode onEndBracket = null;

    public TSqlNode getHintInfo() {
        return hintInfo;
    }

    public void setHintInfo(TSqlNode hintInfo) {
        this.hintInfo = hintInfo;
        setPreviousObject(this.hintInfo);
    }

    public TSqlNode getInto() {
        return into;
    }

    public void setInto(TSqlNode into) {
        this.into = into;
        setPreviousObject(this.into);
    }

    public TExpression getSrcTable() {
        return srcTable;
    }

    public void setSrcTable(TExpression srcTable) {
        this.srcTable = srcTable;
        setPreviousObject(this.srcTable);
    }

    public TSqlNode getUsing() {
        return using;
    }

    public void setUsing(TSqlNode using) {
        this.using = using;
        setPreviousObject(this.using);
    }

    public TExpression getDestTable() {
        return destTable;
    }

    public void setDestTable(TExpression destTable) {
        this.destTable = destTable;
        setPreviousObject(this.destTable);
    }

    public TSqlNode getOn() {
        return on;
    }

    public void setOn(TSqlNode on) {
        this.on = on;
        setPreviousObject(this.on);
    }

    public TSqlNode getOnStartBracket() {
        return onStartBracket;
    }

    public void setOnStartBracket(TSqlNode onStartBracket) {
        this.onStartBracket = onStartBracket;
        setPreviousObject(this.onStartBracket);
    }

    public TSqlNode getOnEndBracket() {
        return onEndBracket;
    }

    public void setOnEndBracket(TSqlNode onEndBracket) {
        this.onEndBracket = onEndBracket;
        setPreviousObject(this.onEndBracket);
    }

}
