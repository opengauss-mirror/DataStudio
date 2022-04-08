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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.cursor;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: TLoopASTNode
 *
 * @since 3.0.0
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
