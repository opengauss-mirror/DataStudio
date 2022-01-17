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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.begin;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * Title: TBeginASTNode
 * 
 * @since 3.0.0
 */
public class TBeginASTNode extends TBasicASTNode {

    /** 
     * The full stmt. 
     */
    private TFullStmt tFullStmt = null;

    /** 
     * The exception. 
     */
    private TSqlNode exception = null;

    /** 
     * The end. 
     */
    private TSqlNode sqlNodeEnd = null;

    /** 
     * The end expression.
     */
    private TExpression endExpression = null;

    /** 
     * The exception when list. 
     */
    private TParseTreeNodeList<?> exceptionWhenList = null;

    /**
     * Gets the full stmt.
     *
     * @return the full stmt
     */
    public TFullStmt getFullStmt() {
        return tFullStmt;
    }

    /**
     * Sets the full stmt.
     *
     * @param fullStmt the new full stmt
     */
    public void setFullStmt(TFullStmt fullStmt) {
        this.tFullStmt = fullStmt;
        setPreviousObject(this.tFullStmt);
    }

    /**
     * Gets the end.
     *
     * @return the end
     */
    public TSqlNode getEnd() {
        return sqlNodeEnd;
    }

    /**
     * Sets the end.
     *
     * @param end the new end
     */
    public void setEnd(TSqlNode end) {
        this.sqlNodeEnd = end;
        setPreviousObject(this.sqlNodeEnd);
    }

    /**
     * Gets the end expression.
     *
     * @return the end expression
     */
    public TExpression getEndExpression() {
        return endExpression;
    }

    /**
     * Sets the end expression.
     *
     * @param endExpression the new end expression
     */
    public void setEndExpression(TExpression endExpression) {
        this.endExpression = endExpression;
        setPreviousObject(this.endExpression);
    }

    /**
     * Gets the exception.
     *
     * @return the exception
     */
    public TSqlNode getException() {
        return exception;
    }

    /**
     * Sets the exception.
     *
     * @param exception the new exception
     */
    public void setException(TSqlNode exception) {
        this.exception = exception;
        setPreviousObject(this.exception);
    }

    /**
     * Gets the exception when list.
     *
     * @return the exception when list
     */
    public TParseTreeNodeList<?> getExceptionWhenList() {
        return exceptionWhenList;
    }

    /**
     * Sets the exception when list.
     *
     * @param tParseTreeNodeList the new exception when list
     */
    public void setExceptionWhenList(TParseTreeNodeList<?> tParseTreeNodeList) {
        this.exceptionWhenList = tParseTreeNodeList;
        setPreviousObject(this.exceptionWhenList);
    }

}
