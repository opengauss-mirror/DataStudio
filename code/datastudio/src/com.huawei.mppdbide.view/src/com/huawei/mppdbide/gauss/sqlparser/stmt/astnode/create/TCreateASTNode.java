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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TCreateASTNode
 *
 * @since 3.0.0
 */
public class TCreateASTNode extends TBasicASTNode {

    /**
     * the text between create and Procedure and Function is maintained in the
     * below expression
     */
    private TExpression intermediateText = null;

    private TExpression ifNotExists = null;

    private TSqlNode procOrFunction = null;

    private TExpression procOrFuncName = null;

    private TSqlNode procStartBracket = null;

    private TSqlNode procEndBracket = null;

    private TExpression commonExpression = null;

    public TExpression getIntermediateText() {
        return intermediateText;
    }

    public void setIntermediateText(TExpression intermediateText) {
        this.intermediateText = intermediateText;
        setPreviousObject(this.intermediateText);
    }

    public TSqlNode getProcOrFunction() {
        return procOrFunction;
    }

    public void setProcOrFunction(TSqlNode procOrFunction) {
        this.procOrFunction = procOrFunction;
        setPreviousObject(this.procOrFunction);
    }

    public TExpression getProcOrFuncName() {
        return procOrFuncName;
    }

    public void setProcOrFuncName(TExpression procOrFuncName) {
        this.procOrFuncName = procOrFuncName;
        setPreviousObject(this.procOrFuncName);
    }

    public TSqlNode getProcStartBracket() {
        return procStartBracket;
    }

    public void setProcStartBracket(TSqlNode procStartBracket) {
        this.procStartBracket = procStartBracket;
        setPreviousObject(this.procStartBracket);
    }

    public TSqlNode getProcEndBracket() {
        return procEndBracket;
    }

    public void setProcEndBracket(TSqlNode procEndBracket) {
        this.procEndBracket = procEndBracket;
        setPreviousObject(this.procEndBracket);
    }

    public TExpression getCommonExpression() {
        return commonExpression;
    }

    public void setCommonExpression(TExpression commonExpression) {
        this.commonExpression = commonExpression;
        setPreviousObject(this.commonExpression);
    }

    public TExpression getIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(TExpression ifNotExists) {
        this.ifNotExists = ifNotExists;
        setPreviousObject(this.ifNotExists);
    }

}
