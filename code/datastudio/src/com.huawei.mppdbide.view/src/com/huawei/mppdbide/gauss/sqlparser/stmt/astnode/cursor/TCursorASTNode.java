/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.cursor;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: TLoopASTNode Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class TCursorASTNode extends TBasicASTNode {

    private TExpression cursorExpression = null;

    private TSqlNode paramStartBracket = null;

    private TSqlNode paramEndBracket = null;

    private TSqlNode forOrIs = null;

    private TExpression cursorStmts = null;

    public TExpression getCursorExpression() {
        return cursorExpression;
    }

    public void setCursorExpression(TExpression sursorExpression) {
        this.cursorExpression = sursorExpression;
        setPreviousObject(this.cursorExpression);
    }

    public TSqlNode getParamStartBracket() {
        return paramStartBracket;
    }

    public void setParamStartBracket(TSqlNode paramStartBracket) {
        this.paramStartBracket = paramStartBracket;
        setPreviousObject(this.paramStartBracket);
    }

    public TSqlNode getParamEndBracket() {
        return paramEndBracket;
    }

    public void setParamEndBracket(TSqlNode paramEndBracket) {
        this.paramEndBracket = paramEndBracket;
        setPreviousObject(this.paramEndBracket);
    }

    public TSqlNode getForOrIs() {
        return forOrIs;
    }

    public void setForOrIs(TSqlNode forOrIs) {
        this.forOrIs = forOrIs;
        setPreviousObject(this.forOrIs);
    }

    public TExpression getCursorStmts() {
        return cursorStmts;
    }

    public void setCursorStmts(TExpression cursorStmts) {
        this.cursorStmts = cursorStmts;
        setPreviousObject(this.cursorStmts);
    }

}
