/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: TMergeASTNode Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author s72444
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
